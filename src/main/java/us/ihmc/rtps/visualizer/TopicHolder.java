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
   
   private TopicDataTypeHolder getHolder(TopicAttributesHolder attributesHolder)
   {
      if(!topicTypes.containsKey(attributesHolder.getTopicType()))
      {
         TopicDataTypeHolder value = new TopicDataTypeHolder(attributesHolder.getTopicType(), attributesHolder.getTopicType());
         topicTypes.put(attributesHolder.getTopicType(), value);
         Platform.runLater(() -> this.getChildren().add(value));
      }
      return topicTypes.get(attributesHolder.getTopicType());
   }
   
   public void addSubscriber(Guid guid, ParticipantHolder participantGuid, SubscriberAttributesHolder attributes)
   {
      getHolder(attributes).addSubscriber(participantGuid, guid, attributes);
   }
   
   public void addPublisher(Guid guid, ParticipantHolder participantGuid, PublisherAttributesHolder attributes)
   {
      getHolder(attributes).addPublisher(participantGuid, guid, attributes);
   }
   
   public void removeParticipant(ParticipantHolder participant)
   {
      for(Iterator<Entry<String, TopicDataTypeHolder>> it = topicTypes.entrySet().iterator(); it.hasNext(); ) 
      {
         Entry<String, TopicDataTypeHolder> entry = it.next();
         entry.getValue().removeParticipant(participant);
         
         if(entry.getValue().isEmpty())
         {
            Platform.runLater(() -> this.getChildren().remove(entry.getValue()));
            it.remove();
         }
      }
   }
   
   public boolean isEmpty()
   {
      return topicTypes.isEmpty();
   }

   @Override
   public String toString()
   {
      return name;
   }
}
