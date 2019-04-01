# IHMC Java RTPS Visualizer

A visualizer for traffic in a RTPS domain. This visualizer is implemented as an active participant, and will connect to the domain and subscribe to topics.

## Toolchain
- [IHMC Pub/Sub](https://github.com/ihmcrobotics/ihmc-pub-sub): IHMC Pub/Sub RTPS library
- [IHMC Pub/Sub generator](https://github.com/ihmcrobotics/ihmc-pub-sub-generator): Gradle plugin and standalone application to generate java classes from .idl messages.
- [IHMC RTPS Visualizer](https://github.com/ihmcrobotics/ihmc-rtps-visualizer): GUI to display partitions, topics, participants, subscribers, publisher and publisher data on a RTPS domain.
- [IHMC Pub/Sub serializers extra](https://github.com/ihmcrobotics/ihmc-pub-sub-serializers-extra): Optional serializer to generated JSON, BSON, YAML, Java Properties and XML(limited) output from .idl messages. 

## License
The IHMC RTPS Visualizer is licensed under the Apache 2.0. See LICENSE.txt

## Features

- Show topics as they come and go on the domain
- Show participants, publishers and subscribers per topic data type
- Show data on topic
- Warn on QoS misconfiguration
- Decode topic data based on dynamically loaded bundles of TopicDataTypes.

## Usage

The main application class is us.ihmc.rtps.visualizer.IHMCRTPSVisualizer

### Executable:
Multiplatform executables are available on GitHub: https://github.com/ihmcrobotics/ihmc-rtps-visualizer/releases

### Run from source, use the following commands to start 
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


