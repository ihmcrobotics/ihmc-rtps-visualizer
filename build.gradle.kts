plugins {
   id("us.ihmc.ihmc-build")
   id("us.ihmc.ihmc-ci") version "8.3"
   id("us.ihmc.ihmc-cd") version "1.26"
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
   api("us.ihmc:ihmc-pub-sub:0.19.0")
}

testDependencies {
   api("us.ihmc:ros2-common-interfaces:0.23.1") {
      exclude(group = "us.ihmc", module = "euclid")
   }
   api("us.ihmc:euclid-geometry:0.16.2")
}

license {
    header = rootProject.file("license-header.txt")
    strictCheck = true
}

launch4j {
   mainClassName = "us.ihmc.rtps.visualizer.IHMCRTPSVisualizer"
}
