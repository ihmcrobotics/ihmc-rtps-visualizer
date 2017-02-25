package us.ihmc.rtps.visualizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import us.ihmc.pubsub.Domain;
import us.ihmc.pubsub.DomainFactory;
import us.ihmc.pubsub.DomainFactory.PubSubImplementation;
import us.ihmc.pubsub.attributes.Locator;
import us.ihmc.pubsub.attributes.ParticipantAttributes;
import us.ihmc.pubsub.attributes.ReaderQosHolder;
import us.ihmc.pubsub.attributes.TopicAttributes.TopicKind;
import us.ihmc.pubsub.attributes.WriterQosHolder;
import us.ihmc.pubsub.common.DiscoveryStatus;
import us.ihmc.pubsub.common.Guid;
import us.ihmc.pubsub.common.Time;
import us.ihmc.pubsub.participant.Participant;
import us.ihmc.pubsub.participant.ParticipantDiscoveryInfo;
import us.ihmc.pubsub.participant.ParticipantListener;
import us.ihmc.pubsub.participant.PublisherEndpointDiscoveryListener;
import us.ihmc.pubsub.participant.SubscriberEndpointDiscoveryListener;
import us.ihmc.rtps.impl.fastRTPS.ReaderQos;

public class IHMCRTPSParticipant
{
   
   private final ReentrantLock lock = new ReentrantLock();
   
   private final IHMCRTPSController controller;
   private final HashMap<Guid, ParticipantHolder> participants = new HashMap<>();
   private final HashMap<String, TopicHolder> topics = new HashMap<>();
   
   private class ParticipantListenerImpl implements ParticipantListener
   {

      @Override
      public void onParticipantDiscovery(Participant participant, ParticipantDiscoveryInfo info)
      {
         lock.lock();
         if(info.getStatus() == DiscoveryStatus.DISCOVERED_RTPSPARTICIPANT)
         {
            ParticipantHolder holder = getParticipant(info.getGuid());
            holder.setName(info.getName());
            lock.unlock();
         }
      }

   }
   
   private class PublisherEndpointDiscoveryListenerImpl implements PublisherEndpointDiscoveryListener
   {

      @Override
      public void publisherTopicChange(boolean isAlive, Guid guid, ArrayList<Locator> unicastLocatorList, ArrayList<Locator> multicastLocatorList,
                                       Guid participantGuid, String typeName, String topicName, int userDefinedId, long typeMaxSerialized, TopicKind topicKind,
                                       WriterQosHolder<?> writerQosHolder)
      {
         lock.lock();
         ParticipantHolder participantHolder = getParticipant(participantGuid);
         TopicHolder topicHolder = getTopic(topicName);
         topicHolder.addPublisher(guid, participantHolder, typeName);
         lock.unlock();
      }
      
   }
   
   private class SubscriberEndpointDiscoveryListenerImpl implements SubscriberEndpointDiscoveryListener
   {

      @Override
      public void subscriberTopicChange(boolean isAlive, Guid guid, boolean expectsInlineQos, ArrayList<Locator> unicastLocatorList,
                                        ArrayList<Locator> multicastLocatorList, Guid participantGuid, String typeName, String topicName, int userDefinedId,
                                        TopicKind javaTopicKind, ReaderQosHolder<ReaderQos> readerQosHolder)
      {
         lock.lock();
         ParticipantHolder participantHolder = getParticipant(participantGuid);
         TopicHolder topicHolder = getTopic(topicName);
         topicHolder.addSubscriber(guid, participantHolder, typeName);
         lock.unlock();
      }
      
   }
   
   private ParticipantHolder getParticipant(Guid guid)
   {
      if(!participants.containsKey(guid))
      {
         ParticipantHolder holder = new ParticipantHolder(guid);
         participants.put(guid, holder);
      }
      
      return participants.get(guid);
   }
   
   
   private TopicHolder getTopic(String name)
   {
      if(!topics.containsKey(name))
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
      
      Domain domain = DomainFactory.getDomain(PubSubImplementation.FAST_RTPS);
      
      ParticipantAttributes<?> attributes = domain.createParticipantAttributes();
      attributes.setDomainId(1);
      attributes.setLeaseDuration(Time.Infinite);
      attributes.setName("IHMCRTPSVisualizer");
      Participant participant = domain.createParticipant(attributes, new ParticipantListenerImpl());
      
      
      participant.registerEndpointDiscoveryListeners(new PublisherEndpointDiscoveryListenerImpl(), new SubscriberEndpointDiscoveryListenerImpl());

   }

}
