package us.ihmc.rtps.visualizer;

import java.io.IOException;
import java.nio.ByteOrder;

import org.apache.commons.lang3.NotImplementedException;

import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.common.SerializedPayload;

public class HexStringTopicDataType implements TopicDataType<HexStringMessage>
{
   private final int maxSize;
   private final String name;
   

   public HexStringTopicDataType(int maxSize, String name, ByteOrder order)
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
      int length = Math.min(serializedPayload.getLength(), maxSize);
      byte[] dataArray = new byte[length];
      serializedPayload.getData().get(dataArray);
      data.setData(dataArray);
      
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
}
