package us.ihmc.rtps.visualizer;

import java.util.HashMap;

import javafx.scene.control.TreeItem;
import us.ihmc.pubsub.common.Guid;

public class TopicDataParticipantHolder extends TreeItem<String>
{
   private final HashMap<Guid, TreeItem<String>> publishers = new HashMap<>();
   private final HashMap<Guid, TreeItem<String>> subscribers = new HashMap<>();
   
   public TopicDataParticipantHolder(ParticipantHolder participant)
   {
      super(participant.getName() + " " + participant.getGuid());
      setExpanded(true);
   }

   public void addSubscriber(Guid guid)
   {
      TreeItem<String> leaf = new TreeItem<String>("Sub: " + guid.toString());
      subscribers.put(guid, leaf);
      getChildren().add(leaf);
   }

   public void addPublisher(Guid guid)
   {
      TreeItem<String> leaf = new TreeItem<String>("Pub " + guid.toString());
      publishers.put(guid, leaf);
      getChildren().add(leaf);
   }
   
   
}
