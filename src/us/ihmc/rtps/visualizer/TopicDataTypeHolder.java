package us.ihmc.rtps.visualizer;

import java.util.HashMap;

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

   public void addSubscriber(ParticipantHolder participantGuid, Guid guid)
   {
      getHolder(participantGuid).addSubscriber(guid);
   }

   public void addPublisher(ParticipantHolder participantGuid, Guid guid)
   {
      getHolder(participantGuid).addPublisher(guid);
   }

   public TreeItem<String> getRootNode()
   {
      return rootNode;
   }
}
