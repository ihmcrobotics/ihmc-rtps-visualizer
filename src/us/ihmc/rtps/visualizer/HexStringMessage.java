package us.ihmc.rtps.visualizer;

import java.io.IOException;

import org.apache.commons.io.HexDump;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;

public class HexStringMessage
{
   private byte[] data;
   
   
   public HexStringMessage()
   {
      
   }
   
   public void setData(byte[] data)
   {
      this.data = data;
   }
   
   @Override 
   public String toString()
   {
      try
      {
         StringBuilderWriter writer = new StringBuilderWriter(data.length << 1 + 2);
         WriterOutputStream os = new WriterOutputStream(writer);
         HexDump.dump(data, 0, os, 0);
         os.close();
         return writer.toString();
      }
      catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | IOException e)
      {
         return e.getMessage();
      }

   }
}
