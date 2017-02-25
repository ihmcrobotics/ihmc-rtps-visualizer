package us.ihmc.rtps.visualizer;

import java.io.IOException;
import java.nio.ByteOrder;

import org.apache.commons.lang3.NotImplementedException;

import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.common.SerializedPayload;

public class HexStringTopicDataType implements TopicDataType<StringBuilder>
{
   private final int maxSize;
   private final String name;
   private final ByteOrder order;

   public HexStringTopicDataType(int maxSize, String name, ByteOrder order)
   {
      this.name = name;
      this.maxSize = maxSize;
      this.order = order;
   }

   @Override
   public void serialize(StringBuilder data, SerializedPayload serializedPayload) throws IOException
   {
      throw new NotImplementedException("Serialize is not implemented for this message type");
   }

   @Override
   public void deserialize(SerializedPayload serializedPayload, StringBuilder data) throws IOException
   {
      data.setLength(0);
      int length = Math.min(serializedPayload.getLength(), maxSize);

      for (int i = 0; i < length; i++)
      {
         data.append(String.format("%02x", serializedPayload.getData().get()));
      }
      if (maxSize < serializedPayload.getLength())
      {
         data.append("[Truncated]");
      }
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
   public StringBuilder createData()
   {
      return new StringBuilder(maxSize);
   }
}
