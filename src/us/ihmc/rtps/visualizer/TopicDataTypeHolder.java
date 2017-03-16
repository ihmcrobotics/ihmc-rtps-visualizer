package us.ihmc.rtps.visualizer;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import us.ihmc.pubsub.attributes.QosInterface;
import us.ihmc.pubsub.common.Guid;

public class TopicDataTypeHolder extends TreeItem<String>
{
   
   private final static Image ERROR_IMAGE = new Image(TopicHolder.class.getResourceAsStream("error_icon_16x16.png"));

   private final TreeItem<String> rootNode;
   private final HashMap<ParticipantHolder, TopicDataParticipantHolder> participants = new HashMap<>();

   
   private final String topicName;
   private final String topicDataType;
   private final String partition;
   
   private final TopicQosHolder topicQosHolder = new TopicQosHolder();
   
   
   public TopicDataTypeHolder(String name, String topicName, String topicDataType, String partition)
   {
      super(name);
      
      rootNode = new TreeItem<String>(name);
      rootNode.setExpanded(true);
      
      this.topicName = topicName;
      this.topicDataType = topicDataType;
      this.partition = partition;
   }

   private TopicDataParticipantHolder getHolder(ParticipantHolder holder)
   {
      if (!participants.containsKey(holder))
      {
         TopicDataParticipantHolder value = new TopicDataParticipantHolder(holder);
         participants.put(holder, value);
         rootNode.getChildren().add(value);
         
      }
      return participants.get(holder);
   }
   
   private void updateQos(QosInterface qosInterface)
   {
      if(!topicQosHolder.update(qosInterface))
      {
         Platform.runLater(() -> setGraphic(new ImageView(ERROR_IMAGE)));
      }
   }

   public void addSubscriber(ParticipantHolder participantGuid, Guid guid, SubscriberAttributesHolder attributes)
   {
      getHolder(participantGuid).addSubscriber(guid, attributes);
      updateQos(attributes.getQosInterface());
   }

   public void addPublisher(ParticipantHolder participantGuid, Guid guid, PublisherAttributesHolder attributes)
   {
      getHolder(participantGuid).addPublisher(guid, attributes);
      updateQos(attributes.getQosInterface());
   }

   public TreeItem<String> getRootNode()
   {
      return rootNode;
   }

   public void removeParticipant(ParticipantHolder participant)
   {
      TopicDataParticipantHolder holder = participants.remove(participant);
      if(holder != null)
      {
         Platform.runLater(() -> rootNode.getChildren().remove(holder));
      }
   }
   
   public boolean isEmpty()
   {
      return participants.isEmpty();
   }

   public String getTopicName()
   {
      return topicName;
   }

   public String getTopicDataType()
   {
      return topicDataType;
   }

   public TopicQosHolder getTopicQosHolder()
   {
      return topicQosHolder;
   }

   public String getPartition()
   {
      return partition;
   }
   
   
}
