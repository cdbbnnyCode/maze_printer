# maze_printer
Generates and prints mazes.  

### Building  
This project requires the image4j library, which can be obtained from https://sourceforge.net/projects/image4j/files/. 
Place the image4j.jar file in the root directory of the project.  
#### With eclipse
Export the project as a runnable jar.  
#### Without eclipse
You will need a JDK to do this. Get one at http://www.oracle.com/technetwork/java/javase/downloads/index.html.  
Open a command prompt in the root directory of the project.  
Create a `bin` directory.  
`javac -cp image4j-0.7.2.jar -d bin -sourcepath src src/maze_printer/MazePrinter.java`.  
Now `cd` into the `bin` directory. Create a jar using the provided `manifest.txt` file by running  
`jar cmf ../manifest.txt ../maze_printer.jar .`.  

Otherwise, if you don't want to build your own, get the latest release build from the **Releases** page.  
It will still need a copy of image4j.jar in the same directory to work.
