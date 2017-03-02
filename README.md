# IHMC Java RTPS Visualizer

A visualizer for traffic in a RTPS domain. This visualizer is implemented as an active participant, and will connect to the domain and subscribe to topics.


## Features

- Show topics as they come and go on the domain
- Show participants, publishers and subscribers per topic data type
- Show data on topic
- Warn on QoS misconfiguration
- Decode topic data based on dynamically loaded bundles of TopicDataTypes.

## Usage

Run us.ihmc.rtps.visualizer.IHMCRTPSVisualizer

When checked out from source run "gradle run".

### Loading Topic data type bundles
To decode messages it is necessary to create JAR bundles of topic data types using the IHMC Pub Sub Generator. Refer to the IHMC Pub Sub Generator documentation for more information.

Topic data type bundles can be dynamically loaded from the GUI or added as command line argument:

```
us.ihmc.rtps.visualizer.IHMCRTPSVisualizer -DdataTypeBundles=[bundle1.jar];[bundle2.jar];...;[bundleN.jar]
```


## License
The IHMC Java RTPS Visualizer is licensed under the Apache 2.0. See LICENSE.txt