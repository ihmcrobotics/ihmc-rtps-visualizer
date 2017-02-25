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

   public void addSubscriber(Guid guid, SubscriberAttributesHolder attributes)
   {
      TreeItem<String> leaf = new EndpointHolder("Sub: " + guid.toString(), attributes);
      subscribers.put(guid, leaf);
      getChildren().add(leaf);
   }

   public void addPublisher(Guid guid, PublisherAttributesHolder attributes)
   {
      TreeItem<String> leaf = new EndpointHolder("Pub: " + guid.toString(), attributes);
      publishers.put(guid, leaf);
      getChildren().add(leaf);
   }
   
   
}
