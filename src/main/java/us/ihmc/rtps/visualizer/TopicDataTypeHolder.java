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

import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import us.ihmc.pubsub.common.Guid;

public class TopicDataTypeHolder extends TreeItem<String>
{
   private final TreeItem<String> rootNode;
   private final HashMap<ParticipantHolder, TopicDataParticipantHolder> participants = new HashMap<>();

   private final String topicDataType;

   public TopicDataTypeHolder(String name, String topicDataType)
   {
      super(name);
      
      rootNode = new TreeItem<>(name);
      rootNode.setExpanded(true);

      this.topicDataType = topicDataType;
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

   public String getTopicDataType()
   {
      return topicDataType;
   }
}
