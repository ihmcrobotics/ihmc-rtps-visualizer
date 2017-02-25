package us.ihmc.rtps.visualizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import us.ihmc.pubsub.common.Guid;

public class TopicHolder extends TreeItem<String>
{
   private final String name;
   
   private final HashMap<String, TopicDataTypeHolder> topicTypes = new HashMap<>();
   
   
   public TopicHolder(String name)
   {
      super(name);
      this.name = name;
      setExpanded(true);
   }
   
   private TopicDataTypeHolder getHolder(String topicDataType)
   {
      if(!topicTypes.containsKey(topicDataType))
      {
         TopicDataTypeHolder value = new TopicDataTypeHolder(topicDataType);
         topicTypes.put(topicDataType, value);
         Platform.runLater(() -> this.getChildren().add(value));
      }
      return topicTypes.get(topicDataType);

   }
   
   public void addSubscriber(Guid guid, ParticipantHolder participantGuid, String topicDataType, SubscriberAttributesHolder attributes)
   {
      getHolder(topicDataType).addSubscriber(participantGuid, guid, attributes);
   }
   
   public void addPublisher(Guid guid, ParticipantHolder participantGuid, String topicDataType, PublisherAttributesHolder attributes)
   {
      getHolder(topicDataType).addPublisher(participantGuid, guid, attributes);
   }
   
   public void removeParticipant(ParticipantHolder participant)
   {
      for(Iterator<Entry<String, TopicDataTypeHolder>> it = topicTypes.entrySet().iterator(); it.hasNext(); ) 
      {
         Entry<String, TopicDataTypeHolder> entry = it.next();
         entry.getValue().removeParticipant(participant);
         
         if(entry.getValue().isEmpty())
         {
            Platform.runLater(() -> this.getChildren().remove(entry.getValue()));
            it.remove();
         }
      }
   }
   
   public boolean isEmpty()
   {
      return topicTypes.isEmpty();
   }

   @Override
   public String toString()
   {
      return name;
   }

}
