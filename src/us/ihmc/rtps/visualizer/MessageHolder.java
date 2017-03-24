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

import java.util.Arrays;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import us.ihmc.pubsub.common.SampleInfo;

public class MessageHolder
{
   private final SimpleStringProperty timestamp = new SimpleStringProperty();
   private final SimpleLongProperty sequenceNumber = new SimpleLongProperty();
   private final SimpleIntegerProperty bytes = new SimpleIntegerProperty();
   private final SimpleStringProperty changeKind = new SimpleStringProperty();
   
   
   private final boolean valid;
   private final String stackTrace;
   private final Object msg;
   private final SampleInfo info;

   public MessageHolder(Object msg, SampleInfo info)
   {
      this.msg = msg;
      this.info = info;
      this.valid = true;
      stackTrace = "";
      
      this.timestamp.set(info.getSourceTimestamp().toString());
      this.sequenceNumber.set(info.getSampleIdentity().getSequenceNumber().get());
      this.bytes.set(info.getDataLength());
      this.changeKind.set(info.getSampleKind().toString());
   }

   public MessageHolder(boolean invalid, String stackTrace)
   {
      super();
      if (!invalid)
      {
         throw new RuntimeException();
      }
      this.msg = null;
      this.info = null;
      this.valid = false;
      this.stackTrace = stackTrace;
      
      this.timestamp.set("<Corrupted message>");      
      
   }

   public String getData()
   {
      if(valid)
      {
         StringBuilder builder = new StringBuilder();
         builder.append("Sample kind: ");
         builder.append(info.getSampleKind());
         builder.append('\n');

         builder.append("Data length: ");
         builder.append(info.getDataLength());
         builder.append('\n');
         
         builder.append("Ownership Strength: ");
         builder.append(info.getOwnershipStrength());
         builder.append('\n');

         builder.append("Source timestamp: ");
         builder.append(info.getSourceTimestamp());
         builder.append('\n');
         
         builder.append("Key: ");
         if(info.getInstanceHandle().isDefined())
         {
            builder.append(Arrays.toString(info.getInstanceHandle().getValue()));
         }
         else
         {
            builder.append("NO_KEY");
         }
         builder.append('\n');
         
         builder.append("Sample identity: ");
         builder.append("\n\tSequence number: ");
         builder.append(info.getSampleIdentity().getSequenceNumber().get());
         builder.append("\n\tGUID: ");
         builder.append(info.getSampleIdentity().getGuid());
         builder.append('\n');
         
         builder.append("Related sample identity: ");
         builder.append("\n\tSequence number: ");
         builder.append(info.getRelatedSampleIdentity().getSequenceNumber().getHigh());
         builder.append('.');
         builder.append(info.getRelatedSampleIdentity().getSequenceNumber().getLow());
         builder.append("\n\tGUID: ");
         builder.append(info.getRelatedSampleIdentity().getGuid());
         builder.append('\n');
         
         builder.append("Data: \n");
         builder.append(msg.toString());
         
         return builder.toString();         
      }
      else
      {
         return stackTrace;
      }
   }

   public String getTimestamp()
   {
      return timestamp.get();
   }

   public long getSequenceNumber()
   {
      return sequenceNumber.get();
   }

   public int getBytes()
   {
      return bytes.get();
   }

   public String getChangeKind()
   {
      return changeKind.get();
   }
   
   
   
}
