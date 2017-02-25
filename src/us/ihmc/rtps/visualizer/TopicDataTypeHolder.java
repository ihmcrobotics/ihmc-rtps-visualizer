package us.ihmc.rtps.visualizer;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import us.ihmc.pubsub.common.Guid;

public class TopicDataTypeHolder extends TreeItem<String>
{

   private final TreeItem<String> rootNode;
   private final HashMap<ParticipantHolder, TopicDataParticipantHolder> participants = new HashMap<>();

   public TopicDataTypeHolder(String name)
   {
      super(name);
      
      rootNode = new TreeItem<String>(name);
      rootNode.setExpanded(true);
      
      
   }

   private TopicDataParticipantHolder getHolder(ParticipantHolder holder)
   {
      if (!participants.containsKey(holder))
      {
         TopicDataParticipantHolder value = new TopicDataParticipantHolder(holder);
         participants.put(holder, value);
         rootNode.getChildren().add(value);
         
      }
      return participants.get(holder);
   }

   public void addSubscriber(ParticipantHolder participantGuid, Guid guid, SubscriberAttributesHolder attributes)
   {
      getHolder(participantGuid).addSubscriber(guid, attributes);
   }

   public void addPublisher(ParticipantHolder participantGuid, Guid guid, PublisherAttributesHolder attributes)
   {
      getHolder(participantGuid).addPublisher(guid, attributes);
   }

   public TreeItem<String> getRootNode()
   {
      return rootNode;
   }

   public void removeParticipant(ParticipantHolder participant)
   {
      TopicDataParticipantHolder holder = participants.remove(participant);
      if(holder != null)
      {
         Platform.runLater(() -> rootNode.getChildren().remove(holder));
      }
   }
   
   public boolean isEmpty()
   {
      return participants.isEmpty();
   }
}
