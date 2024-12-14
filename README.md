# Minesweeper
This is my implementation of the game [Minesweeper](https://minesweeper.online/) using entirely Java. 
This was a pair-programming assignment for my class CS2510. 

In this project, my partner and I made the game from scratch using ArrayLists, Iterators, and Java impworld library.

# How to play
- Click a cell to reveal what’s underneath. If it’s a number, it shows how many mines are nearby.
- Two-finger click (for my fellow Mac user) or right-click to flag a cell if you think it’s a mine.
- Flag all the mines cells correctly to win - the game will let you know when you’ve won.
- If you click on a bomb, the game is over.

Good luck and Have fun! 😚💣🎊

P.S. you can change the amount of mines in the game by following [this](#change-amount-of-bombs).


# What's so special?
The logic across every Minesweeper is the same: "The board is divided into cells, with mines randomly distributed. To win, you need to open all the cells. The number on a cell shows the number of mines adjacent to it. Using this information, you can determine cells that are safe, and cells that contain mines. Cells suspected of being mines can be marked with a flag using the right mouse button." - [minesweeper.online](https://minesweeper.online/)

I enhanced the graphics of the game, which entails:
- Implemented so that the first click the player plays will never be a bomb, but will always be a floodfil instead
- Forming 3D cells
- Checking win and lose condition and pop up screen accordingly
- The flag in the game is the Vietnam flag! 🇻🇳
- You can restart the game when clicked in the box in the middle
  

# How to Set Up
Since this project was developed using a library made by my university, they must be installed and set up correctly for the game to work. **Please** run the project in Eclipse since the project was written in Eclipse. (IntelliJ IDEA for Java always but we gotta respect the OG and setting up the run config and library dependencies in other IDE is a nightmare...) 

If you do not have Eclipse and don't want to download the IDE, here is a demo of the project:

https://github.com/user-attachments/assets/4a4fe553-2c4c-400c-b5f2-6a9e08fc18c5



## Prerequisites
Java Development Kit (JDK): Make sure JDK 11 is installed. If not, you can download it [here](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html).

External Library: This project is built on a custom library provided by CS2510 Fundies 2 team. You need to import the required JAR files to run the project. The library can be found in the EclipseJars folder.

### Eclipse
1. Clone this repository or download the ZIP file and extract it.
2. Open Eclipse and create a new project.
3. Browse to the directory where you cloned or extracted the project and import it.
4. Adding the external JARS:
   1) Right-click on the project and select Properties.
   2) Click on Java Build Path > Libraries and click Add External JARS
   3) Browse to the lib folder and select both of the JAR files to add it.
   4) Click Apply and Close
5. Navigate to the EclipseWorkspace folder and move the Minesweeper.java file to the src file of the project you created.
6. Running the file:
   1) Click the arrow down symbol next to the first green play button in the bar near the top of the screen/
   2) Click Run Configurations > Java Application in the sidebar.
   3) Select tester.Main as the Main class.
   4) In the Arguments tab, for Program arguments, type ExamplesMinesweeper
   5) Click Apply then Run.
7. Next time you want to run the project again, click the arrow down button next to the first green play button and choose the config you set up.

<a id="change-amount-of-mines"></a>
# Change Amount of Mines
To change amount of mines in the game, navigate to the class ExamplesMinesweeper and scroll to the last method testBigBang, which should be the last method in the file. 

In this line,

```java
MinesweeperWorld world = new MinesweeperWorld(row, col, 10);
```
The number 10 represents the amount of mines in the game. Currently, it is set to 10. You can switch to whatever amount you like as long as the number of mines must be larger than 0 and smaller than the grid's size.








