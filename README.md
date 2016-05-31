BebopRosJava is a pure Java implementation to control and simulate the Bebop drone using ROS/Gazebo.
This implementation has worked for ROS Indigo.

#  Installation
BebopRosJava requires the following ROS packages to be installed:
- [RosJava](http://wiki.ros.org/rosjava/Tutorials/indigo/Installation)
- [bebop_autonomy](http://bebop-autonomy.readthedocs.io/en/latest/installation.html)

For simulation (this simulator uses the AR.Drone model, which has almost the same ROS interface as Bebop drone):
- [tum_simulator](https://github.com/dougvk/tum_simulator)

# Getting started
- [Create a RosJava package](http://wiki.ros.org/rosjava_build_tools/Tutorials/indigo/Creating%20Rosjava%20Packages#RosJava_Catkin_Packages)
- Clone this repository into your package directory
- Open `your_package/settings.gradle` and add `include 'BebopRosJava'` to the end of file
- Compile the project by one of the following ways:
    1. Compiling with Gradle, this will only compile your project alone: 
        - Go to `your_package/BebopRosJava/` and run `../gradlew installApp`
    2. Compiling using `catkin_make`, with this, cmake will run through your entire workspace and when it gets to your new project, it will pass off the build to gradle:
        - Open `your_package/CMakelist.txt`
        - Find and replace the line `catkin_rosjava_setup()` by `catkin_rosjava_setup(installApp publishMavenJavaPublicationToMavenRepository)`
        - Go to your workspace and run `catkin_make`
        - For more information on building a RosJava project, see [this page, section 2](http://wiki.ros.org/rosjava_build_tools/Tutorials/indigo/WritingPublisherSubscriber%28Java%29)

- Run your ros node(s): 
    - Go to `your_package/BebopRosJava/build/install/BebopRosJava/bin`
    - Run `./BebopRosJava your_rosnode_class_directory`