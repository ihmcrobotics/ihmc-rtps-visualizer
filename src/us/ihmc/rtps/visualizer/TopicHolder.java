package us.ihmc.rtps.visualizer;

import java.util.HashMap;

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
   
   public void addSubscriber(Guid guid, ParticipantHolder participantGuid, String topicDataType)
   {
      getHolder(topicDataType).addSubscriber(participantGuid, guid);
   }
   
   public void addPublisher(Guid guid, ParticipantHolder participantGuid, String topicDataType)
   {
      getHolder(topicDataType).addPublisher(participantGuid, guid);
   }

   @Override
   public String toString()
   {
      return name;
   }

}
