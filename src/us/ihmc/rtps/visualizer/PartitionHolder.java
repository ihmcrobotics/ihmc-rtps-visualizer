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
import java.util.Iterator;
import java.util.Map.Entry;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import us.ihmc.pubsub.common.Guid;

public class PartitionHolder extends TreeItem<String>
{
   private static final String DEFAULT_PARTITION = "[Default partition]";
   private final String displayName;
   private final String name;
   private final HashMap<String, TopicHolder> topics = new HashMap<>();

   public PartitionHolder(String name)
   {
      super(name);
      this.name = name;
      this.displayName = name;
      setExpanded(true);
   }

   public PartitionHolder()
   {
      super(DEFAULT_PARTITION);
      this.displayName = DEFAULT_PARTITION;
      this.name = null;
      setExpanded(true);
   }

   private TopicHolder getHolder(String topicName)
   {
      if (!topics.containsKey(topicName))
      {

         TopicHolder value = new TopicHolder(topicName, name);
         topics.put(topicName, value);
         Platform.runLater(() -> this.getChildren().add(value));
      }
      return topics.get(topicName);
   }

   public void addPublisher(Guid guid, ParticipantHolder participantHolder, PublisherAttributesHolder attributes)
   {
      TopicHolder holder = getHolder(attributes.getTopicName());
      holder.addPublisher(guid, participantHolder, attributes);
   }

   public void addSubscriber(Guid guid, ParticipantHolder participantHolder, SubscriberAttributesHolder attributes)
   {
      TopicHolder holder = getHolder(attributes.getTopicName());
      holder.addSubscriber(guid, participantHolder, attributes);
   }

   public void removeParticipant(ParticipantHolder participant)
   {
      for (Iterator<Entry<String, TopicHolder>> it = topics.entrySet().iterator(); it.hasNext();)
      {
         Entry<String, TopicHolder> entry = it.next();
         entry.getValue().removeParticipant(participant);

         if (entry.getValue().isEmpty())
         {
            Platform.runLater(() -> this.getChildren().remove(entry.getValue()));
            it.remove();
         }
      }
   }

   public boolean isEmpty()
   {
      return topics.isEmpty();
   }

   @Override
   public String toString()
   {
      return displayName;
   }
}
