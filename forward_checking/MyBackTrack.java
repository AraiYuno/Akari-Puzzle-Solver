import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

class MyBackTrack { 
    private static char[][] board;
    private static boolean breakRecursion = false;
    static int depth=0;
    private static boolean [][] visited;
    private static char[][] goalBoard;
    private static Node blockArr [];
    private static LinkedList<char[][]> trace;
    private static int nodes =0;
    
    
    /**********************************************************
     * main function
     * generates the board from input file.
     * calls solve function to find solution.
     * Prints the solution
     *********************************************************/
    public static void main(String[]args) {
        generateBoard();
        Node start = randomNode();
        trace = new LinkedList<char[][]> ();
        System.out.println(start.getI() +" , " + start.getJ());
        char [][] solBoard = solve(start);
        if(breakRecursion) {
            System.out.println("This should be the answer and nodes = "+nodes);
            printBoard(trace.get(0));
            
        }
    }
    
    public static Node randomNode() {
        int row = (int) (Math.random() * (board.length-2)) + 1;
        int column = (int) (Math.random() * (board.length-2)) + 1;
        Node newNode = new Node(row, column, null, new LinkedList<Node>());
        
        return newNode;
    }
    
    public static char [][] randomBackTracking( char [][] inputBoard, Node curr, Node [] inputBlocks ){
       char [][] solution = inputBoard;
    
       board = duplicateBoard(inputBoard);
       visited = new boolean[inputBoard.length][inputBoard[0].length];
       blockArr = new Node[inputBlocks.length];
       trace = new LinkedList<char[][]> ();
       breakRecursion = false;
       depth = 0;
       nodes = 0;
       
       for( int i = 0; i < inputBlocks.length; i++ )
         blockArr[i] = inputBlocks[i];
       for(int i=0;i<visited[0].length;i++) {
         for(int j=0;j<visited.length;j++) {
           visited[i][j] = false;
         }
       }
       solve( curr );
       if( breakRecursion )
         solution = trace.get(0);
       /*printTrace(trace);
       System.out.println( trace.size() );
       solution = goalBoard;*/
       return solution;
    }
    
    public static int getNumNodes(){ return nodes; }
    
    public static char[][] solve(Node start) {
        nodes++;
        int row = start.getI();
        int column = start.getJ();
        boolean [][] tempBoard;
        char [][] solBoard=null; //Keeping a copy of the board before making changes.
        Node start1 = start;   
        if(board[row][column] != '_') {
            start = makeNextMove(start);            
        }
        if(isGoalBoard() || breakRecursion) {
            breakRecursion = true;  //if the input is only a solution
            goalBoard = duplicateBoard(board);
            trace.add(duplicateBoard(board));
        }   
        else {
            //System.out.println("Start: "+row+", "+column);
            if(canPlaceBulb(start) && validateBlocks()) {   // To check if bulb can be placed at the random start position.
                tempBoard = copyBoard(visited);
                solBoard = placeBulb(start, 1);    // 1st Recursive call
                board[row][column] = '_';
                updateBoard(start);
                visited = copyBoard(tempBoard);
            } else {
                //System.out.println("SKIP");
                //Node next = makeNextMove( start );
                placeBlank( start, 1 );
            }

            if(!breakRecursion) {
                tempBoard = copyBoard(visited);
                solBoard = placeBlank(start1, 1);  // 2nd recursive call
                visited = copyBoard(tempBoard);
            }
        }
        return solBoard;           //final solution.
    }
    
    public static boolean isGoalBoard(){
        boolean toReturn = true;
        // check that there exists no shades
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
    public static boolean [][] copyBoard(boolean[][] currBoard) {
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
        for(int row=0;row<newBoard.length;row++){
            for(int column=0;column<newBoard[0].length;column++) {
                newBoard[row][column] = currBoard[row][column];
            }
        }
        return newBoard;
    }
    
    public static void printTrace(LinkedList<char[][]> inputTrace){
       for( int i = 0; i < inputTrace.size(); i++ )
          printBoard(inputTrace.get(i));
    }
    
    public static char[][] printBoard(char[][] currBoard) {
        
        for(int row=0;row<currBoard[0].length;row++){
            for(int column=0;column<currBoard.length;column++) {
              System.out.print(currBoard[row][column]);
            }
            System.out.println();
        }
        System.out.println();
        return currBoard;
    }   
    
    /*********************************************************************
     * Given the node, this function will see if a bulb can be placed in that position.
     * If possible, the bulb will be placed and the board will be updated.
     * and Then the 2 recursive calls again.
     *********************************************************************/
    public static char[][] placeBulb(Node current, int depth) {
        nodes++;
        int row = current.getI();
        int column = current.getJ();
        boolean[][] tempBoard;
        Node nextNode = null;
        char [][] solBoard=null;
        depth++;
        if(isGoalBoard() || breakRecursion ||nodes>200000000) {
            goalBoard = duplicateBoard(board);
            breakRecursion = true;
            trace.add(duplicateBoard(board));

        }
        else { 
            if(canPlaceBulb(current) && !breakRecursion && current!=null) {
                tempBoard = copyBoard(visited);
                board[row][column] = 'b';
                updateBoard(current);
                if(isGoalBoard()) {
                    trace.add(duplicateBoard(board));
                    goalBoard = duplicateBoard(board);
                    breakRecursion=true;
                }
                nextNode = makeNextMove(current);
                if(nextNode!=null && !breakRecursion) {
                    solBoard = placeBulb(nextNode, depth++);
                    board[nextNode.getI()][nextNode.getJ()] = '_';
                    updateBoard(nextNode);
                    visited = copyBoard(tempBoard);
                }
                
            }
            if(!breakRecursion && nextNode!=null) {
                tempBoard = copyBoard(visited);
                solBoard = placeBlank(nextNode, depth++);
                visited = copyBoard(tempBoard);
            }
        }
        return solBoard;
    }
    
    /*********************************************************************
     * Given the node, if the bulb cant be placed there, it will move to the next 
     * blank place in the board .
     * and Then call the 2 recursive calls again.
     *********************************************************************/
    public static char[][] placeBlank(Node current, int depth) {
        nodes++;
        int row = current.getI();
        int column = current.getJ();
        boolean[][] tempBoard;
        char [][] solBoard = null;
        depth++;
        if(isGoalBoard() || breakRecursion || nodes>200000000) {
            breakRecursion = true;
            trace.add(duplicateBoard(board));
        }
        else {  
            Node nextNode = makeNextMove(current);
            if(nextNode!=null) {
                tempBoard = copyBoard(visited);
                solBoard = placeBulb(nextNode, depth++);
                if(isGoalBoard()) {
                    goalBoard = duplicateBoard(board);
                    breakRecursion=true;
                }
                visited = copyBoard(tempBoard);
                board[nextNode.getI()][nextNode.getJ()] = '_';
                updateBoard(nextNode);
                tempBoard = copyBoard(visited);
                solBoard = placeBlank(nextNode, depth++);
                visited = copyBoard(tempBoard);
            }
        }
        return solBoard;
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
            System.out.println(scanner.nextLine() );
          
            String [] tokens = scanner.nextLine().split("\\s+");
            board = new char [Integer.parseInt(tokens[0])+2][Integer.parseInt(tokens[1])+2];
            visited = new boolean[board[0].length][board.length];
            for(int i=0;i<visited[0].length;i++) {
                for(int j=0;j<visited.length;j++) {
                    visited[i][j] = false;
                }
            }
            System.out.println( Integer.parseInt(tokens[0]) + " " + Integer.parseInt(tokens[0]) );
          
            for( int i = 0; i < board.length; i++ ) {
                for( int j = 0; j < board[0].length; j++ ){
                    if( i == 0 || j == 0 ) {
                        board[i][j] = '@';
                    }
                    if( i == board.length-1 || j == board[0].length-1 ) {
                        board[i][j] = '@';
                    }
                }
            }
          
            int rows = 1;
            int numBlocks = 0;
            while (scanner.hasNextLine()) {
                String tempStr = scanner.nextLine();
                for( int cols = 1; cols < board[0].length-1; cols++ ){
                    board[rows][cols] = tempStr.charAt(cols-1);
                    if( Character.isDigit(tempStr.charAt(cols-1))){
                        numBlocks++;
                    }
                }
                rows++;
            }
            blockArr = new Node[numBlocks];
          
            int index = 0;
            for( int i = 0; i < board.length; i++ ) {
                for( int j = 0; j < board[0].length; j++ ) {
                    if( Character.isDigit(board[i][j]) ) {
                        blockArr[index] = new Node( i, j, null, null );
                        index++;
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
      if( board[row][column] == '@' || board[row][column] == 'b' || board[row][column] == 'o' ) {
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
  
  
  public static void updateBoard( Node node )
  {
    
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
  
//   public static Node nextConstrainingMove(Node current) {
//         Node nextNode = null;
//         int i = current.getI();
//         int j = current.getJ();
//         boolean toBreak = false;
//         boolean cycle = false;
//         int maxRow = 0;
//         int maxCol = 0;
//         int litBefore = 0;
//         int litAfter = 0;
//         int maxLit = 0;
//         // check if it's the end of the cols. If it is, then we increment row by 1 and set column as 1, otherwise, just increment column by 1.
//         do
//         { 
//           if(visited[i][j]) {
//               cycle = true;
//               break;
//           }
//           visited[i][j] = true;
//           if( j == board[0].length - 1 )
//           {
//             j = 1;
//             if( i == board.length - 1 )
//               i = 1;
//             else
//               i++;
//           }
//           else
//             j++;
//           if(board[i][j]=='_') {
//               litBefore = countLitArea();
//               board[i][j]='b';
//               updateBoard();
//               litAfter = countLitArea();
//               board[i][j]='_';
//               updateBoard();
//               if((litAfter-litBefore)> maxLit) {
//                   maxLit = litAfter-litBefore;
//                   maxRow = i;
//                   maxCol = j;
//               }
//             }
//         }while ( current.getI()!=i && current.getJ()!=j); 
//         nextNode = new Node(i,j,current, new LinkedList<Node>());
//         return nextNode;
//     }
  
}