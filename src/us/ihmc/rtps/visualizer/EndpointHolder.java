package us.ihmc.rtps.visualizer;

import javafx.scene.control.TreeItem;

public class EndpointHolder extends TreeItem<String>
{
   private final TreeItem<String> data;
   
   public EndpointHolder(String name, TreeItem<String> data)
   {
      super(name);
      this.data = data;
   }
   
   public TreeItem<String> getData()
   {
      return data;
   }
}
