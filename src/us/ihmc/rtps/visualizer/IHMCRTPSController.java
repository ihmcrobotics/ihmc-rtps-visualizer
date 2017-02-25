package us.ihmc.rtps.visualizer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class IHMCRTPSController implements Initializable
{
   
   @FXML private TreeView<String> topicTree;
   @FXML private TreeView<String> participantTree;
   @FXML private ListView<String> dataList;
   
   
   private final ObservableList<String> dataObserverable = FXCollections.observableArrayList();
   
   
   private final TreeItem<String> topicRoot = new TreeItem<>("Root");
   
   
   public void addTopic(TopicHolder topic)
   {
      Platform.runLater(()->topicRoot.getChildren().add(topic));
   }
   
   public void removeTopic(TopicHolder topic)
   {
      topicRoot.getChildren().remove(topic);
   }


   @Override
   public void initialize(URL location, ResourceBundle resources)
   {
      dataList.setItems(dataObserverable);
      
      topicTree.setShowRoot(false);
      topicTree.setRoot(topicRoot);
      
      topicTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>()
      {

         @Override
         public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue)
         {
            if(newValue instanceof TopicDataTypeHolder)
            {
               participantTree.setRoot(((TopicDataTypeHolder) newValue).getRootNode());
            }
            else
            {
               participantTree.setRoot(null);
            }
            
         }
      });
      
      participantTree.setShowRoot(false);
      
      dataObserverable.add("A");
      dataObserverable.add("B");
      dataObserverable.add("C");
      dataObserverable.add("D");
      
      
      
   }

}
