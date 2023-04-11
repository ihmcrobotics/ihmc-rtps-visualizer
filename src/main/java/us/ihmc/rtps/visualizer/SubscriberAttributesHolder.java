/**
 * Copyright 2017 Florida Institute for Human and Machine Cognition (IHMC)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package us.ihmc.rtps.visualizer;

import java.util.ArrayList;

import com.eprosima.xmlschemas.fastrtps_profiles.TopicKindType;
import javafx.scene.control.TreeItem;
import org.xml.sax.Locator;
import us.ihmc.pubsub.common.Guid;

public class SubscriberAttributesHolder extends TreeItem<String> implements TopicAttributesHolder
{
   private final String topicName;
   private final String topicType;

   public SubscriberAttributesHolder(Guid guid,
                                     boolean expectsInlineQos,
                                     ArrayList<Locator> unicastLocatorList,
                                     ArrayList<Locator> multicastLocatorList,
                                     Guid participantGuid,
                                     String typeName,
                                     String topicName,
                                     int userDefinedId,
                                     TopicKindType javaTopicKind)
   {
      super("Root");
      this.topicName = topicName;
      this.topicType = typeName;
      setExpanded(true);
      getChildren().add(new TreeItem<>("Topic name: " + topicName));
      getChildren().add(new TreeItem<>("Topic type: " + typeName));
      
      getChildren().add(new TreeItem<>("Guid: " + guid));
      getChildren().add(new TreeItem<>("Expects inline QoS: " +  expectsInlineQos));
      TreeItem<String> unicastTree = new TreeItem<>("Unicast locators");
      for(Locator locator : unicastLocatorList)
      {
         unicastTree.getChildren().add(new TreeItem<>(locator.toString()));
      }

      TreeItem<String> multicastTree = new TreeItem<>("Multicast locators");
      for(Locator locator : multicastLocatorList)
      {
         multicastTree.getChildren().add(new TreeItem<>(locator.toString()));
      }
      
      getChildren().add(unicastTree);
      getChildren().add(multicastTree);
      
      getChildren().add(new TreeItem<>("Participant Guid: " + participantGuid));
      getChildren().add(new TreeItem<>("User defined id: " + userDefinedId));
      getChildren().add(new TreeItem<>("Topic kind: " + javaTopicKind));
   }

   @Override
   public String getTopicName()
   {
      return topicName;
   }

   @Override
   public String getTopicType()
   {
      return topicType;
   }
}
