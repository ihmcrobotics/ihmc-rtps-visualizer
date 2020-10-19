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
