# IHMC Java RTPS Visualizer

A visualizer for traffic in a RTPS domain. This visualizer is implemented as an active participant, and will connect to the domain and subscribe to topics.

Download here: https://github.com/ihmcrobotics/ihmc-rtps-visualizer/releases

### Toolchain
- [IHMC Pub/Sub](https://github.com/ihmcrobotics/ihmc-pub-sub-group): IHMC Pub/Sub RTPS library
- [IHMC Pub/Sub generator](https://github.com/ihmcrobotics/ihmc-pub-sub-group): Gradle plugin and standalone application to generate java classes from .idl messages.
- [IHMC RTPS Visualizer](https://github.com/ihmcrobotics/ihmc-rtps-visualizer): GUI to display partitions, topics, participants, subscribers, publisher and publisher data on a RTPS domain.
- [IHMC Pub/Sub serializers extra](https://github.com/ihmcrobotics/ihmc-pub-sub-group): Optional serializer to generated JSON, BSON, YAML, Java Properties and XML(limited) output from .idl messages. 

### Features

- Show topics as they come and go on the domain
- Show participants, publishers and subscribers per topic data type
- Show data on topic
- Warn on QoS misconfiguration
- Decode topic data based on dynamically loaded bundles of TopicDataTypes.

### Running from source

The main application class is `us.ihmc.rtps.visualizer.IHMCRTPSVisualizer`

```
git clone https://github.com/ihmcrobotics/ihmc-rtps-visualizer.git
cd ihmc-rtps-visualizer
./gradlew run
```

### Loading Topic data type bundles

To optionally decode messages it is necessary to create JAR bundles of topic data types using the IHMC Pub Sub Generator. Refer to the IHMC Pub Sub Generator documentation for more information.

Topic data type bundles can be dynamically loaded from the GUI or added as command line argument:

```
us.ihmc.rtps.visualizer.IHMCRTPSVisualizer -DdataTypeBundles=[bundle1.jar];[bundle2.jar];...;[bundleN.jar]
```

### License

Apache 2.0
