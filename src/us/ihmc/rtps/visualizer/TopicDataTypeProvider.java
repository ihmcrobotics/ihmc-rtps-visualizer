package us.ihmc.rtps.visualizer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import us.ihmc.idl.generated.Chat.ChatMessagePubSubType;
import us.ihmc.pubsub.TopicDataType;

public class TopicDataTypeProvider
{
   private final HashMap<String, TopicDataType<?>> topicDataTypes = new HashMap<>();
   
   
   public TopicDataTypeProvider()
   {
      loadBundle(new File("/home/jesper/.m2/repository/us/ihmc/IHMCPubSub/0.1.2/IHMCPubSub-0.1.2.jar"));
   }
   
   
   public void loadBundle(File bundle)
   {
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
      
   }
   
   
   public TopicDataType<?> getTopicDataType(String topicType)
   {
      if(topicDataTypes.containsKey(topicType))
      {
         return topicDataTypes.get(topicType);
      }
      
      return new HexStringTopicDataType(1024, topicType, ByteOrder.nativeOrder());
   }
}
