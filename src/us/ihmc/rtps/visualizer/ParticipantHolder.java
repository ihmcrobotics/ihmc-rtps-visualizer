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

import us.ihmc.pubsub.common.Guid;

public class ParticipantHolder
{
   private final Guid guid;
   private String name = "";
   
   public ParticipantHolder(Guid guid)
   {
      this.guid = guid;
      this.name = guid.toString();
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   public String getName()
   {
      return name;
   }

   @Override
   public int hashCode()
   {
      return guid.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      return guid.equals(obj);
   }

   public Guid getGuid()
   {
      return guid;
   }
}
