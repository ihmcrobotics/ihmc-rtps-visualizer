package us.ihmc.rtps.visualizer;

import java.util.ArrayList;

import us.ihmc.pubsub.attributes.DurabilityKind;
import us.ihmc.pubsub.attributes.QosInterface;
import us.ihmc.pubsub.attributes.ReliabilityKind;

public class TopicQosHolder
{
   private static final String DEFAULT_MSG = "Qos policy valid";
   private final ArrayList<QosInterface> writerQos = new ArrayList<>();
   private final ArrayList<QosInterface> readerQos = new ArrayList<>();
   
   
   
   private String error = DEFAULT_MSG;
   
   public synchronized boolean update(QosInterface qosInterface)
   {
      if(qosInterface.isWriter())
      {
         writerQos.add(qosInterface);
      }
      else
      {
         readerQos.add(qosInterface);
      }
      
      
      for(QosInterface wQos : writerQos)
      {
         for(QosInterface rQos : readerQos)
         {
            if(wQos.getReliabilityKind() == ReliabilityKind.BEST_EFFORT && rQos.getReliabilityKind() == ReliabilityKind.RELIABLE)
            {
               error = "Incompatible QoS: Publisher has BEST_EFFORT reliablity but subscriber is RELIABLE";
               return false;
            }
            
            if(rQos.getDurabilityKind() == DurabilityKind.TRANSIENT_LOCAL_DURABILITY_QOS && wQos.getDurabilityKind() == DurabilityKind.VOLATILE_DURABILITY_QOS)
            {
               error = "Incompatible QoS: Publisher has VOLATILE_DURABILITY_QOS durability but subscriber is TRANSIENT_LOCAL_DURABILITY_QOS";
               return false;
            }
            if(wQos.getOwnershipPolicyKind() != rQos.getOwnershipPolicyKind())
            {
               error = "Incompatible QoS: Publisher has a different ownership policy kind than the subscriber";
               return false;
            }
         }
      }
      
      error = DEFAULT_MSG;
      return true;
   }
   
   public String getError()
   {
      return error;
   }
   
   public QosInterface getQosInterfaceForSubscriber()
   {
      if(writerQos.size() > 0)
      {
         return writerQos.get(0);
      }
      
      else if(readerQos.size() > 0)
      {
         return readerQos.get(0);
      }
      
      else 
      {
         return null;
      }
   }
}
