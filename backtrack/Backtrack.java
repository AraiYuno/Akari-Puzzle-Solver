import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;

class Backtrack {
  private static char[][] board;
  private static boolean breakRecursion = false;
  private static boolean [][] visited;
  private static Block blockArr [];
  private static LinkedList<char[][]> trace;
  private static int nodes =0;
  private final  static int cutOff = 20000000;
  private static long startTime;
  private static long endTime;
  private static long duration;
  
  
  public static void resetBoard(char [][]tempBoard) {
    board=copyBoard(tempBoard);
    breakRecursion = false;
    visited = null;
    trace = new LinkedList<char[][]>();
    nodes = 0;
  }
  /**********************************************************
    * main function
    * generates the board from input file.
    * calls solve function to find solution.
    * Prints the solution
    *********************************************************/
  public static void main(String[]args) {
    generateBoard();
    System.out.println();
  }
  
  public static void solve()
  {
    boolean h1 =false,h2 =false,h3 =false,h2h3 = false;
    printBoard(board);
    char [][] tempBoard = copyBoard(board);
    Node start = randomNode();
    trace = new LinkedList<char[][]> ();
    startTime = System.nanoTime();
    h1 = solveH1(start);
    endTime = System.nanoTime();
    duration = (endTime - startTime);
    char[][] solBoard = null;
    if(trace.size()!=0) 
      board = copyBoard(trace.get(0));
    if(isGoalBoard()) {
      solBoard = copyBoard(board);
      System.out.println("H1-> backtrack solution found in "+nodes+" nodes and "+(duration/100000000.0)+ "seconds");
    }
    else {
      System.out.println("Solution not found in short time.");
    }
    resetBoard(tempBoard);
    startTime = System.nanoTime();
    h2 = solveH2();
    endTime = System.nanoTime();
    duration = (endTime - startTime);
    if(trace.size()!=0) 
      board = copyBoard(trace.get(0));
    if(h2) {
      solBoard = copyBoard(board);
      System.out.println("H2-> backtrack solution found in "+nodes+" nodes and "+(duration/100000000.0)+ "seconds");
    }
    else {
      System.out.println("Solution not found in short time.");
    }
    resetBoard(tempBoard);
    startTime = System.nanoTime();
    h3 = solveH3();
    endTime = System.nanoTime();
    duration = (endTime - startTime);
    if(trace.size()!=0) 
      board = copyBoard(trace.get(0));
    if(h3) {
      solBoard = copyBoard(board);
      System.out.println("H3-> backtrack solution found in "+nodes+" nodes and "+(duration/100000000.0)+ "seconds");
    }
    else {
      System.out.println("Solution not found in short time.");
    }
    resetBoard(tempBoard);
    startTime = System.nanoTime();
    h2h3 = solveH2H3();
    endTime = System.nanoTime();
    duration = (endTime - startTime);
    if(trace.size()!=0)
      board = copyBoard(trace.get(0));
    if(h2h3) {
      System.out.println("H2+H3-> backtrack solution found in "+nodes+" nodes and "+(duration/100000000.0)+ "seconds");
    }
    else {
      System.out.println("Solution not found in short time.");
    }
    resetBoard(tempBoard);
    if(solBoard!=null) {
      System.out.println("Solution");
      printBoard(solBoard);
    }
    else {
      System.out.println("Solution not found.");
    }
  }
  public static Node randomNode() {
    int row = (int) (Math.random() * (board.length-2)) + 1;
    int column = (int) (Math.random() * (board.length-2)) + 1;
    Node newNode = new Node(row, column, null, new LinkedList<Node>());
    
    return newNode;
  }
  
  public static boolean solveH1(Node start) {
    nodes++;
    int row = start.getI();
    boolean foundSol=false;
    int column = start.getJ();
    boolean [][] tempBoard;
    Node start1 = start;
    if(Character.isDigit(board[row][column])) {
      start1 = makeNextMove(start);
    }
    if(isGoalBoard() || breakRecursion) {
      if(isGoalBoard()) {
        foundSol = true;
        System.out.println("in");
      }
      breakRecursion = true;  //if the input is only a solution
      trace.add(duplicateBoard(board));
      
    }
    else {
      if(canPlaceBulb(start1)) {   // To check if bulb can be placed at the random start position.
        tempBoard = copyBoard(visited);
        foundSol= placeBulb(start1, 1);    // 1st Recursive call
        board[row][column] = '_';
        updateBoard(start1);
        visited = copyBoard(tempBoard);
      }
      if(!breakRecursion) {
        tempBoard = copyBoard(visited);
        foundSol = placeBlank(start1, 1);  // 2nd recursive call
        visited = copyBoard(tempBoard);
      }
    }
    return foundSol;
  }
  
  public static char[][] copyBoard(char[][] currBoard) {
    char[][] newBoard = new char[currBoard.length][currBoard[0].length];
    for(int row=0;row<newBoard[0].length;row++){
      for(int column=0;column<newBoard.length;column++) {
        newBoard[row][column] = currBoard[row][column];
      }
    }
    return newBoard;
  }
  
  public static boolean solveH2() {              //This is our recursive function.
    nodes++;
    ArrayList<Node> sorted = null;       //This one holds the '_' which has the least option left in sorted order(least to most).
    Node temp = null;
    boolean foundSol = false;
    sorted = nextConstrainedMoves();   //To find spots which light most parts of the board to least.
    // All the elements in the arraylist are siblings. First sibling is the one which lights the most area.
    //Last Sibling in the list light the least area on the board.
    if(sorted!=null) {
      for(int i=0;i<sorted.size();i++) {
        //For each element in the arrayList, I place a bulb at that spot and then call solve function again
        temp = sorted.get(i);
        if(canPlaceBulb(temp)){
          board[temp.getI()][temp.getJ()] = 'b';
          updateBoard(temp);
          if(isGoalBoard()|| breakRecursion || nodes>cutOff){
            if(isGoalBoard()) {
              foundSol=true;
            }
            breakRecursion = true;
            trace.add(copyBoard(board));
            break;
          }
          foundSol=solveH2();
          //If solution is not found after the above solve function completes, then I go to the next sibling
          //I undo the changes I made on the board till now before moving to the next sibling.
          board[temp.getI()][temp.getJ()] = '_';
          updateBoard(temp);
        }
      }
      
    }
    return foundSol;
  }
  
  public static boolean solveH3() {              //This is our recursive function.
    nodes++;
    boolean foundSol = false;
    ArrayList<Node> sorted = null;       //This one holds the '_' which can light most areas to least in order.
    Node temp = null;
    sorted = nextConstrainingMoves();   //To find spots which light most parts of the board to least.
    // All the elements in the arraylist are siblings. First sibling is the one which lights the most area.
    //Last Sibling in the list light the least area on the board.
    if(sorted!=null) {
      for(int i=0;i<sorted.size();i++) {
        //For each element in the arrayList, I place a bulb at that spot and then call solve function again
        temp = sorted.get(i);
        board[temp.getI()][temp.getJ()] = 'b';
        updateBoard(temp);
        if(isGoalBoard()|| breakRecursion || nodes>cutOff){
          if(isGoalBoard()) {
            foundSol=true;
          }
          breakRecursion = true;
          trace.add(copyBoard(board));
          break;
        }
        foundSol = solveH3();
        //If solution is not found after the above solve function completes, then I go to the next sibling
        //I undo the changes I made on the board till now before moving to the next sibling.
        board[temp.getI()][temp.getJ()] = '_';
        updateBoard(temp);
      }
      
    }
    return foundSol;
  }
  
  public static boolean solveH2H3() {              //This is our recursive function.
    nodes++;
    boolean foundSol = false;
    ArrayList<Node> sorted = null;       //This one holds the '_' which can light most areas to least in order.
    Node temp = null;
    sorted = nextHybridMoves();   //To find spots which light most parts of the board to least.
    // All the elements in the arraylist are siblings. First sibling is the one which lights the most area.
    //Last Sibling in the list light the least area on the board.
    if(sorted!=null) {
      for(int i=0;i<sorted.size();i++) {
        //For each element in the arrayList, I place a bulb at that spot and then call solve function again
        temp = sorted.get(i);
        if(canPlaceBulb(temp)){
          board[temp.getI()][temp.getJ()] = 'b';
          updateBoard(temp);
          if(isGoalBoard()|| breakRecursion || nodes>cutOff){
            if(isGoalBoard()) {
              foundSol=true;
            }
            breakRecursion = true;
            trace.add(copyBoard(board));
            break;
          }
          foundSol = solveH2H3();
          //If solution is not found after the above solve function completes, then I go to the next sibling
          //I undo the changes I made on the board till now before moving to the next sibling.
          board[temp.getI()][temp.getJ()] = '_';
          updateBoard(temp);
        }
      }
      
    }
    return foundSol;
  }
  
  /*******************************************************************************************************
    * The method check every '_' spot on board.
    * Checks if a bulb can be placed there, If it can be placed, it counts how many areas that bulb lit on
    * the board.
    * Based on the number of areas it lit, the Node gets added to the arraylist which is sorted in
    * descending order.
    * The top most element in the arraylist is the spot for bulb which lights the most area.
    * The whole arrayList is returned by the function.
    *******************************************************************************************************/
  public static ArrayList<Node> nextConstrainingMoves() {
    ArrayList<Node> result = new ArrayList<Node> ();
    
    int maxRow = 0;
    int maxCol = 0;
    int litBefore = 0;
    int litAfter = 0;
    int maxLit = 0;
    Node temp = null;
    for(int row=0;row<board[0].length;row++){
      for(int column=0;column<board.length;column++) {
        temp = new Node(row,column,null,null);
        if(board[row][column]=='_' && canPlaceBulb(temp)){
          //System.out.println("came in2");
          litBefore = countLitArea();
          board[row][column]='b';
          updateBoard(temp);
          litAfter = countLitArea();
          board[row][column]='_';
          updateBoard(temp);
          temp = new Node(row, column, litAfter-litBefore);
          if(result.size()==0) {
            result.add(temp);
          }
          for(int i=0;i<result.size();i++) {
            if(result.get(i).getLit()<temp.getLit()) {
              result.add(i, temp);
              break;
            }
          }
        }
      }
    }
    return result;
  }
  
  public static boolean isGoalBoard(){
    boolean toReturn = true;
    // check that there exists no unlit spots
    for( int i = 1; i < board.length-1; i++ )
    {
      for( int j = 1; j < board[0].length-1; j++ )
      {
        if( board[i][j] == '_')
          toReturn = false;
      }
    }
    return toReturn;
  }
  
  public static boolean[][] copyBoard(boolean[][] currBoard) {
    boolean[][] newBoard = new boolean[currBoard.length][currBoard[0].length];
    for(int row=0;row<newBoard[0].length;row++){
      for(int column=0;column<newBoard.length;column++) {
        newBoard[row][column] = currBoard[row][column];
      }
    }
    return newBoard;
  }
  
  public static char[][] duplicateBoard(char[][] currBoard) {
    char[][] newBoard = new char[currBoard.length][currBoard[0].length];
    for(int row=0;row<newBoard[0].length;row++){
      for(int column=0;column<newBoard.length;column++) {
        newBoard[row][column] = currBoard[row][column];
      }
    }
    return newBoard;
  }
  
  public static void printBoard(char[][] currBoard) {
    
    for(int row=0;row<currBoard[0].length;row++){
      for(int column=0;column<currBoard.length;column++) {
        if(currBoard[row][column]!='@') {
          if(currBoard[row][column]!='o'){
            System.out.print(currBoard[row][column]);
          }
          else {
            System.out.print('_');
          }
        }
      }
      System.out.println();
    }
  }
  
  /*********************************************************************
    * Given the node, this function will see if a bulb can be placed in that position.
    * If possible, the bulb will be placed and the board will be updated.
    * and Then the 2 recursive calls again.
    *********************************************************************/
  public static boolean placeBulb(Node current, int depth) {
    nodes++;
    int row = current.getI();
    boolean foundSol=false;
    int column = current.getJ();
    boolean[][] tempBoard;
    Node nextNode = null;
    depth++;
    if(isGoalBoard() || breakRecursion ||nodes>cutOff) {
      if(isGoalBoard()) {
        foundSol = true;
        System.out.println("in");
      }
      breakRecursion = true;
      trace.add(duplicateBoard(board));
    }
    else {
      if(canPlaceBulb(current) && !breakRecursion && current!=null) {
        tempBoard = copyBoard(visited);
        board[row][column] = 'b';
        updateBoard(current);
        if(isGoalBoard()) {
          foundSol = true;
          trace.add(duplicateBoard(board));
          breakRecursion=true;
        }
        nextNode = makeNextMove(current);
        if(nextNode!=null && !breakRecursion) {
          foundSol = placeBulb(nextNode, depth++);
          board[nextNode.getI()][nextNode.getJ()] = '_';
          updateBoard(nextNode);
          visited = copyBoard(tempBoard);
        }
      }
      if(!breakRecursion && nextNode!=null) {
        tempBoard = copyBoard(visited);
        foundSol = placeBlank(nextNode, depth++);
        visited = copyBoard(tempBoard);
      }
    }
    return foundSol;
  }
  
  /*********************************************************************
    * Given the node, if the bulb cant be placed there, it will move to the next
    * blank place in the board .
    * and Then call the 2 recursive calls again.
    *********************************************************************/
  public static boolean placeBlank(Node current, int depth) {
    nodes++;
    int row = current.getI();
    int column = current.getJ();
    boolean foundSol=false;
    boolean[][] tempBoard;
    depth++;
    if(isGoalBoard() || breakRecursion || nodes>cutOff) {
      if(isGoalBoard()) {
        foundSol = true;
        System.out.println("in");
      }
      breakRecursion = true;
      trace.add(duplicateBoard(board));
    }
    else {
      Node nextNode = makeNextMove(current);
      if(nextNode!=null) {
        tempBoard = copyBoard(visited);
        foundSol = placeBulb(nextNode, depth++);
        if(isGoalBoard()) {
          foundSol = true;
          System.out.println("in");
          breakRecursion=true;
        }
        visited = copyBoard(tempBoard);
        board[nextNode.getI()][nextNode.getJ()] = '_';
        updateBoard(nextNode);
        tempBoard = copyBoard(visited);
        foundSol= placeBlank(nextNode, depth++);
        visited = copyBoard(tempBoard);
      }
    }
    return foundSol;
  }
  
  
  
  /*********************************************************************
    *Finds the next empty blank on the board.
    *********************************************************************/
  public static Node makeNextMove(Node current) {
    Node nextNode = null;
    int i = current.getI();
    int j = current.getJ();
    boolean toBreak = false;
    boolean cycle = false;
    // check if it's the end of the cols. If it is, then we increment row by 1 and set column as 1, otherwise, just increment column by 1.
    do
    {
      if(visited[i][j]) {
        cycle = true;
        break;
      }
      visited[i][j] = true;
      if( j == board[0].length - 1 )
      {
        j = 1;
        if( i == board.length - 1 )
          i = 1;
        else
          i++;
      }
      else
        j++;
    }while ( Character.isDigit(board[i][j]) || board[i][j] == '@' || board[i][j] == 'o' || board[i][j]=='b');
    if(!cycle) {
      nextNode = new Node(i,j,current, new LinkedList<Node>());
    }
    return nextNode;
  }
  
  public static void generateBoard(){
    try {
      Scanner scanner = new Scanner(new File("inputPuzzle.txt"));
      while(scanner.hasNextLine())
      {
        String line = scanner.nextLine();
        if(line.length()>0&&line.charAt(0) == '#')
        {
          System.out.println(line);
          
          String [] tokens = scanner.nextLine().split("\\s+");
          if(tokens.length > 0 && tokens[0].charAt(0) != '#') {
            board = new char [Integer.parseInt(tokens[0])+2][Integer.parseInt(tokens[1])+2];
            visited = new boolean[board[0].length][board.length];
            for(int i=0;i<visited[0].length;i++) {
              for(int j=0;j<visited.length;j++) {
                visited[i][j] = false;
              }
            }
            System.out.println( Integer.parseInt(tokens[0]) + " " + Integer.parseInt(tokens[0]) );
            
            for( int i = 0; i < board.length; i ++ ) {
              for( int j = 0; j < board[0].length; j++ ){
                if( i == 0 || j == 0 ) {
                  board[i][j] = '@';
                }
                if( i == board.length-1 || j == board[0].length-1 ) {
                  board[i][j] = '@';
                }
              }
            }
            int numBlocks = 0;
            for(int rows = 1; rows < board.length-1;rows++){
              String tempStr = scanner.nextLine();
              for( int cols = 1; cols < board[0].length-1; cols++ ){
                board[rows][cols] = tempStr.charAt(cols-1);
                if( Character.isDigit(tempStr.charAt(cols-1))){
                  numBlocks++;
                }
              }
              
            }
            blockArr = new Block[numBlocks];
            
            int index = 0;
            for( int i = 0; i < board.length; i++ ) {
              for( int j = 0; j < board[0].length; j++ ) {
                if( Character.isDigit(board[i][j]) ) {
                  blockArr[index] = new Block( board[i][j], i, j );
                  index++;
                }
              }
            }
            solve();
          }
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  /*********************************************************************
    * Checks every wall in the board to see if its still valid.
    *********************************************************************/
  public static boolean validateBlocks() {
    boolean toReturn = true;
    int count;
    // check for every visited node.
    for( int i = 0; i < blockArr.length; i++ ){
      int rowPos = blockArr[i].getI();
      int colPos = blockArr[i].getJ();
      count = 0;
      int bulbs = Character.getNumericValue(board[rowPos][colPos]);
      if( board[rowPos - 1][colPos] == 'b' || board[rowPos - 1][colPos] == '_' )
        count++;
      if( board[rowPos + 1][colPos] == 'b' || board[rowPos + 1][colPos] == '_' )
        count++;
      if( board[rowPos][colPos - 1] == 'b' || board[rowPos][colPos - 1] == '_' )
        count++;
      if( board[rowPos][colPos + 1] == 'b' || board[rowPos][colPos + 1] == '_' )
        count++;
      
      if(count< bulbs) {
        toReturn = false;
      }
    }
    return toReturn;
  }
  
  /*********************************************************************
    *To check if a bulb can be placed at a given location
    *(Same like your code. I just took out similar lines and made a seperate
    *function called 'checkOneDirection' which checks the given direction
    *********************************************************************/
  public static boolean canPlaceBulb( Node node ) {
    boolean toReturn = true;
    int row = node.getI();
    int column = node.getJ();
    boolean north = true;
    boolean south = true;
    boolean east = true;
    boolean west = true;
    boolean result3 = true;
    //if the current cell is bulb or lighted, then we can't place a bulb anyways.
    if( board[row][column]=='@'|| board[row][column] == 'b' || board[row][column] == 'o' ) {
      result3 = false;
    }
    else {
      if(board[row-1][column]!='@') {
        north = checkOneDirection(row-1, column);
      }
      if(board[row+1][ column]!='@') {
        south = checkOneDirection(row+1, column);
      }
      if(board[row][column-1]!='@') {
        west = checkOneDirection(row, column-1);
      }
      if(board[row][column+1]!='@') {
        east = checkOneDirection(row, column+1);
      }
      //result1 = checkOneDirection(row-1, column) && checkOneDirection(row+1, column);
      //result2 = checkOneDirection(row, column-1) && checkOneDirection(row, column+1);
      if(north && south && east && west && result3 ) {
        board[row][column] = 'b';
        updateBoard(node);
        toReturn = validateBlocks();
        board[row][column] = '_';
        updateBoard(node);
      }
    }
    return (north && south && east && west && result3 &&toReturn);
  }
  
  
  public static boolean checkOneDirection(int row, int column) {
    boolean result = true;
    if( Character.isDigit(board[row][column]) )  {
      // if there exists a block, check the number and do a corresponding action
      int wall = Character.getNumericValue(board[row][column]);
      int countBlocks = 0;
      if( board[row-1][column] == 'b' )
        countBlocks++;
      if( board[row+1][column] == 'b' )
        countBlocks++;
      if( board[row][column-1] == 'b' )
        countBlocks++;
      if( board[row][column+1] == 'b' )
        countBlocks++;
      if( countBlocks >= wall )
        result = false;
      
    }
    return result;
  }
  
  
  public static void updateBoard( Node node ) {
    
    // update the board if a bulb has been placed in the current cell.
    if( board[node.getI()][node.getJ()] == 'b' )
    {
      // light up all the cells to the south of the current cell unless it is blocked by a block on its way.
      int i2 = node.getI() + 1;
      while( i2 < board.length  && i2 > 0 )
      {
        if( Character.isDigit(board[i2][node.getJ()]) || board[i2][node.getJ()] =='@' )
          break;
        board[i2][node.getJ()] = 'o';
        i2++;
      }
      
      // light up all the cells to the north of the current cell unless it is blocked by a block on its way.
      i2 = node.getI() - 1;
      while( i2 < board.length  && i2 > 0 )
      {
        if( Character.isDigit( board[i2][node.getJ()] ) || board[i2][node.getJ()] == '@' )
          break;
        board[i2][node.getJ()] = 'o';
        i2--;
      }
      
      // light up all the cells to the east of the current cell unless it is blocked by a block on its way.
      int j2 = node.getJ() + 1;
      while( j2 < board[0].length && j2 > 0 )
      {
        if( Character.isDigit( board[node.getI()][j2] ) || board[node.getI()][j2] == '@' )
          break;
        board[node.getI()][j2] = 'o';
        j2++;
      }
      
      // light up all the cells to the west of the current cell unless it is blocked by a block on its way.
      j2 = node.getJ() - 1;
      while( j2 < board[0].length && j2 >  0 )
      {
        if( Character.isDigit( board[node.getI()][j2] ) || board[node.getI()][j2] == '@' )
          break;
        board[node.getI()][j2] = 'o';
        j2--;
      }
    }
    
    // if there does not exist a bulb in the current cell, or the bulb is removed, then we update the board again
    if( board[node.getI()][node.getJ()] == '_' )
    {
      // CHECK NORTH
      //
      //
      int northI = node.getI();
      
      while( board[northI][node.getJ()] != '@' && !Character.isDigit(board[northI][node.getJ()]) )
      {
        int north_eastJ = node.getJ()+1;
        int north_westJ = node.getJ()-1;
        boolean checkN_E = true;
        boolean checkN_W = true;
        //NORTH -> EAST
        while( board[northI][north_eastJ]!= '@' && !Character.isDigit(board[northI][north_eastJ]) )
        {
          //System.out.println("DDs");
          if( board[northI][north_eastJ] == 'b' || Character.isDigit(board[northI][north_eastJ]) )
          {
            //System.out.println("DD");
            checkN_E = false;
            break;
          }
          north_eastJ++;
        }
        
        //NORTH -> WEST
        while( board[northI][north_westJ] != '@' && !Character.isDigit(board[northI][north_westJ]) )
        {
          if( board[northI][north_westJ] == 'b' || Character.isDigit(board[northI][north_westJ]) )
          {
            checkN_W = false;
            break;
          }
          north_westJ--;
        }
        
        if( checkN_E && checkN_W )
          board[northI][node.getJ()] = '_';
        northI--;
      }
      
      
      // CHECK SOUTH
      //
      //
      int southI = node.getI();
      
      while( board[southI][node.getJ()] != '@' && !Character.isDigit(board[southI][node.getJ()]) )
      {
        int south_eastJ = node.getJ()+1;
        int south_westJ = node.getJ()-1;
        boolean checkS_E = true;
        boolean checkS_W = true;
        //SOUTH -> EAST
        while( board[southI][south_eastJ]!= '@' && !Character.isDigit(board[southI][south_eastJ]) )
        {
          if( board[southI][south_eastJ] == 'b' || Character.isDigit(board[southI][south_eastJ]) )
          {
            checkS_E = false;
            break;
          }
          south_eastJ++;
        }
        
        //SOUTH -> WEST
        while( board[southI][south_westJ]!= '@' && !Character.isDigit(board[southI][south_westJ]) )
        {
          if( board[southI][south_westJ] == 'b' || Character.isDigit(board[southI][south_westJ]) )
          {
            checkS_W = false;
            break;
          }
          south_westJ--;
        }
        
        if( checkS_E && checkS_W )
          board[southI][node.getJ()] = '_';
        southI++;
      }
      
      
      //CHECK EAST
      //
      //
      int eastJ = node.getJ();
      
      while( board[node.getI()][eastJ] != '@' && !Character.isDigit(board[node.getI()][eastJ]) )
      {
        int east_northI = node.getI()-1;
        int east_southI = node.getI()+1;
        boolean checkE_N = true;
        boolean checkE_S = true;
        //EAST -> NORTH
        while( board[east_northI][eastJ]!= '@' && !Character.isDigit(board[east_northI][eastJ]) )
        {
          if( board[east_northI][eastJ] == 'b' || Character.isDigit(board[east_northI][eastJ]) )
          {
            checkE_N = false;
            break;
          }
          east_northI--;
        }
        
        //EAST -> SOUTH
        while( board[east_southI][eastJ]!= '@' && !Character.isDigit(board[east_southI][eastJ]) )
        {
          if( board[east_southI][eastJ] == 'b' || Character.isDigit(board[east_southI][eastJ]) )
          {
            checkE_S = false;
            break;
          }
          east_southI++;
        }
        
        if( checkE_S && checkE_N )
          board[node.getI()][eastJ] = '_';
        eastJ++;
      }
      
      //CHECK WEST
      //
      //
      int westJ = node.getJ();
      
      while( board[node.getI()][westJ] != '@' && !Character.isDigit(board[node.getI()][westJ]) )
      {
        int west_northI = node.getI()-1;
        int west_southI = node.getI()+1;
        boolean checkW_N = true;
        boolean checkW_S = true;
        //WEST -> NORTH
        while( board[west_northI][westJ]!= '@' && !Character.isDigit(board[west_northI][westJ]) )
        {
          if( board[west_northI][westJ] == 'b' || Character.isDigit(board[west_northI][westJ]) )
          {
            checkW_N = false;
            break;
          }
          west_northI--;
        }
        
        //EAST -> SOUTH
        while( board[west_southI][westJ]!= '@' && !Character.isDigit(board[west_southI][westJ]) )
        {
          if( board[west_southI][westJ] == 'b' || Character.isDigit(board[west_southI][westJ]) )
          {
            checkW_S = false;
            break;
          }
          west_southI++;
        }
        
        if( checkW_S && checkW_N )
          board[node.getI()][westJ] = '_';
        westJ--;
      }
    } // end of _
  }
  
  /*******************************************************************************************************
    * The method check every '_' spot on board.
    * Checks if a bulb can be placed there, If it can be placed, it counts how many areas that bulb lit on
    * the board.
    * Based on the number of areas it lit, the Node gets added to the arraylist which is sorted in
    * descending order.
    * The top most element in the arraylist is the spot for bulb which lights the most area.
    * The whole arrayList is returned by the function.
    *******************************************************************************************************/
  public static ArrayList<Node> nextConstrainedMoves() {
    ArrayList<Node> result = new ArrayList<Node> ();
    Node temp = null;
    boolean [][] visited = new boolean[(board.length)][board.length];
    for(int row=0;row<board[0].length;row++){
      for(int column=0;column<board.length;column++) {
        visited[row][column] = false;
      }
    }
    
    for(int row=0;row<board[0].length;row++){
      for(int column=0;column<board.length;column++) {
        if(!visited[row][column]) {
          temp = new Node(row,column,null,null);
          if(Character.isDigit(board[row][column])) {
            int wall = Character.getNumericValue(board[row][column]);
            if(unlitSpots(row, column)<=wall ) {
              if(board[row][column+1]=='_') {
                result.add(0, new Node(row,column+1,null,null));
                visited[row][column+1] = true;
              }
              if(board[row][column-1]=='_') {
                result.add(0, new Node(row,column-1,null,null));
                visited[row][column-1] = true;
              }
              if(board[row+1][column]=='_') {
                result.add(0, new Node(row+1,column,null,null));
                visited[row+1][column] = true;
              }
              if(board[row-1][column]=='_') {
                result.add(0, new Node(row-1,column,null,null));
                visited[row-1][column] = true;
              }
            }
          }
        }
      }
    }
    for(int row=0;row<board[0].length;row++){
      for(int column=0;column<board.length;column++) {
        if(!visited[row][column] && board[row][column]=='_') {
          result.add(new Node(row, column,null, null));
          visited[row][column] = true;
        }
      }
    }
    
    return result;
  }
  
  public static int unlitSpots(int row, int col) {
    int result = 0;
    if(board[row][col+1]=='_') {
      result++;
    }
    if(board[row][col-1]=='_') {
      result++;
    }
    if(board[row+1][col]=='_') {
      result++;
    }
    if(board[row-1][col]=='_') {
      result++;
    }
    return result;
  }
  
  
  public static int countLitArea() {
    int count = 0;
    for(int row=0;row<board[0].length;row++){
      for(int column=0;column<board.length;column++) {
        if(board[row][column]=='o') {
          count++;
        }
      }
    }
    return count;
  }
  
  public static ArrayList<Node> nextHybridMoves() {
    ArrayList<Node> constrainedMoves= nextConstrainedMoves();
    ArrayList<Node> result = new ArrayList<Node>();
    
    int maxRow = 0;
    int maxCol = 0;
    int litBefore = 0;
    int litAfter = 0;
    int maxLit = 0;
    Node temp = null;
    for(int i=0;i<constrainedMoves.size();i++) {
      temp = constrainedMoves.get(i);
      int row= temp.getI();
      int column = temp.getJ();
      if(board[row][column]=='_' && canPlaceBulb(temp)){
        litBefore = countLitArea();
        board[row][column]='b';
        updateBoard(temp);
        litAfter = countLitArea();
        board[row][column]='_';
        updateBoard(temp);
        temp = new Node(row, column, litAfter-litBefore);
        if(result.size()==0) {
          result.add(temp);
        }
        for(int j=0;j<result.size();j++) {
          if(result.get(j).getLit()<temp.getLit()) {
            result.add(j, temp);
            break;
          }
        }
      }
    }
    return result;
  }
}