# maze_printer
Generates and prints mazes.  

### Changelog  
#### Version 0.2 - Improved speed and schematic export

* Removed "space-heater" code  
  * Previous version would take about 24 hours to generate a 10,000 x 10,000 pixel maze with the path size set to the
    smallest setting; now it takes only a few seconds.  
* Improved maze generator algorithm  
  * Maximum path length is 10, creating more forks in the maze  
* Added schematic export functionality  
  * Now you can generate a .schematic maze file for MCEdit to put into your Minecraft worlds!  

#### Version 0.1 - Initial release

First working version.

### Building  

Requires Apache Maven to build and install.  
Steps:  
* Clone the repository onto your computer  
* Open a command prompt in the directory you cloned the repository into  
* Type `mvn package`  
* When that is finished, the jar and its dependencies will be in the `target` folder. To run, double-click the jar or type
  `java -jar maze_printer-<VERSION>.jar` (replace `<VERSION>` with the current version)  
  * On some operating systems, you may need to make the jar executable before you can run it. On Linux, you can use
    `chmod +x` to do this.  
* When moving the jar file, include the `lib` folder with it. It needs this folder (which has the dependencies in it) to
  work properly. It may function without the `lib` folder, but some functionality (currently schematic export) will throw
  errors.  

Otherwise, if you don't want to build your own, get the latest release build from the **Releases** page and manually 
download the required dependencies listed below into a `lib` folder next to the jar.

#### Dependencies (download the latest versions)  

* [Gson](https://repo.maven.apache.org/maven2/com/google/code/gson/gson/)
* [Flow NBT](https://repo.maven.apache.org/maven2/com/flowpowered/flow-nbt/)

### License

Released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
