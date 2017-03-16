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
   private final String partition;
   
   private final HashMap<String, TopicDataTypeHolder> topicTypes = new HashMap<>();
   
   
   public TopicHolder(String name, String partition)
   {
      super(name);
      this.name = name;
      this.partition = partition;
      setExpanded(true);
   }
   
   private TopicDataTypeHolder getHolder(TopicAttributesHolder attributesHolder)
   {
      if(!topicTypes.containsKey(attributesHolder.getTopicType()))
      {
         TopicDataTypeHolder value = new TopicDataTypeHolder(attributesHolder.getTopicType(), attributesHolder.getTopicName(), attributesHolder.getTopicType(), partition);
         topicTypes.put(attributesHolder.getTopicType(), value);
         Platform.runLater(() -> this.getChildren().add(value));
      }
      return topicTypes.get(attributesHolder.getTopicType());

   }
   
   public void addSubscriber(Guid guid, ParticipantHolder participantGuid, SubscriberAttributesHolder attributes)
   {
      getHolder(attributes).addSubscriber(participantGuid, guid, attributes);
   }
   
   public void addPublisher(Guid guid, ParticipantHolder participantGuid, PublisherAttributesHolder attributes)
   {
      getHolder(attributes).addPublisher(participantGuid, guid, attributes);
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
