package us.ihmc.rtps.visualizer;

import java.nio.ByteOrder;

import us.ihmc.pubsub.TopicDataType;

public class TopicDataTypeProvider
{
   public TopicDataType<?> getTopicDataType(String topicType)
   {
      return new HexStringTopicDataType(1024, topicType, ByteOrder.nativeOrder());
   }
}
