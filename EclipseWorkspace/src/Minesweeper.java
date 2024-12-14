import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// ------- READ ME -------

/*1. Enhanced the graphic and gameplay, includes:
*  - Implemented so that the first click the player plays
*    will never be a bomb, but will always be a floodfil instead
*  - Forming 3D cells
*  - Checking win and lose condition and pop up screen accordingly
*
*2. Restart the game when clicked in the box in the middle
* 

*/

//------------------------------
// Constant interface
interface Constant {
  int HEIGHT = 500;
  int WIDTH = 500;
  // PLS switch back to 40 for the whole board 
  int CELL_SIZE = 50;
  // FOR TESTING!!!
  int ROW = 10;
  int COL = 20;
  double LETTER_SIZE = CELL_SIZE / 2;
  
  int RESTART_BUTTON_WIDTH = CELL_SIZE * 6;
  int RESTART_BUTTON_HEIGHT = CELL_SIZE * 2;
  
  int WORLDWIDTH = CELL_SIZE * COL;
  int WORLDHEIGHT = CELL_SIZE * ROW;


  // -- DRAWINGS --
  // triangle to create shadow effects
  TriangleImage TRIANGLE = 
      new TriangleImage(new Posn(0,0),new Posn(CELL_SIZE / 7,0), new Posn(0,CELL_SIZE / 7), 
          OutlineMode.SOLID, Color.getHSBColor(2.6f, 0.4f, 0.75f)); // light blue

  // gives blending effects 
  RectangleImage BACK_TOP = 
      new RectangleImage(CELL_SIZE / 7, CELL_SIZE / 7, OutlineMode.SOLID, 
          Color.getHSBColor(2.6f, 1f, 0.55f)); // dark blue

  OverlayImage BLEND_TOP = 
      new OverlayImage(TRIANGLE, BACK_TOP);

  RectangleImage BACK_BOT = 
      new RectangleImage(CELL_SIZE / 7, CELL_SIZE / 7, OutlineMode.SOLID, 
          Color.getHSBColor(2.6f, 0.4f, 0.75f));

  OverlayImage BLEND_BOT = 
      new OverlayImage(TRIANGLE, BACK_TOP);

  // shadow shapes
  RectangleImage BOT = new RectangleImage( 
      CELL_SIZE, CELL_SIZE / 7, OutlineMode.SOLID, 
      Color.getHSBColor(2.6f, 1f, 0.55f));

  RectangleImage RIGHT = new RectangleImage(
      CELL_SIZE / 7, CELL_SIZE, OutlineMode.SOLID, 
      Color.getHSBColor(2.6f, 1f, 0.55f));

  RectangleImage TOP = new RectangleImage(
      CELL_SIZE, CELL_SIZE / 8, OutlineMode.SOLID, 
      Color.getHSBColor(2.6f, 0.4f, 0.75f));

  RectangleImage LEFT = new RectangleImage(
      CELL_SIZE / 8, CELL_SIZE, OutlineMode.SOLID, 
      Color.getHSBColor(2.6f, 0.4f, 0.75f));

  // unopened background
  RectangleImage UNREVEALED_BACKGROUND = new RectangleImage(
      CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, 
      Color.getHSBColor(2.6f, 0.8f, 0.7f));

  // unopened cell
  OverlayOffsetImage UNREVEALED =
      new OverlayOffsetImage(
          BLEND_BOT, 
          (CELL_SIZE - CELL_SIZE / 7) / 2 + CELL_SIZE * 0.003,
          -(CELL_SIZE - CELL_SIZE / 7) / 2 + CELL_SIZE * 0.003,
          new OverlayOffsetImage(
              BLEND_TOP, 
              -(CELL_SIZE - CELL_SIZE / 7) / 2 + CELL_SIZE * 0.003,
              (CELL_SIZE - CELL_SIZE / 7) / 2 + CELL_SIZE * 0.003,
              new OverlayOffsetImage(
                  BOT, 
                  0, 
                  - CELL_SIZE / 2.23,
                  new OverlayOffsetImage(
                      RIGHT,
                      - CELL_SIZE / 2.23,
                      0,
                      new OverlayOffsetImage(
                          LEFT, 
                          CELL_SIZE / 2.23,
                          0,
                          new OverlayOffsetImage(
                              TOP, 
                              0, 
                              CELL_SIZE / 2.23,
                              UNREVEALED_BACKGROUND))))));

  // outline
  RectangleImage OUTLINE = new RectangleImage(
      CELL_SIZE, CELL_SIZE, OutlineMode.OUTLINE, Color.BLACK);

  // open bomb cell
  OverlayImage BOMB_REVEALED = new OverlayImage(
      OUTLINE,
      new RectangleImage(
          CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, 
          Color.getHSBColor(357, 0.57f, 1f)));

  // blue opened cell 
  RectangleImage REVEALED = new RectangleImage(
      CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.getHSBColor(2.6f, 0.6f, 0.7f));

  // opened cell
  OverlayImage OPENED = 
      new OverlayImage(Constant.OUTLINE, Constant.REVEALED);

  // bomb drawing
  StarImage BOMB = new StarImage(
      CELL_SIZE / 2.75, 8, 2, OutlineMode.SOLID, Color.getHSBColor(5.1f, 0.71f, 1f));

  // image of flag
  OverlayImage FLAG = 
      new OverlayImage(
          new StarImage(CELL_SIZE / 8, OutlineMode.SOLID, Color.YELLOW),
          new RectangleImage(CELL_SIZE / 2, CELL_SIZE / 3, OutlineMode.SOLID, Color.RED));

  OverlayOffsetImage FLAG1 = 
      new OverlayOffsetImage(
          FLAG,
          - CELL_SIZE / 4, CELL_SIZE / 13,
          new RectangleImage(CELL_SIZE / 15, CELL_SIZE / 2, OutlineMode.SOLID, Color.BLACK));
  
  EquilateralTriangleImage END = 
      new EquilateralTriangleImage(CELL_SIZE, OutlineMode.SOLID, Color.RED);
}

//------------------------------
//------------------------------
// represent the class MinesweeperWorld
class MinesweeperWorld extends World {
  int row;
  int col;
  int mines;
  ArrayList<ArrayList<Cell>> grid;
  Random rand;
  boolean gameOver = false;
  boolean restartGame = false;
  boolean firstClick = true;
  
  // 1st constructor 
  MinesweeperWorld(int row, int col, int mines) {
    this.row = new Utils().check(row, 
        "Number of rows must be larger than 0 and smaller than 51."); 
    this.col = new Utils().check(col, 
        "Number of columns must be larger than 0 and smaller than 51.");
    this.mines = new Utils().checkMines(mines, row, col, 
        "Number of mines must be larger than 0 and smaller than the grid's size.");
    this.grid = new ArrayList<ArrayList<Cell>>();
    this.rand = new Random();

    makeGrid();
  }

  // 2nd constructor for testing
  MinesweeperWorld(int row, int col, int mines, ArrayList<ArrayList<Cell>> grid) {
    this.row = new Utils().check(row, 
        "Number of rows must be larger than 0 and smaller than 51."); 
    this.col = new Utils().check(col, 
        "Number of columns must be larger than 0 and smaller than 51.");
    this.mines = new Utils().checkMines(mines, row, col, 
        "Number of mines must be larger than 0 and smaller than the grid's size.");
    this.grid = grid;
    this.rand = new Random();

    makeGrid();
  }

  // 3rd constructor for testing random
  MinesweeperWorld(int row, int col, int mines, Random rand) {
    this.row = new Utils().check(row, 
        "Number of rows must be larger than 0 and smaller than 51."); 
    this.col = new Utils().check(col, 
        "Number of columns must be larger than 0 and smaller than 51.");
    this.mines = new Utils().checkMines(mines, row, col, 
        "Number of mines must be larger than 0 and smaller than the grid's size.");
    this.grid = new ArrayList<ArrayList<Cell>>();
    this.rand = rand;

    makeGrid();

  }

  // make grid & add cells onto it
  void makeGrid() {
    // making rows
    for (int i = 0; i < row; i++) {
      ArrayList<Cell> row1 = new ArrayList<Cell>();
      // making columns
      for (int j = 0; j < col; j++) {
        row1.add(new Cell(false));
      } // adding onto the grid
      this.grid.add(row1);
    }  
  }


  // place mines into random cells
  void placeMines(int firstRow, int firstCol) {
    int numMines = 0;
    // while no. of mines are less than amount of mines, 
    // keep placing mines randomly
    while (numMines < this.mines) {
      int r = rand.nextInt(this.row);
      int c = rand.nextInt(this.col);
      
    //look into the neighbor cells 
      //look into the neighbor cells 
      //need to use math.abs because
      //the click can be negative, 
      //which messes up with the result of a click
      boolean neighborFirstClick = 
          Math.abs(firstRow - r) <= 1 
          && Math.abs(firstCol - c) <= 1;

      if (!(this.grid.get(r).get(c).isMine)
          && !neighborFirstClick
          && !(this.grid.get(r).get(c).isOpen)) {
        this.grid.get(r).set(c, new Cell(true));
        numMines++;
      } 
    }
  }

  // linking neighbors of a cell together with respect to grid's boundaries
  void linkNeighbors() {
    for (int r = 0; r < row; r++) {
      for (int c = 0; c < col; c++) {
        Cell currentCell = grid.get(r).get(c);

        // iterate over neighbors
        for (int dy = -1; dy <= 1; dy++) {
          for (int dx = -1; dx <= 1; dx++) {
            // skip current cell
            if (dy != 0 || dx != 0) {
              int neighborRow = r + dy;
              int neighborCol = c + dx;

              // check if neighbor cell is within boundaries of grid
              if (isValidCell(neighborRow, neighborCol)) {
                Cell neighborCell = grid.get(neighborRow).get(neighborCol);
                currentCell.addNeighbor(neighborCell);
              }
            }
          }
        }

      }
    }
  }

  // check if a cell is within boundaries of grid
  boolean isValidCell(int r, int c) {
    return r >= 0 && r < row && c >= 0 && c < col;
  }

  // draw a WorldScene
  public WorldScene makeScene() {
    WorldScene scene = this.getEmptyScene();
    for (int r = 0; r < this.row; r++) {
      for (int c = 0; c < this.col; c++) {
        Cell cell = this.grid.get(r).get(c);
        WorldImage cellImage = cell.drawCell();

        // Calculate the position of the cell in the scene
        int x = c * Constant.CELL_SIZE + Constant.CELL_SIZE / 2;
        int y = r * Constant.CELL_SIZE + Constant.CELL_SIZE / 2;

        scene.placeImageXY(cellImage, x, y);
      }
    }
    if (gameOver) {
      WorldImage end = 
          new OverlayImage(
              new AboveImage(
                  winOrLose(),
                  new TextImage("RESTART", Constant.CELL_SIZE / 2, FontStyle.BOLD, Color.WHITE)),
              new RectangleImage(
                  Constant.CELL_SIZE * 6, Constant.CELL_SIZE * 2, OutlineMode.SOLID, Color.getHSBColor(2.6f, 1f, 0.55f)));

      scene.placeImageXY(end, Constant.WORLDWIDTH / 2, Constant.WORLDHEIGHT / 2);
      this.restartGame = true;      
      return scene;

    } return scene;
  }

  //print the corresponding message for when a player win or loses
  public TextImage winOrLose() {
    if (checkWin()) {
      return new TextImage("You won!", Constant.CELL_SIZE / 2, FontStyle.BOLD, Color.GREEN);
    } else {
      return new TextImage("Game Over!", Constant.CELL_SIZE / 2, FontStyle.BOLD, Color.RED);
    }
  }
  

  // helper method to flood open empty cells
  void floodFill(Cell cell) {
    // Mark the current cell as open
    cell.isOpen = true;

    // go through neighbors of the current cell
    for (Cell neighbor : cell.neighbors) {

      // if neighbor not open, not mine, not flagged, and empty
      if (!neighbor.isOpen && !neighbor.isMine && !neighbor.isFlagged
          && neighbor.countNeighborMines() == 0) {
        // go into neighbors of neighbors
        floodFill(neighbor);

        // if not open & not flagged then open
      } else if (!neighbor.isOpen && !neighbor.isFlagged) {
        neighbor.isOpen = true;
      }
    }
  }

  // reveal all mine cells when click on a mine 
  public void revealAll() {
    for (ArrayList<Cell> row : grid) {
      for (Cell cell : row) {
        if (cell.isMine) {
          cell.isOpen = true;
        }
      }
    }
  }

  // on mouse click 
  public void onMouseClicked(Posn pos, String buttonName) { 
    // current cell
    Cell currentCell = grid.get((pos.y / Constant.CELL_SIZE)).get(pos.x / Constant.CELL_SIZE);     
    boolean withinXBounds =  pos.x >= Constant.WORLDWIDTH / 2 - Constant.RESTART_BUTTON_WIDTH / 2
        && pos.x <= Constant.WORLDWIDTH / 2 + Constant.RESTART_BUTTON_WIDTH / 2;

    boolean withinYBounds = pos.y >= Constant.WORLDHEIGHT / 2 - Constant.RESTART_BUTTON_HEIGHT / 2
        && pos.y <= Constant.WORLDHEIGHT / 2 + Constant.RESTART_BUTTON_HEIGHT / 2;

    // left click
    if (buttonName.equals("LeftButton")) { 
      //check condition for the first click 
      //and never let the player lose here
      if (firstClick) {
        firstClick = false;
        placeMines(pos.y / Constant.CELL_SIZE, pos.x / Constant.CELL_SIZE);
        linkNeighbors();
      }
      // if a mine, lose -> end game & reveal all mines
      if (currentCell.isMine && !currentCell.isFlagged) { 
        currentCell.isOpen = true;
        this.gameOver = true;
        winOrLose();

        // reveal all mines
        this.revealAll();
        // end world
        this.worldEnds();

        // can't open a flagged cell
      } else if (currentCell.isFlagged) {
        currentCell.isOpen = false;
        winOrLose();

        // if current cell is empty -> flood fill action
      } else if (currentCell.countNeighborMines() == 0) {
        floodFill(currentCell);

        // if cell is not open & is near a mine -> open
      } else if (!currentCell.isOpen && currentCell.countNeighborMines() > 0) {
        currentCell.isOpen = true;

        // else
      } else if (!currentCell.isFlagged) {
        currentCell.isOpen = true;
      } 
      if (gameOver && restartGame) {
        if (withinXBounds && withinYBounds) {
          this.restartGame = true;
          restartGame();
        }
      }

      // right click to flag a cell
    } else if (buttonName.equals("RightButton")) { 

      // if cell is not flagged & is not open
      if (!currentCell.isFlagged && !currentCell.isOpen) {
        currentCell.isFlagged = true;
        if (checkWin()) {
          gameOver = true;
        }


        // if cell is flagged, unflagged cell
      } else if (currentCell.isFlagged) { 
        currentCell.isFlagged = false;

        // else
      } else {
        currentCell.isFlagged = true;

      }
    } 
  }

  // method to check win condition
  boolean checkWin() {
    // Iterate through all cells in the grid
    for (ArrayList<Cell> row : grid) {
      for (Cell cell : row) {
        // check if all mines are flagged
        if (cell.isMine && !cell.isFlagged) {
          return false; // not win yet
        }
      }
    }
    return true; // win!!!
  }

  // check loss condition
  boolean checkLose(Cell clickedCell) {
    // check if the clicked cell contains a mine
    return clickedCell.isMine && clickedCell.isOpen;
  }


  
  //restart the game, initialize the game again
  void restartGame() {
    this.grid.clear(); 
    this.gameOver = false;
    this.firstClick = true;
    makeGrid(); 
    linkNeighbors();
  }
}


//------------------------------
// utility class
class Utils {
  Utils() {}


  // check if values are out of bounds
  int check(int val, String msg) {
    if (val > 0 && val < 51) {
      return val;
    } else {
      throw new IllegalArgumentException(msg);
    }
  }

  // check if number of mines are smaller than the grid 
  int checkMines(int val1, int val2, int val3, String msg) {
    if (val1 >= 0 && val1 < val2 * val3) {
      return val1;
    } else {
      throw new IllegalArgumentException(msg);
    }
  }
}


//------------------------------
// represent a cell class
class Cell {
  // is this cell a mine?
  boolean isMine;
  // is this cell flagged?
  boolean isFlagged;
  // is this cell opened?
  boolean isOpen;
  // represent cell's neighbors
  ArrayList<Cell> neighbors;

  // empty constructor
  Cell() {
    this.isMine = false;
    this.isFlagged = false;
    this.isOpen = false;
    this.neighbors = new ArrayList<Cell>(); 
  }

  // 2nd constructor
  Cell(boolean isMine, boolean isFlagged, boolean isOpen) {
    this.isMine = isMine;
    this.isFlagged = isFlagged;
    this.isOpen = isOpen;
    this.neighbors = new ArrayList<Cell>();
  }

  // 3rd constructor
  Cell(boolean isMine) {
    this.isMine = isMine;
    this.isFlagged = false;
    this.isOpen = false;
    this.neighbors = new ArrayList<Cell>();
  }

  // helper method
  // Add a neighbor to the cell
  void addNeighbor(Cell neighbor) {
    if (!(neighbors.contains(neighbor))) {
      neighbors.add(neighbor);
    }
  }

  // Count the number of neighboring cells
  int countNeighborMines() {
    int count = 0;
    for (Cell neighbors : this.neighbors) {
      if (neighbors.isMine) {
        count += 1;
      }
    }
    return count;
  }

  // color the number based on the number of neighboring mine cells
  TextImage colorNumber() {
    if (this.countNeighborMines() == 1) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.getHSBColor(1.5f, 0.2f, 1f)); // aqua blue
    } else if (countNeighborMines() == 2) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.getHSBColor(3.3f, 0.64f, 1f)); // light green
    } else if (countNeighborMines() == 3) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.getHSBColor(1.9f, 0.29f, 1f)); // salmon red
    } else if (countNeighborMines() == 4) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.getHSBColor(2.6f, 1f, 0.55f)); // dark blue
    } else if (countNeighborMines() == 5) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.getHSBColor(357, 0.3f, 1f)); // dark red
    } else if (countNeighborMines() == 6) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.GREEN); // teal
    } else if (countNeighborMines() == 7) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.BLACK); // black
    } else if (countNeighborMines() == 8) {
      return new TextImage(
          Integer.toString(countNeighborMines()), 
          Constant.LETTER_SIZE, FontStyle.BOLD, 
          Color.DARK_GRAY); // light gray
    } else {
      return new TextImage("", 
          Constant.LETTER_SIZE, FontStyle.BOLD, Color.GREEN);
    }  
  }


  // draw a cell
  public WorldImage drawCell() {
    // is open & is NOT a mine
    if (isOpen && !isMine) { 
      return new OverlayImage(
          colorNumber(), 
          new OverlayImage(Constant.OUTLINE, Constant.REVEALED));

      // is open & is a mine
    } else if (isOpen && isMine) { 
      return new OverlayImage(
          Constant.BOMB,
          Constant.BOMB_REVEALED);

      // not open and is a mine and not flagged
    } else if (!isOpen && isMine && !isFlagged) {
      return Constant.UNREVEALED;

      // is flagged
    } else if (isFlagged) {
      return new OverlayImage(
          Constant.FLAG1,
          Constant.UNREVEALED);

      // else
    } else {
      return Constant.UNREVEALED;
    }
  }
}


//------------------------------
// examples of minesweeper
class ExamplesMinesweeper {

  // examples of cells
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;

  Cell c10;
  Cell c11;
  Cell c12;
  Cell c13;
  Cell c14;
  Cell c15;
  Cell c16;
  Cell c17;
  Cell c18;
  Cell c19;
  Cell c20;
  Cell c21;

  ArrayList<ArrayList<Cell>> grid1;

  ArrayList<ArrayList<Cell>> grid2;

  MinesweeperWorld ms1;

  MinesweeperWorld ms2;

  WorldScene expected;

  // reset the examples
  void reset() {
    // examples of a 3x3 grid that has 9 cells
    c1 = new Cell(true, false, false); // mine & not opened
    c2 = new Cell(false, true, false); // flagged
    c3 = new Cell(false, false, true); 
    c4 = new Cell(false, false, true); 
    c5 = new Cell(true, false, true); // mine & opened
    c6 = new Cell(false, false, false);
    c7 = new Cell(false, false, false);
    c8 = new Cell(true, false, false); // mine
    c9 = new Cell(false, false, true); // opened

    // making the grid
    ArrayList<ArrayList<Cell>> grid1 = 
        new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(c1, c2, c3)),
            new ArrayList<>(Arrays.asList(c4, c5, c6)),
            new ArrayList<>(Arrays.asList(c7, c8, c9))));

    // creating a new MinesweeperWorld with grid1
    ms1 = new MinesweeperWorld(3, 3, 0, grid1);

    // linking neighbors
    ms1.linkNeighbors();

    // world scene
    expected = new WorldScene(0, 0);
    // draw the world scene 
    expected.placeImageXY(c1.drawCell(), 
        0 * Constant.CELL_SIZE + Constant.CELL_SIZE, 0 * Constant.CELL_SIZE + Constant.CELL_SIZE);
    expected.placeImageXY(c2.drawCell(), 
        1 * Constant.CELL_SIZE, 0 * Constant.CELL_SIZE);
    expected.placeImageXY(c3.drawCell(), 
        2 * Constant.CELL_SIZE, 0 * Constant.CELL_SIZE);
    expected.placeImageXY(c4.drawCell(), 
        0 * Constant.CELL_SIZE, 1 * Constant.CELL_SIZE);
    expected.placeImageXY(c5.drawCell(), 
        1 * Constant.CELL_SIZE, 1 * Constant.CELL_SIZE);
    expected.placeImageXY(c6.drawCell(), 
        2 * Constant.CELL_SIZE, 1 * Constant.CELL_SIZE);
    expected.placeImageXY(c7.drawCell(), 
        0 * Constant.CELL_SIZE, 2 * Constant.CELL_SIZE);
    expected.placeImageXY(c8.drawCell(), 
        1 * Constant.CELL_SIZE, 2 * Constant.CELL_SIZE);
    expected.placeImageXY(c9.drawCell(), 
        2 * Constant.CELL_SIZE, 2 * Constant.CELL_SIZE);

    // minesweeper 2 example
    c10 = new Cell(false, false, false); 
    c11 = new Cell(true, false, false); // mine & not opened
    c12 = new Cell(false, false, false); // flagged
    c13 = new Cell(false, false, false); 
    c14 = new Cell(false, false, false); 
    c15 = new Cell(true, false, false); // mine & opened
    c16 = new Cell(false, false, false);
    c17 = new Cell(false, false, false);
    c18 = new Cell(false, false, false); // mine
    c19 = new Cell(false, false, false); // opened
    c20 = new Cell(true, false, false); // mine
    c21 = new Cell(false, false, false); // opened

    // making the grid
    ArrayList<ArrayList<Cell>> grid2 = 
        new ArrayList<>(Arrays.asList(
            new ArrayList<>(Arrays.asList(c10, c11, c12, c13)),
            new ArrayList<>(Arrays.asList(c14, c15, c16, c17)),
            new ArrayList<>(Arrays.asList(c18, c19, c20, c21))));

    // creating a new MinesweeperWorld with grid1
    ms2 = new MinesweeperWorld(3, 4, 0, grid2);

    // linking neighbors
    ms2.linkNeighbors();
  }

  //------------------------------
  // test if number of rows and columns are appropriate according
  // to the creator's standards
  void testCheck(Tester t) {
    Utils utils = new Utils();

    t.checkExpect(utils.check(1, "Error message"), 1);
    t.checkExpect(utils.check(50, "Error message"), 50);
    t.checkExpect(utils.check(25, "Error message"), 25);

    t.checkConstructorException(
        new IllegalArgumentException(
            "Number of rows must be larger than 0 and smaller than 51."),
        "MinesweeperWorld", 0, 0, 0);
    t.checkConstructorException(
        new IllegalArgumentException(
            "Number of rows must be larger than 0 and smaller than 51."),
        "MinesweeperWorld", -1, 3, 0);
    t.checkConstructorException(
        new IllegalArgumentException(
            "Number of columns must be larger than 0 and smaller than 51."),
        "MinesweeperWorld", 3, 51, 0);

    t.checkException(new IllegalArgumentException("Error message"), utils, "check", 0, 
        "Error message");
    t.checkException(new IllegalArgumentException("Error message"), utils, "check", 51, 
        "Error message");
    t.checkException(new IllegalArgumentException("Error message"), utils, "check", -1, 
        "Error message"); 
  }

  //------------------------------
  // test to make sure that number of mines are appropriate according
  // to boundaries set by the creators (us)
  void testCheckMines(Tester t) {
    Utils utils = new Utils();

    t.checkExpect(utils.checkMines(1, 10, 10, "Error message"), 1); 
    t.checkExpect(utils.checkMines(99, 10, 10, "Error message"), 99); 
    t.checkExpect(utils.checkMines(50, 10, 10, "Error message"), 50); 

    t.checkException(new IllegalArgumentException("Error message"), utils, "checkMines", 
        0, 0, 0, "Error message");
    t.checkException(new IllegalArgumentException("Error message"), utils, "checkMines", -1, 
        10, 10, "Error message");
    t.checkException(new IllegalArgumentException("Error message"), utils, "checkMines", 
        100, 10, 10, "Error message"); 
    t.checkException(new IllegalArgumentException("Error message"), utils, "checkMines", 
        101, 10, 10, "Error message");

    t.checkConstructorException(
        new IllegalArgumentException(
            "Number of mines must be larger than 0 "
                + "and smaller than the grid's size."),
        "MinesweeperWorld", 4, 3, -1);
    t.checkConstructorException(
        new IllegalArgumentException(
            "Number of mines must be larger than 0 "
                + "and smaller than the grid's size."),
        "MinesweeperWorld", 5, 3, 16);
  }

  //------------------------------
  // Test method for the linkNeighbors method
  void testLinkNeighbors(Tester t) {
    reset(); 
    // top left 
    t.checkExpect(c1.neighbors.size(), 3); 

    t.checkExpect(c2.neighbors.size(), 5); 
    t.checkExpect(c3.neighbors.size(), 3); 
    t.checkExpect(c4.neighbors.size(), 5);
    // middle cell
    t.checkExpect(c5.neighbors.size(), 8); 

    t.checkExpect(c6.neighbors.size(), 5); 
    t.checkExpect(c7.neighbors.size(), 3); 
    t.checkExpect(c8.neighbors.size(), 5); 
    // bottom right
    t.checkExpect(c9.neighbors.size(), 3); 

    // Additional checks for specific neighbor relationships if needed
    t.checkExpect(c1.neighbors, new ArrayList<Cell>(Arrays.asList(c2, c4, c5)));
    t.checkExpect(c1.neighbors.contains(c1), false);
    t.checkExpect(c1.neighbors.contains(c2), true); 
    t.checkExpect(c1.neighbors.contains(c3), false);
    t.checkExpect(c1.neighbors.contains(c4), true);
    t.checkExpect(c1.neighbors.contains(c5), true); 
    t.checkExpect(c1.neighbors.contains(c6), false);
    t.checkExpect(c1.neighbors.contains(c7), false);
    t.checkExpect(c1.neighbors.contains(c8), false);
    t.checkExpect(c1.neighbors.contains(c9), false); 

    t.checkExpect(c5.neighbors, 
        new ArrayList<Cell>(Arrays.asList(c1, c2, c3, c4, c6, c7, c8, c9)));
    t.checkExpect(c5.neighbors.contains(c1), true);
    t.checkExpect(c5.neighbors.contains(c2), true); 
    t.checkExpect(c5.neighbors.contains(c3), true);
    t.checkExpect(c5.neighbors.contains(c4), true);
    t.checkExpect(c5.neighbors.contains(c5), false); 
    t.checkExpect(c5.neighbors.contains(c6), true);
    t.checkExpect(c5.neighbors.contains(c7), true);
    t.checkExpect(c5.neighbors.contains(c8), true);
    t.checkExpect(c5.neighbors.contains(c9), true); 
  }

  //------------------------------
  // test method that make the game board or the grid
  void testMakeGrid(Tester t) {
    MinesweeperWorld world = new MinesweeperWorld(2, 2, 0); 

    // Expected grid size
    t.checkExpect(world.grid.size(), 2);
    t.checkExpect(world.grid.get(0).size(), 2);

    // Check that cells are initialized correctly (not mines, not opened)
    for (ArrayList<Cell> row : world.grid) {
      for (Cell cell : row) {
        t.checkExpect(cell.isMine, false);
        t.checkExpect(cell.isOpen, false);
      }
    }
  }

  //------------------------------
  // test if method place mines in random places 
  boolean testPlaceMines(Tester t) {
    Random randomSeed1 = new Random(12345);
    MinesweeperWorld testWorld = new MinesweeperWorld(3, 3, 1, randomSeed1); 

    Random randomSeed2 = new Random(230305);
    MinesweeperWorld testWorld2 = new MinesweeperWorld(3, 4, 2, randomSeed2); 

    // for random seed 1
    return 
        t.checkExpect(testWorld.grid.get(0).get(0).isMine, false) 
        &&
        t.checkExpect(testWorld.grid.get(0).get(1).isMine, false) 
        &&
        t.checkExpect(testWorld.grid.get(0).get(2).isMine, false) 
        &&
        t.checkExpect(testWorld.grid.get(1).get(0).isMine, false) 
        &&
        t.checkExpect(testWorld.grid.get(1).get(1).isMine, false) // one mine
        &&
        t.checkExpect(testWorld.grid.get(1).get(2).isMine, false)
        &&
        t.checkExpect(testWorld.grid.get(2).get(0).isMine, false)
        &&
        t.checkExpect(testWorld.grid.get(2).get(1).isMine, false)
        &&
        t.checkExpect(testWorld.grid.get(2).get(2).isMine, false)
        &&

        // for random seed 2
        t.checkExpect(testWorld2.grid.get(0).get(0).isMine, false)
        &&
        t.checkExpect(testWorld2.grid.get(0).get(1).isMine, false)
        &&
        t.checkExpect(testWorld2.grid.get(0).get(2).isMine, false) 
        &&
        t.checkExpect(testWorld2.grid.get(0).get(3).isMine, false)
        &&

        t.checkExpect(testWorld2.grid.get(1).get(0).isMine, false) 
        &&
        t.checkExpect(testWorld2.grid.get(1).get(1).isMine, false) 
        &&
        t.checkExpect(testWorld2.grid.get(1).get(2).isMine, false)
        &&
        t.checkExpect(testWorld2.grid.get(1).get(3).isMine, false)
        &&

        t.checkExpect(testWorld2.grid.get(2).get(0).isMine, false) // 2 mines
        &&
        t.checkExpect(testWorld2.grid.get(2).get(1).isMine, false)
        &&
        t.checkExpect(testWorld2.grid.get(2).get(2).isMine, false)
        &&
        t.checkExpect(testWorld2.grid.get(2).get(3).isMine, false); // 2 mines 
  }

  //------------------------------
  // test if given cell is in boundaries of the grid
  void testIsValidCell(Tester t) {
    reset(); 

    // Assuming ms1 is a 3x3 MinesweeperWorld from the reset method
    // Inside boundaries
    t.checkExpect(ms1.isValidCell(1, 1), true); // Middle cell
    t.checkExpect(ms1.isValidCell(0, 0), true); // Top-left corner
    t.checkExpect(ms1.isValidCell(2, 2), true); // Bottom-right corner
    t.checkExpect(ms1.isValidCell(0, 2), true); // Top-right corner
    t.checkExpect(ms1.isValidCell(2, 0), true); // Bottom-left corner

    // Edges
    t.checkExpect(ms1.isValidCell(0, 1), true); // Top edge
    t.checkExpect(ms1.isValidCell(1, 0), true); // Left edge
    t.checkExpect(ms1.isValidCell(1, 2), true); // Right edge
    t.checkExpect(ms1.isValidCell(2, 1), true); // Bottom edge

    // Outside boundaries
    t.checkExpect(ms1.isValidCell(-1, 1), false); // Above grid
    t.checkExpect(ms1.isValidCell(1, -1), false); // Left of grid
    t.checkExpect(ms1.isValidCell(3, 1), false); // Below grid
    t.checkExpect(ms1.isValidCell(1, 3), false); // Right of grid
    t.checkExpect(ms1.isValidCell(-1, -1), false); // Top-left outside
    t.checkExpect(ms1.isValidCell(3, 3), false); // Bottom-right outside
  }

  //------------------------------
  // test making the scene
  boolean testMakeScene(Tester t) {
    reset();

    // Test makeScene() method
    return
        t.checkExpect(ms1.makeScene(), expected);
  }

  //------------------------------
  // test for adding neighbors to a cell
  void testAddNeighbor(Tester t) {
    reset(); 

    // Initial number of neighbors should be set by linkNeighbors in reset
    int initialNeighbors = c1.neighbors.size();

    // Add a new neighbor
    Cell newNeighbor = new Cell(false);
    c1.addNeighbor(newNeighbor);

    // Verify the neighbor is added
    t.checkExpect(c1.neighbors.size(), initialNeighbors + 1);
    t.checkExpect(c1.neighbors.contains(newNeighbor), true);

    // Try to add the same neighbor again
    c1.addNeighbor(newNeighbor);

    // Verify no duplicate neighbors are added
    t.checkExpect(c1.neighbors.size(), initialNeighbors + 1);
  }

  //------------------------------
  // test count how many mines are in a cell's neighbors
  // don't count itself if the cell is a mine
  void testCountNeighborMines(Tester t) {
    reset();

    t.checkExpect(c1.countNeighborMines(), 1);
    t.checkExpect(c2.countNeighborMines(), 2);
    t.checkExpect(c3.countNeighborMines(), 1);
    t.checkExpect(c4.countNeighborMines(), 3);
    t.checkExpect(c5.countNeighborMines(), 2);
    t.checkExpect(c6.countNeighborMines(), 2);
    t.checkExpect(c7.countNeighborMines(), 2);
    t.checkExpect(c8.countNeighborMines(), 1);
    t.checkExpect(c9.countNeighborMines(), 2);
  }

  //------------------------------
  // test method that draw number of mines nearby onto a cell
  void testcolorNumber(Tester t) {
    reset(); 

    // NOTE: for testing, the grid is small so we can't test up to 8 mines nearby!

    // cell 3 has 1 mine nearby
    TextImage oneMine = new TextImage("1", Constant.LETTER_SIZE, FontStyle.BOLD, 
        Color.getHSBColor(1.5f, 0.2f, 1f));
    t.checkExpect(c3.colorNumber(), oneMine);

    // cell 9 has 2 mines nearby
    TextImage twoMine = new TextImage("2", Constant.LETTER_SIZE, FontStyle.BOLD, 
        Color.getHSBColor(3.3f, 0.64f, 1f));
    t.checkExpect(c9.colorNumber(), twoMine);

    // cell 4 has 3 mines nearby
    TextImage threeMine = new TextImage("3", Constant.LETTER_SIZE, FontStyle.BOLD, 
        Color.getHSBColor(1.9f, 0.29f, 1f));
    t.checkExpect(c4.colorNumber(), threeMine);
  }

  //------------------------------
  // method to draw the cell
  void testDrawCell(Tester t) {
    reset();

    t.checkExpect(c6.drawCell(), Constant.UNREVEALED);

    OverlayImage flaggedImage = new OverlayImage(
        Constant.FLAG1,
        Constant.UNREVEALED);
    t.checkExpect(c2.drawCell(), flaggedImage);

    OverlayImage openedImage = new OverlayImage(
        c3.colorNumber(), 
        new OverlayImage(Constant.OUTLINE, Constant.REVEALED));
    t.checkExpect(c3.drawCell(), openedImage);


    OverlayImage mineImage = new OverlayImage(
        Constant.BOMB,
        Constant.BOMB_REVEALED);
    t.checkExpect(c5.drawCell(), mineImage);  
  }

  //Test method for floodFill
  void testFloodFill(Tester t) {
    reset(); 


    ms1.floodFill(ms1.grid.get(1).get(1)); 


    t.checkExpect(ms1.grid.get(1).get(0).isOpen, true);
    t.checkExpect(ms1.grid.get(0).get(1).isOpen, false);



    t.checkExpect(ms1.grid.get(2).get(2).isOpen, true); 
  }

  //Test method for revealAll
  void testRevealAll(Tester t) {
    reset(); 


    ms1.revealAll();

    t.checkExpect(ms1.grid.get(0).get(0).isOpen, true); 
    t.checkExpect(ms1.grid.get(2).get(2).isOpen, true);



    t.checkExpect(ms1.grid.get(1).get(1).isOpen, true);
  }



  //--------------------
  // test bigBang() method
  void testBigBang(Tester t) {

    reset();

    // initial board - CELL_SIZE should be switch to 40!!
    int row = 10;
    int col = 20;
    MinesweeperWorld world = new MinesweeperWorld(row, col, 10);
    int worldHeight = Constant.CELL_SIZE * row;
    int worldWidth = Constant.CELL_SIZE * col;
    double tickRate = 0.15;
    world.bigBang(worldWidth, worldHeight, tickRate);

//    reset();
//    // switch cell size to a 100 so that it is easier to look at
//    // CELL_SIZE is currently 100 so that its easier to look
//    int worldHeight1 = Constant.CELL_SIZE * 3;
//    int worldWidth1 = Constant.CELL_SIZE * 3;
//    double tickRate1 = 0.15;
//    ms1.bigBang(worldWidth1, worldHeight1, tickRate1);


//    reset();
//    // another state of the world - CELL_SIZE should be switch to 40!!
//    int worldHeight2 = Constant.CELL_SIZE * 3;
//    int worldWidth2 = Constant.CELL_SIZE * 4;
//    double tickRate2 = 0.15;
//    ms2.bigBang(worldWidth2, worldHeight2, tickRate2);
  }
}

