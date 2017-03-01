package us.ihmc.rtps.visualizer;

import us.ihmc.pubsub.attributes.QosInterface;

public interface TopicAttributesHolder
{

   String getTopicName();

   String getTopicType();

   QosInterface getQosInterface();

}