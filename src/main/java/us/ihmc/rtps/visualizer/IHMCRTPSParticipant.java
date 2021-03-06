/**
 * Copyright 2017 Florida Institute for Human and Machine Cognition (IHMC)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.ihmc.rtps.visualizer;

import us.ihmc.log.LogTools;
import us.ihmc.pubsub.Domain;
import us.ihmc.pubsub.DomainFactory;
import us.ihmc.pubsub.DomainFactory.PubSubImplementation;
import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.attributes.*;
import us.ihmc.pubsub.attributes.TopicAttributes.TopicKind;
import us.ihmc.pubsub.common.*;
import us.ihmc.pubsub.participant.*;
import us.ihmc.pubsub.subscriber.Subscriber;
import us.ihmc.pubsub.subscriber.SubscriberListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class IHMCRTPSParticipant
{
   private final ReentrantLock lock = new ReentrantLock();
   private final ReentrantLock subScriberLock = new ReentrantLock();

   private Guid myGUID;
   
   private final IHMCRTPSController controller;
   private final HashMap<Guid, ParticipantHolder> participants = new HashMap<>();
   private final PartitionHolder defaultPartition = new PartitionHolder();
   private final HashMap<String, PartitionHolder> partitions = new HashMap<>();

   private Subscriber subscriber;
   private final Domain domain;
   private Participant participant;
   private final TopicDataTypeProvider topicDataTypeProvider = new TopicDataTypeProvider();
   
   private class SubscriberListenerImpl implements SubscriberListener
   {
      private final TopicDataType<?> topicDataType;
      
      private SubscriberListenerImpl(TopicDataType<?> topicDataType)
      {
         this.topicDataType = topicDataType;
      }

      @Override
      public void onNewDataMessage(Subscriber subscriber)
      {
         subScriberLock.lock();
         Object msg = topicDataType.createData();
         SampleInfo info = new SampleInfo();
         MessageHolder messageHolder;
         if (subscriber.takeNextData(msg, info))
         {
            messageHolder = new MessageHolder(msg, info);
         }
         else
         {
            subScriberLock.unlock();
            return;
         }

         controller.updateDataList(messageHolder);
         
         subScriberLock.unlock();
      }

      @Override
      public void onSubscriptionMatched(Subscriber subscriber, MatchingInfo info)
      {
      }
   }
   
   private class ParticipantListenerImpl implements ParticipantListener
   {
      @Override
      public void onParticipantDiscovery(Participant participant, ParticipantDiscoveryInfo info)
      {
         if(info.getGuid().equals(myGUID))
            return;
         lock.lock();
         if (info.getStatus() == DiscoveryStatus.DISCOVERED_RTPSPARTICIPANT)
         {
            ParticipantHolder holder = getParticipant(info.getGuid());
            holder.setName(info.getName());
         }
         else if (info.getStatus() == DiscoveryStatus.REMOVED_RTPSPARTICIPANT)
         {
            ParticipantHolder removed = participants.remove(info.getGuid());
            if(removed != null)
            {
               removeParticipant(removed);
            }
         }
         lock.unlock();
      }
   }

   private class PublisherEndpointDiscoveryListenerImpl implements PublisherEndpointDiscoveryListener
   {
      @Override
      public void publisherTopicChange(boolean isAlive,
                                       Guid guid,
                                       ArrayList<Locator> unicastLocatorList,
                                       ArrayList<Locator> multicastLocatorList,
                                       Guid participantGuid,
                                       String typeName,
                                       String topicName,
                                       int userDefinedId,
                                       long typeMaxSerialized,
                                       TopicKind topicKind,
                                       WriterQosHolder writerQosHolder)
      {
         if(participantGuid.equals(myGUID))
            return;
         
         lock.lock();
         ParticipantHolder participantHolder = getParticipant(participantGuid);
         PublisherAttributesHolder attributes = new PublisherAttributesHolder(isAlive,
                                                                              guid,
                                                                              unicastLocatorList,
                                                                              multicastLocatorList,
                                                                              participantGuid,
                                                                              typeName,
                                                                              topicName,
                                                                              userDefinedId,
                                                                              typeMaxSerialized,
                                                                              topicKind,
                                                                              writerQosHolder);
         
         List<String> topicPartitions = writerQosHolder.getPartitions();
         if(topicPartitions.isEmpty())
         {
            defaultPartition.addPublisher(guid, participantHolder, attributes);
         }
         
         for(String partition : topicPartitions)
         {
            PartitionHolder partitionHolder = getPartition(partition);
            partitionHolder.addPublisher(guid, participantHolder, attributes);
         }
         lock.unlock();
      }
   }

   private class SubscriberEndpointDiscoveryListenerImpl implements SubscriberEndpointDiscoveryListener
   {
      @Override
      public void subscriberTopicChange(boolean isAlive,
                                        Guid guid,
                                        boolean expectsInlineQos,
                                        ArrayList<Locator> unicastLocatorList,
                                        ArrayList<Locator> multicastLocatorList,
                                        Guid participantGuid,
                                        String typeName,
                                        String topicName,
                                        int userDefinedId,
                                        TopicKind javaTopicKind,
                                        ReaderQosHolder readerQosHolder)
      {
         if(participantGuid.equals(myGUID))
            return;
         
         lock.lock();
         ParticipantHolder participantHolder = getParticipant(participantGuid);
         SubscriberAttributesHolder attributes = new SubscriberAttributesHolder(isAlive,
                                                                                guid,
                                                                                expectsInlineQos,
                                                                                unicastLocatorList,
                                                                                multicastLocatorList,
                                                                                participantGuid,
                                                                                typeName,
                                                                                topicName,
                                                                                userDefinedId,
                                                                                javaTopicKind,
                                                                                readerQosHolder);
         
         List<String> topicPartitions = readerQosHolder.getPartitions();
         if(topicPartitions.isEmpty())
         {
            defaultPartition.addSubscriber(guid, participantHolder, attributes);
         }
         else
         {
            for(String partition : topicPartitions)
            {
               PartitionHolder partitionHolder = getPartition(partition);
               partitionHolder.addSubscriber(guid, participantHolder, attributes);
            }
         }
         lock.unlock();
      }
   }

   private void removeParticipant(ParticipantHolder removed)
   {
      for(Iterator<Entry<String, PartitionHolder>> it = partitions.entrySet().iterator(); it.hasNext(); ) 
      {
         Entry<String, PartitionHolder> entry = it.next();
         entry.getValue().removeParticipant(removed);
         
         if(entry.getValue().isEmpty())
         {
            it.remove();
            controller.removePartition(entry.getValue());
         }
      }
      
      defaultPartition.removeParticipant(removed);
   }
   
   private ParticipantHolder getParticipant(Guid guid)
   {
      if (!participants.containsKey(guid))
      {
         ParticipantHolder holder = new ParticipantHolder(guid);
         participants.put(guid, holder);
      }

      return participants.get(guid);
   }
   
   private PartitionHolder getPartition(String name)
   {
      if(!partitions.containsKey(name))
      {
         PartitionHolder holder = new PartitionHolder(name);
         partitions.put(name, holder);
         controller.addPartition(holder);
      }
      return partitions.get(name);
   }

   public IHMCRTPSParticipant(IHMCRTPSController controller) throws IOException
   {
      this.controller = controller;
      domain = DomainFactory.getDomain(PubSubImplementation.FAST_RTPS);
      controller.setParticipant(this);
      controller.addPartition(defaultPartition);
   }
   
   public void connect(int domainID) throws IOException
   {
      lock.lock();
      
      ParticipantAttributes attributes = domain.createParticipantAttributes();
      attributes.setDomainId(domainID);
      attributes.setLeaseDuration(Time.Infinite);
      attributes.setName("IHMCRTPSVisualizer");
      participant = domain.createParticipant(attributes, new ParticipantListenerImpl());
      myGUID = participant.getGuid();
      participant.registerEndpointDiscoveryListeners(new PublisherEndpointDiscoveryListenerImpl(), new SubscriberEndpointDiscoveryListenerImpl());
      
      lock.unlock();
   }
   
   public void disconnect()
   {
      lock.lock();
      subScriberLock.lock();
      
      unSubscribeFromTopic();
      
      for(ParticipantHolder removed : participants.values())
      {
         removeParticipant(removed);
      }
      
      domain.removeParticipant(participant);
      participant = null;
      myGUID = null;
      subScriberLock.unlock();
      lock.unlock();
   }

   public void unSubscribeFromTopic()
   {
      subScriberLock.lock();
      if(subscriber != null)
      {
         domain.removeSubscriber(subscriber);
         controller.clearDataList();
      }
      subScriberLock.unlock();
   }

   public void subscribeToTopic(TopicDataTypeHolder topicDataTypeHolder)
   {
      subScriberLock.lock();
      
      if(participant == null)
      {
         subScriberLock.unlock();
         return;
      }
      
      unSubscribeFromTopic();
      
      QosInterface topicQos = topicDataTypeHolder.getTopicQosHolder().getQosInterfaceForSubscriber();
      if(topicQos != null)
      {
         SubscriberAttributes attr = domain.createSubscriberAttributes();
         attr.getTopic().setTopicKind(TopicKind.NO_KEY);
         attr.getTopic().setTopicDataType(topicDataTypeHolder.getTopicDataType());
         attr.getTopic().setTopicName(topicDataTypeHolder.getTopicName());

         if(topicQos.getReliabilityKind() == ReliabilityKind.RELIABLE)
         {
            attr.getQos().setReliabilityKind(ReliabilityKind.RELIABLE);
         }
         else
         {
            attr.getQos().setReliabilityKind(ReliabilityKind.BEST_EFFORT); // Best effort always works
         }
         if(topicQos.getDurabilityKind() == DurabilityKind.VOLATILE_DURABILITY_QOS)
         {
            attr.getQos().setDurabilityKind(DurabilityKind.VOLATILE_DURABILITY_QOS); // Volatile always works            
         }
         else
         {
            attr.getQos().setDurabilityKind(DurabilityKind.TRANSIENT_LOCAL_DURABILITY_QOS);
         }
         
         attr.getQos().setOwnershipPolicyKind(topicQos.getOwnershipPolicyKind()); // Ownership needs to match
         
         if(topicDataTypeHolder.getPartition() != null)
         {
            attr.getQos().addPartition(topicDataTypeHolder.getPartition());
         }
         
         TopicDataType<?> topicDataType = domain.getRegisteredType(participant, topicDataTypeHolder.getTopicDataType()); 
         if(topicDataType == null)
         {
            topicDataType = topicDataTypeProvider.getTopicDataType(topicDataTypeHolder.getTopicDataType());
            domain.registerType(participant, topicDataType);
         }
         
         try
         {
            subscriber = domain.createSubscriber(participant, attr, new SubscriberListenerImpl(topicDataType));
         }
         catch (IllegalArgumentException | IOException e)
         {
            e.printStackTrace();
         }
      }
      
      subScriberLock.unlock();
   }

   public void loadBundle(File file)
   {
      List<String> registered = topicDataTypeProvider.loadBundle(file);
      subScriberLock.lock();
      for(String topicType : registered)
      {
         TopicDataType<?> topicDataType = domain.getRegisteredType(participant, topicType);
         if(topicDataType != null)
         {
            try
            {
               domain.unregisterType(participant, topicType);
            }
            catch (IOException e)
            {
               LogTools.error("Cannot unregister type {}", topicType);
            }
         }
      }
      subScriberLock.unlock();
   }
}
