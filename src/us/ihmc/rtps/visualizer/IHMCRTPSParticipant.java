package us.ihmc.rtps.visualizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import us.ihmc.pubsub.Domain;
import us.ihmc.pubsub.DomainFactory;
import us.ihmc.pubsub.DomainFactory.PubSubImplementation;
import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.attributes.DurabilityKind;
import us.ihmc.pubsub.attributes.Locator;
import us.ihmc.pubsub.attributes.ParticipantAttributes;
import us.ihmc.pubsub.attributes.QosInterface;
import us.ihmc.pubsub.attributes.ReaderQosHolder;
import us.ihmc.pubsub.attributes.ReliabilityKind;
import us.ihmc.pubsub.attributes.SubscriberAttributes;
import us.ihmc.pubsub.attributes.TopicAttributes.TopicKind;
import us.ihmc.pubsub.attributes.WriterQosHolder;
import us.ihmc.pubsub.common.DiscoveryStatus;
import us.ihmc.pubsub.common.Guid;
import us.ihmc.pubsub.common.MatchingInfo;
import us.ihmc.pubsub.common.SampleInfo;
import us.ihmc.pubsub.common.Time;
import us.ihmc.pubsub.participant.Participant;
import us.ihmc.pubsub.participant.ParticipantDiscoveryInfo;
import us.ihmc.pubsub.participant.ParticipantListener;
import us.ihmc.pubsub.participant.PublisherEndpointDiscoveryListener;
import us.ihmc.pubsub.participant.SubscriberEndpointDiscoveryListener;
import us.ihmc.pubsub.subscriber.Subscriber;
import us.ihmc.pubsub.subscriber.SubscriberListener;

public class IHMCRTPSParticipant
{

   private final ReentrantLock lock = new ReentrantLock();
   private final ReentrantLock subScriberLock = new ReentrantLock();

   private final Guid myGUID;
   
   private final IHMCRTPSController controller;
   private final HashMap<Guid, ParticipantHolder> participants = new HashMap<>();
   private final HashMap<String, TopicHolder> topics = new HashMap<>();

   private Subscriber subscriber;

   private final Domain domain;

   private final Participant participant;
   
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
         try
         {
            if(subscriber.takeNextData(msg, info))
            {
               messageHolder = new MessageHolder(msg, info);
            }
            else
            {
               subScriberLock.unlock();
               return;
            }
         }
         catch (IOException e)
         {
            messageHolder = new MessageHolder(true, e.getMessage());
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
               for(Iterator<Entry<String, TopicHolder>> it = topics.entrySet().iterator(); it.hasNext(); ) 
               {
                  Entry<String, TopicHolder> entry = it.next();
                  entry.getValue().removeParticipant(removed);
                  
                  if(entry.getValue().isEmpty())
                  {
                     it.remove();
                     controller.removeTopic(entry.getValue());
                  }
               }
            }
         }
         lock.unlock();
      }

   }

   private class PublisherEndpointDiscoveryListenerImpl implements PublisherEndpointDiscoveryListener
   {

      @Override
      public void publisherTopicChange(boolean isAlive, Guid guid, ArrayList<Locator> unicastLocatorList, ArrayList<Locator> multicastLocatorList,
                                       Guid participantGuid, String typeName, String topicName, int userDefinedId, long typeMaxSerialized, TopicKind topicKind,
                                       WriterQosHolder<?> writerQosHolder)
      {
         if(participantGuid.equals(myGUID))
            return;
         
         lock.lock();
         ParticipantHolder participantHolder = getParticipant(participantGuid);
         TopicHolder topicHolder = getTopic(topicName);

         PublisherAttributesHolder attributes = new PublisherAttributesHolder(isAlive, guid, unicastLocatorList, multicastLocatorList, participantGuid,
                                                                              typeName, topicName, userDefinedId, typeMaxSerialized, topicKind,
                                                                              writerQosHolder);

         topicHolder.addPublisher(guid, participantHolder, attributes);
         lock.unlock();
      }

   }

   private class SubscriberEndpointDiscoveryListenerImpl implements SubscriberEndpointDiscoveryListener
   {

      @Override
      public void subscriberTopicChange(boolean isAlive, Guid guid, boolean expectsInlineQos, ArrayList<Locator> unicastLocatorList,
                                        ArrayList<Locator> multicastLocatorList, Guid participantGuid, String typeName, String topicName, int userDefinedId,
                                        TopicKind javaTopicKind, ReaderQosHolder<?> readerQosHolder)
      {
         if(participantGuid.equals(myGUID))
            return;
         
         lock.lock();
         ParticipantHolder participantHolder = getParticipant(participantGuid);
         TopicHolder topicHolder = getTopic(topicName);
         
         SubscriberAttributesHolder attributes = new SubscriberAttributesHolder(isAlive, guid, expectsInlineQos, unicastLocatorList, multicastLocatorList, participantGuid, typeName, topicName, userDefinedId, javaTopicKind, readerQosHolder);
         topicHolder.addSubscriber(guid, participantHolder, attributes);
         lock.unlock();
      }

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

   private TopicHolder getTopic(String name)
   {
      if (!topics.containsKey(name))
      {
         TopicHolder holder = new TopicHolder(name);
         topics.put(name, holder);
         controller.addTopic(holder);
      }

      return topics.get(name);
   }

   public IHMCRTPSParticipant(IHMCRTPSController controller) throws IOException
   {
      this.controller = controller;

      lock.lock();
      controller.setParticipant(this);
      domain = DomainFactory.getDomain(PubSubImplementation.FAST_RTPS);

      ParticipantAttributes<?> attributes = domain.createParticipantAttributes();
      attributes.setDomainId(1);
      attributes.setLeaseDuration(Time.Infinite);
      attributes.setName("IHMCRTPSVisualizer");
      participant = domain.createParticipant(attributes, new ParticipantListenerImpl());
      myGUID = participant.getGuid();
      participant.registerEndpointDiscoveryListeners(new PublisherEndpointDiscoveryListenerImpl(), new SubscriberEndpointDiscoveryListenerImpl());

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
      unSubscribeFromTopic();
      
      
      QosInterface topicQos = topicDataTypeHolder.getTopicQosHolder().getQosInterfaceForSubscriber();
      if(topicQos != null)
      {
         SubscriberAttributes<?, ?> attr = domain.createSubscriberAttributes();
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
         attr.getQos().setDurabilityKind(DurabilityKind.VOLATILE_DURABILITY_QOS); // Volatile always works
         attr.getQos().setOwnershipPolicyKind(topicQos.getOwnershipPolicyKind()); // Ownership needs to match
         
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
      topicDataTypeProvider.loadBundle(file);
   }

}
