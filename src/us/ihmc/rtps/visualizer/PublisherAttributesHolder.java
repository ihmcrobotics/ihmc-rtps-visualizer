package us.ihmc.rtps.visualizer;

import java.util.ArrayList;

import javafx.scene.control.TreeItem;
import us.ihmc.pubsub.attributes.Locator;
import us.ihmc.pubsub.attributes.TopicAttributes.TopicKind;
import us.ihmc.pubsub.attributes.WriterQosHolder;
import us.ihmc.pubsub.common.Guid;

public class PublisherAttributesHolder extends TreeItem<String>
{
   public PublisherAttributesHolder(boolean isAlive, Guid guid, ArrayList<Locator> unicastLocatorList, ArrayList<Locator> multicastLocatorList,
                                    Guid participantGuid, String typeName, String topicName, int userDefinedId, long typeMaxSerialized, TopicKind topicKind,
                                    WriterQosHolder<?> writerQosHolder)
   {
      super("Root");
      setExpanded(true);
      getChildren().add(new TreeItem<>("Topic name: " + topicName));
      getChildren().add(new TreeItem<>("Topic type: " + typeName));
      getChildren().add(new TreeItem<>("Guid: " + guid));
      
      TreeItem<String> unicastTree = new TreeItem<String>("Unicast locators");
      for(Locator locator : unicastLocatorList)
      {
         unicastTree.getChildren().add(new TreeItem<String>(locator.toString()));
      }

      TreeItem<String> multicastTree = new TreeItem<String>("Multicast locators");
      for(Locator locator : multicastLocatorList)
      {
         multicastTree.getChildren().add(new TreeItem<String>(locator.toString()));
      }
      
      getChildren().add(unicastTree);
      getChildren().add(multicastTree);
      
      getChildren().add(new TreeItem<>("Participant Guid: " + participantGuid));
      getChildren().add(new TreeItem<>("User defined id: " + userDefinedId));
      getChildren().add(new TreeItem<>("Max serialized size: " + typeMaxSerialized));
      getChildren().add(new TreeItem<>("Topic kind: " + topicKind));
      
   }
}
