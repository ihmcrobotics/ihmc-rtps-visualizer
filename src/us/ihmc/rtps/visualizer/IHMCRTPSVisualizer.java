package us.ihmc.rtps.visualizer;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IHMCRTPSVisualizer extends Application
{

   
   

   @Override
   public void start(Stage stage) throws IOException
   {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("IHMCRTPSVisualizer.fxml"));
      Parent root = loader.load();
      
      
      IHMCRTPSController controller = loader.getController();
      new IHMCRTPSParticipant(controller);
      
      
      
      Scene scene = new Scene(root, 800, 600);
      stage.setTitle("IHMC RTPS Visualizer");
      stage.setScene(scene);
      stage.show();
      
   }

   public static void main(String[] args)
   {
      launch(args);
   }
}
