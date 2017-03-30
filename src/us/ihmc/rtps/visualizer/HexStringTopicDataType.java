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

import java.io.IOException;
import java.nio.ByteOrder;
import java.rmi.ServerError;

import org.apache.commons.lang3.NotImplementedException;

import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.common.SerializedPayload;

public class HexStringTopicDataType implements TopicDataType<HexStringMessage>
{
   private final int maxSize;
   private final String name;
   
   

   public HexStringTopicDataType(int maxSize, String name)
   {
      this.name = name;
      this.maxSize = maxSize;
   }

   @Override
   public void serialize(HexStringMessage data, SerializedPayload serializedPayload) throws IOException
   {
      throw new NotImplementedException("Serialize is not implemented for this message type");
   }

   @Override
   public void deserialize(SerializedPayload serializedPayload, HexStringMessage data) throws IOException
   {
      int length = Math.min(serializedPayload.getLength() - 4, maxSize);
      byte[] dataArray = new byte[length];
      serializedPayload.getData().get();
      short encapsulation = serializedPayload.getData().get();
      serializedPayload.getData().getShort();
      
      
      String endianness = "";
      switch(encapsulation)
      {
      case SerializedPayload.CDR_BE:
         endianness = "CDR_BE";
         break;
      case SerializedPayload.CDR_LE:
         endianness = "CDR_LE";
         break;
      case SerializedPayload.PL_CDR_BE:
         endianness = "PL_CDR_BE";
         break;
      case SerializedPayload.PL_CDR_LE:
         endianness = "PL_CDR_LE";
         break;
      }
      
      serializedPayload.getData().get(dataArray);
      data.setData(endianness, dataArray);
      
   }

   @Override
   public int getTypeSize()
   {
      return maxSize;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public HexStringMessage createData()
   {
      return new HexStringMessage();
   }

   @Override
   public TopicDataType<HexStringMessage> newInstance()
   {
      return new HexStringTopicDataType(maxSize, name);
   }
}
