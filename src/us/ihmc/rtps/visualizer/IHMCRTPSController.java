package us.ihmc.rtps.visualizer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;

public class IHMCRTPSController implements Initializable
{
   private IHMCRTPSParticipant participant;
   @FXML
   private TreeView<String> topicTree;
   @FXML
   private TreeView<String> participantTree;
   @FXML
   private TableView<MessageHolder> dataList;
   
   @FXML
   private TableColumn<MessageHolder, String> timestamp;
   @FXML
   private TableColumn<MessageHolder, String> sequence;
   @FXML
   private TableColumn<MessageHolder, String> bytes;
   @FXML
   private TableColumn<MessageHolder, String> change;
   
   
   @FXML
   private TreeView<String> participantDataTree;
   @FXML
   private Label qosPolicyLabel;
   
   @FXML
   private TextArea message;

   private final ObservableList<MessageHolder> dataObserverable = FXCollections.observableArrayList();

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
      timestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
      sequence.setCellValueFactory(new PropertyValueFactory<>("sequenceNumber"));
      bytes.setCellValueFactory(new PropertyValueFactory<>("bytes"));
      change.setCellValueFactory(new PropertyValueFactory<>("changeKind"));
      
      dataList.setItems(dataObserverable);

      participantDataTree.setShowRoot(false);
      topicTree.setShowRoot(false);
      topicTree.setRoot(topicRoot);

      topicTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedTopic(newValue);
      });

      dataList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedData(newValue);
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

   private void selectedData(MessageHolder newValue)
   {
      if(newValue != null)
      {
         message.setText(newValue.getData());
      }
      else
      {
         message.setText("");
      }
   }

   private void selectedTopic(TreeItem<String> newValue)
   {
      if(participant != null)
      {
         if (newValue instanceof TopicDataTypeHolder)
         {
            TopicDataTypeHolder topicDataTypeHolder = (TopicDataTypeHolder) newValue;
            participantTree.setRoot(topicDataTypeHolder.getRootNode());
            participant.subscribeToTopic(topicDataTypeHolder);
            qosPolicyLabel.setText(topicDataTypeHolder.getTopicQosHolder().getError());
         }
         else
         {
            participantTree.setRoot(null);
            participant.unSubscribeFromTopic();
            qosPolicyLabel.setText("");
         }         
      }
   }

   public void setParticipant(IHMCRTPSParticipant ihmcrtpsParticipant)
   {
      this.participant = ihmcrtpsParticipant;
   }

   public void updateDataList(MessageHolder messageHolder)
   {
      Platform.runLater(() -> dataObserverable.add(messageHolder));
   }
   
   public void clearDataList()
   {
      Platform.runLater(() -> dataObserverable.clear());
   }

}
