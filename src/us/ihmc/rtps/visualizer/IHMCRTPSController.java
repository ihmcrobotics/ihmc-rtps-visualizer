package us.ihmc.rtps.visualizer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class IHMCRTPSController implements Initializable
{

   @FXML
   private TreeView<String> topicTree;
   @FXML
   private TreeView<String> participantTree;
   @FXML
   private ListView<String> dataList;
   @FXML
   private TreeView<String> participantDataTree;

   private final ObservableList<String> dataObserverable = FXCollections.observableArrayList();

   private final TreeItem<String> topicRoot = new TreeItem<>("Root");

   public void addTopic(TopicHolder topic)
   {
      Platform.runLater(() -> topicRoot.getChildren().add(topic));
   }

   public void removeTopic(TopicHolder topic)
   {
      Platform.runLater(() -> topicRoot.getChildren().remove(topic));
   }

   @Override
   public void initialize(URL location, ResourceBundle resources)
   {
      dataList.setItems(dataObserverable);

      participantDataTree.setShowRoot(false);
      topicTree.setShowRoot(false);
      topicTree.setRoot(topicRoot);

      topicTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof TopicDataTypeHolder)
            {
               participantTree.setRoot(((TopicDataTypeHolder) newValue).getRootNode());
            }
            else
            {
               participantTree.setRoot(null);
            }
      });

      participantTree.setShowRoot(false);
      participantTree.getSelectionModel().selectedItemProperty().addListener((observervable, oldValue, newValue) -> {
         if (newValue instanceof EndpointHolder)
         {
            participantDataTree.setRoot(((EndpointHolder) newValue).getData());
         }
         else
         {
            participantDataTree.setRoot(null);
         }
      });

   }

}
