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

import org.apache.commons.io.HexDump;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;

public class HexStringMessage
{
   private String endianness;
   private byte[] data;
   
   
   public HexStringMessage()
   {
      
   }
   
   public void setData(String endianness, byte[] data)
   {
      this.endianness = endianness;
      this.data = data;
   }
   
   @Override 
   public String toString()
   {
      try
      {
         StringBuilderWriter writer = new StringBuilderWriter(data.length << 1 + 2);
         writer.write("Encapsulation: " );
         writer.write(endianness);
         writer.write(System.lineSeparator());
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
