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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import us.ihmc.pubsub.TopicDataType;

public class TopicDataTypeProvider
{
   private static final int HEX_STRING_MAX_SIZE = 66560;
   private final HashMap<String, TopicDataType<?>> topicDataTypes = new HashMap<>();
   
   public TopicDataTypeProvider()
   {
      String bundles = System.getProperty("dataTypeBundles");
      if(bundles != null)
      {
         String[] bundleArray = bundles.split(";");
         for(String bundle : bundleArray)
         {
            File bundleFile = new File(bundle);
            if(bundleFile.exists())
            {
               System.out.println("Loading topic data type bundle " + bundle);
               loadBundle(bundleFile);
            }
            else
            {
               System.err.println("Cannot find topic data type bundle " + bundle);
            }
         }
      }
   }

   public List<String> loadBundle(File bundle)
   {
      ArrayList<String> registeredTopics = new ArrayList<>();
      try
      {
         ArrayList<String> classesInBundle = new ArrayList<>();
         JarFile file = new JarFile(bundle);
         Enumeration<JarEntry> files = file.entries();
         while(files.hasMoreElements())
         {
            JarEntry next = files.nextElement();
            if(!next.isDirectory())
            {
               String name = next.getName();
               if(name.endsWith(".class"))
               {
                  
                  String clazz = name.substring(0, name.length() - 6);
                  clazz = clazz.replace('/', '.');
                  clazz = clazz.replace(File.separatorChar, '.');
                  classesInBundle.add(clazz);
               }
            }
         }
         file.close();
         
         URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {bundle.toURI().toURL()});
         for(String className : classesInBundle)
         {
            try
            {
               Class<?> clazz = classLoader.loadClass(className);
               if(TopicDataType.class.isAssignableFrom(clazz) && !TopicDataType.class.equals(clazz))
               {
                  TopicDataType<?> dataType = (TopicDataType<?>) clazz.newInstance();
                  topicDataTypes.put(dataType.getName(), dataType);
                  registeredTopics.add(dataType.getName());
                  System.out.println("Registered topic data type: " + dataType.getName());
               }
            }
            catch (Throwable e)
            {  
               System.err.println("Cannot load " + className + ": " + e.getMessage());
            }
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return registeredTopics;
   }
   
   public TopicDataType<?> getTopicDataType(String topicType)
   {
      if(topicDataTypes.containsKey(topicType))
      {
         return topicDataTypes.get(topicType);
      }
      
      return new HexStringTopicDataType(HEX_STRING_MAX_SIZE, topicType);
   }
}
