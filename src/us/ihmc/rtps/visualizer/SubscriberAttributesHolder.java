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

import javafx.scene.control.TreeItem;
import us.ihmc.pubsub.attributes.Locator;
import us.ihmc.pubsub.attributes.ReaderQosHolder;
import us.ihmc.pubsub.attributes.TopicAttributes.TopicKind;
import us.ihmc.pubsub.common.Guid;

public class SubscriberAttributesHolder extends TreeItem<String> implements TopicAttributesHolder
{
   private final String topicName;
   private final String topicType;
   private final ReaderQosHolder readerQosHolder;

   public SubscriberAttributesHolder(boolean isAlive, Guid guid, boolean expectsInlineQos, ArrayList<Locator> unicastLocatorList,
                                     ArrayList<Locator> multicastLocatorList, Guid participantGuid, String typeName, String topicName, int userDefinedId,
                                     TopicKind javaTopicKind, ReaderQosHolder readerQosHolder)
   {
      super("Root");
      this.topicName = topicName;
      this.topicType = typeName;
      this.readerQosHolder = readerQosHolder;
      setExpanded(true);
      getChildren().add(new TreeItem<>("Topic name: " + topicName));
      getChildren().add(new TreeItem<>("Topic type: " + typeName));
      
      getChildren().add(new TreeItem<>("Guid: " + guid));
      getChildren().add(new TreeItem<>("Expects inline QoS: " +  expectsInlineQos));
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
      getChildren().add(new TreeItem<>("Topic kind: " + javaTopicKind));
      
      TreeItem<String> qos = new TreeItem<>("QoS");
      qos.getChildren().add(new TreeItem<>("Reliability: " + readerQosHolder.getReliabilityKind()));
      qos.getChildren().add(new TreeItem<>("Partitions: " + readerQosHolder.getPartitions()));
      qos.setExpanded(true);
      getChildren().add(qos);
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

   @Override
   public ReaderQosHolder getQosInterface()
   {
      return readerQosHolder;
   }
}
