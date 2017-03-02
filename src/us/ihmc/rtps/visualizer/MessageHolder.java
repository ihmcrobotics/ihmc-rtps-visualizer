package us.ihmc.rtps.visualizer;

import java.io.StringWriter;
import java.util.Arrays;

import us.ihmc.pubsub.common.SampleInfo;

public class MessageHolder
{
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
   }

   public String getData()
   {
      if(valid)
      {
         StringBuilder builder = new StringBuilder();
         builder.append("Sample kind: ");
         builder.append(info.getSampleKind());
         builder.append('\n');

         builder.append("Ownership Strength: ");
         builder.append(info.getOwnershipStrength());
         builder.append('\n');

         builder.append("Source timestamp: ");
         builder.append(info.getSourceTimestamp());
         builder.append('\n');
         
         builder.append("Instance handle: ");
         builder.append(Arrays.toString(info.getInstanceHandle().getValue()));
         builder.append('\n');
         
         builder.append("Sample identity: ");
         builder.append("\n\tSequence number: ");
         builder.append(info.getSampleIdentity().getSequenceNumber().getHigh());
         builder.append('.');
         builder.append(info.getSampleIdentity().getSequenceNumber().getLow());
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
   
   @Override
   public String toString()
   {
      if (valid)
      {
         return "[" + info.getSourceTimestamp() + "] " + info.getSampleKind();
      }
      else
      {
         return "<Corrupted Message>";
      }
   }
}
