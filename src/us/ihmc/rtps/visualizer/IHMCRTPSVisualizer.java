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

      Scene scene = new Scene(root, 1024, 720);
      stage.setTitle("IHMC RTPS Visualizer");
      stage.setScene(scene);
      stage.show();
   }

   public static void main(String[] args)
   {
      launch(args);
   }
}
