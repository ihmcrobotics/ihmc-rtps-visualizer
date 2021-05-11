plugins {
   id("us.ihmc.ihmc-build")
   id("us.ihmc.ihmc-ci") version "7.4"
   id("us.ihmc.ihmc-cd") version "1.20"
   id("com.github.hierynomus.license") version "0.14.0"
   id("edu.sc.seis.launch4j") version "2.4.4"
}

ihmc {
   group = "us.ihmc"
   version = "0.2.0"
   vcsUrl = "https://stash.ihmc.us/projects/LIBS/repos/ihmc-rtps-visualizer"
   maintainer = "Duncan Calvert (dcalvert@ihmc.us)"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

app.entrypoint("IHMCRTPSVisualizer", "us.ihmc.rtps.visualizer.IHMCRTPSVisualizer")

mainDependencies {
   api("us.ihmc:ihmc-pub-sub:0.15.0")
   api("commons-io:commons-io:2.8.0")
}

testDependencies {
   api("us.ihmc:ros2-common-interfaces:0.19.4")
}

license {
    header = rootProject.file("license-header.txt")
    strictCheck = true
}

launch4j {
   mainClassName = "us.ihmc.rtps.visualizer.IHMCRTPSVisualizer"
}
