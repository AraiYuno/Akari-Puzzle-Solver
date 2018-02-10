//=================================================================================
// Author: Kyle Seokjin Ahn
//
//
// Akari Puzzle Sover:
//   This program solves Akari puzzle. It needs .txt file as as an input.
//   This program contains 3 different heuristics.
//     H1: Random Node Selection -> Choosing the next node randomly
//     H2: Most Constrained Node Selection -> Choosing the node that has the least possible option
//     H3: Most Constraining Node Selection -> Choosing the node that reduces the
//                                             max number of bulbs on the board.
// If you have any questions, please email me at ahns@myumanitoba.ca
//==================================================================================

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

// Functons
// 1. main
// 2. isGoalBoard()
// 3. validateBlocks()
// 4. canPlaceBulb(Node node)
// 5. checkOneDirection(int row, int column)
// 6. updateBoard(Node node)
// 7. printBoard( char [][] board )
// 8. generateBoard()
// 9. copyBoard(char [][] inputBoard )
// 10. randomlyMakeNextMove( Node node ) : returns next block from the blockArr[].
// 11. makeAllPossibleChildrenNodes(Node node): makes all possible children nodes for the input node.
//                                                                    This possible nodes mean the possible combinations for bulbs around the block


public class Forward_Checking
{
  // forward checking algorithm
  private static int numNodes = 0;
  private static char [][] boardBeforeSolving;
  private static char [][] board;
  private static Node blockArr [];
  private static Stack<Node> notVisitedBlocks;
  private static Stack<Node> visitedBlocks;
  private static char [][] solution = null;
  private static long averageNodes = 0;
  
  //Backtracking Algorithm to finish the board
  
  
  public static void main(String[]args) {
    generateBoard();
  }
  public static void solve()
  {
    boardBeforeSolving = copyBoard(board);
    // H1: RANDOM HEURISTIC
    long startTime = System.nanoTime();
    forwardCheckingRandomHeuristic(blockArr[0]);
    long endTime   = System.nanoTime();
    long totalTime = endTime - startTime;
    //System.out.println("==============================================\nH1: Forward-Checking Random Heuristic\n==============================================\n");
    if( solution != null ){
      System.out.println("Solution Found!");
      printSolution(solution);
      System.out.println("H1 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      //System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    else{
      System.out.println("Solution Not Found!");
      System.out.println("H1 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    averageNodes += (numNodes+MyBackTrack.getNumNodes());
    
    // H2: MOST CONSTRAINED HEURISTIC
    //System.out.println("==============================================\nH2: Forward-Checking Most Constrained Heuristic\n==============================================\n");
    solution = null;
    numNodes = 0;
    board = copyBoard(boardBeforeSolving);
    startTime = System.nanoTime();
    forwardCheckingMostConstrained();
    endTime   = System.nanoTime();
    totalTime = endTime - startTime;
    if( solution != null ){
      //System.out.println("Solution Found!");
      //printSolution(solution);
      System.out.println("H2 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      //System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    else{
      System.out.println("Solution Not Found!");
      System.out.println("H2 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    averageNodes += (numNodes+MyBackTrack.getNumNodes());
    
    
    // H3: MOST CONSTRAINING HEURISTIC
    //System.out.println("==============================================\nH3: Forward-Checking Most Constraining Heuristic\n==============================================\n");
    visitedBlocks = null;
    notVisitedBlocks = null;
    numNodes = 0;
    solution = null;
    board = copyBoard(boardBeforeSolving);
    startTime = System.nanoTime();
    forwardCheckingMostConstraining();
    endTime   = System.nanoTime();
    totalTime = endTime - startTime;
    if( solution != null ){
      //System.out.println("Solution Found!");
      //printSolution(solution);
      System.out.println("H3 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      //System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    else{
      System.out.println("Solution Not Found!");
      System.out.println("H3 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    averageNodes += (numNodes+MyBackTrack.getNumNodes());
    
    
    // H2+H3: Doule Heuristic
    //System.out.println("==============================================\nH4: H2 + H3 \n==============================================\n");
    visitedBlocks = null;
    notVisitedBlocks = null;
    numNodes = 0;
    solution = null;
    board = copyBoard(boardBeforeSolving);
    startTime = System.nanoTime();
    doubleHeuristic();
    endTime   = System.nanoTime();
    totalTime = endTime - startTime;
    if( solution != null ){
      //System.out.println("Solution Found!");
      //printSolution(solution);
      System.out.println("H2 + H3 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      //System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    else{
      System.out.println("Solution Not Found!");
      System.out.println("H2 + H3 -> Number of visited nodes is " + (numNodes+MyBackTrack.getNumNodes()));
      System.out.println("Time Elapsed: "+ totalTime + " ns"+"\n");
    }
    averageNodes += (numNodes+MyBackTrack.getNumNodes());
    
    System.out.println();
    System.out.println();
  }
  //==================================================================
  // forwardCheckingRandomHeuristic( Node curr )
  //  solves Akari puzzle by choosing random next node. H1.
  //==================================================================
  public static boolean forwardCheckingRandomHeuristic( Node curr ){
    numNodes++;
    // In this case, we have completed the cycle, and we now need to look for any '_' by ourselves
    char [][] originalBoard = copyBoard(board);
    boolean toReturn = false;
    if( curr ==  blockArr[blockArr.length-1] && !isGoalBoard() ){
      //fillInBlanks();
      board = MyBackTrack.randomBackTracking( board, new Node(1, 1, null, null), blockArr);
    }
    
    if( isGoalBoard() ){
      solution = copyBoard(board);
      return true;
    }
    else if( curr != null && solution == null ){
      makeAllPossibleChildrenNodes( curr ); // make all combinations of the curr block's bulbs
      Node next = randomlyMakeNextMove( curr );
      if( curr.getCombination().size() == 0 && validateBlocks() )
        forwardCheckingRandomHeuristic( next );
      else if(curr!= null) {
        for( int i = 0; i < curr.getCombination().size(); i++ ) {
          board = copyBoard(originalBoard);
          boolean boardStatus1 = placeBulb( curr.getCombination().get(i) );     // place bulbs for the ith combination around the current block.
          boolean boardStatus2 = validateBlocks();
          if(  boardStatus1 && boardStatus2  ){
            toReturn = forwardCheckingRandomHeuristic( next );
          }
          else if( i == curr.getCombination().size()-1 && boardStatus2 )
            toReturn = forwardCheckingRandomHeuristic( next );
          else {
            placeBlank( curr.getCombination().get(i) );
          }
          board = copyBoard(originalBoard);
        }
      }
    }
    board = copyBoard(originalBoard);;
    return toReturn;
  }
  
  
  //===================================================================
  //forwardCheckingMostContrained
  //  solves Akari puzzle by choosing the most constraining nodes -> H2
  //===================================================================
  public static void forwardCheckingMostConstrained(){
    // arrange blocks in descending order by the number on the block
    notVisitedBlocks = new Stack<Node>();
    visitedBlocks = new Stack<Node>();
    Node tempNode;
    Node [] tempBlocks = new Node[blockArr.length];
    for( int i = 0; i < blockArr.length; i++ ){
      if( board[blockArr[i].getI()][blockArr[i].getJ()] != '0' )
        notVisitedBlocks.add( blockArr[i] );
      tempBlocks[i] = blockArr[i];
    }
    
    forwardCheckingMostConstrained( blockArr[0] );
  }
  
  //==================================================================
  // forwardCheckingMostConstrained( Node curr )
  //  solves Akari puzzle by choosing a most constrained next no
  //==================================================================
  public static boolean forwardCheckingMostConstrained( Node curr ){
    numNodes++;
    Node next = null;
    // In this case, we have completed the cycle, and we now need to look for any '_' by ourselves
    char [][] originalBoard = copyBoard(board);
    boolean toReturn = false;
    if( notVisitedBlocks.size() == 0 && !isGoalBoard() ){
      board = MyBackTrack.randomBackTracking( board, new Node(1, 1, null, null), blockArr);
    }
    
    if( isGoalBoard() ){
      solution = copyBoard(board);
      return true;
    }
    else if( curr != null && solution == null ){
      makeAllPossibleChildrenNodes( curr ); // make all combinations of the curr block's bulbs
      next = makeMostConstrainedNextMove( curr );
      if( curr.getCombination().size() == 0 && validateBlocks() )
        forwardCheckingMostConstrained( next );
      else if(curr!= null) {
        for( int i = 0; i < curr.getCombination().size(); i++ ) {
          board = copyBoard(originalBoard);
          boolean boardStatus1 = placeBulb( curr.getCombination().get(i) );     // place bulbs for the ith combination around the current block.
          boolean boardStatus2 = validateBlocks();
          if(  boardStatus1 && boardStatus2  ){
            toReturn = forwardCheckingMostConstrained( next );
          }
          else if( i == curr.getCombination().size()-1 && boardStatus2 ){
            toReturn = forwardCheckingMostConstrained( next );
          }
          else {
            placeBlank( curr.getCombination().get(i) );
            if( next != null ){
              notVisitedBlocks.push( next );
              visitedBlocks.remove( next );
            }
          }
          board = copyBoard(originalBoard);
        }
      }
    }
    if( next != null ){
      notVisitedBlocks.push( next );
      visitedBlocks.remove( next );
    }
    board = copyBoard(originalBoard);
    return toReturn;
  }
  
  
  //====================================================================
  // makeMostConstrainedNextMoves( Node curr )
  //  returns the next Node with least number of options left
  //====================================================================
  public static Node makeMostConstrainedNextMove( Node curr ){
    // make all possible options for the next move for each blocks
    // to compare which node has the number of options.
    
    Node [] toSort;
    visitedBlocks.push( curr );
    notVisitedBlocks.remove( curr );
    if( notVisitedBlocks.size() != 0 )
      toSort = new Node[notVisitedBlocks.size()];
    else
      toSort = new Node[1];
    for( int i = 0; i < notVisitedBlocks.size(); i++ ){
      makeAllPossibleChildrenNodes( notVisitedBlocks.get(i));
      toSort[i] = notVisitedBlocks.get(i);
    }
    
    // sort in descending order
    Node toSwap = null;
    for( int i = 0; i < toSort.length - 1; i++ ){
      for( int j = i + 1 ; j < toSort.length ; j++ ){
        if( toSort[i].getCombination().size() > toSort[j].getCombination().size() ){
          toSwap = toSort[i];
          toSort[i] = toSort[j];
          toSort[j] = toSwap;
        }
      }
    }
    if( toSort[0] != null ){
      visitedBlocks.push( toSort[0] );
      notVisitedBlocks.remove(toSort[0]);
    }
    return toSort[0];
  }
  
  
  //===================================================================
  //forwardCheckingMostConstraining
  //  solves Akari puzzle by choosing the most constraining nodes -> H3
  //===================================================================
  public static void forwardCheckingMostConstraining(){
    // arrange blocks in descending order by the number on the block
    notVisitedBlocks = new Stack<Node>();
    visitedBlocks = new Stack<Node>();
    Node tempNode;
    Node [] tempBlocks = new Node[blockArr.length];
    for( int i = 0; i < blockArr.length; i++ ){
      if( board[blockArr[i].getI()][blockArr[i].getJ()] != '0' )
        notVisitedBlocks.add( blockArr[i] );
      tempBlocks[i] = blockArr[i];
    }
    
    forwardCheckingMostConstraining( makeMostConstrainingNextMove( blockArr[0] ));
  }
  
  
  //==================================================================
  // forwardCheckingMostConstraining( Node curr )
  //  solves Akari puzzle by choosing a node that reduces the maximum
  //  number of _ on the board.
  //==================================================================
  public static boolean forwardCheckingMostConstraining( Node curr ){
    numNodes++;
    Node next = null;
    // In this case, we have completed the cycle, and we now need to look for any '_' by ourselves
    char [][] originalBoard = copyBoard(board);
    boolean toReturn = false;
    if( (notVisitedBlocks.size() == 0 || curr == null ) && !isGoalBoard()  ){
      board = MyBackTrack.randomBackTracking( board, new Node(1, 1, null, null), blockArr);
    }
    
    if( isGoalBoard() ){
      solution = copyBoard(board);
      return true;
    }
    else if( curr != null && solution == null ){
      makeAllPossibleChildrenNodes( curr ); // make all combinations of the curr block's bulbs
      next = makeMostConstrainingNextMove( curr );
      if( curr.getCombination().size() == 0 && validateBlocks() )
        forwardCheckingMostConstraining( next );
      else if( curr!= null) {
        for( int i = 0; i < curr.getCombination().size(); i++ ) {
          board = copyBoard(originalBoard);
          boolean boardStatus1 = placeBulb( curr.getCombination().get(i) );     // place bulbs for the ith combination around the current block.
          boolean boardStatus2 = validateBlocks();
          next = makeMostConstrainingNextMove( curr );
          if(  boardStatus1 && boardStatus2  ){
            next = makeMostConstrainingNextMove( curr );
            toReturn = forwardCheckingMostConstraining( next );
          }
          else if( i == curr.getCombination().size()-1 && boardStatus2 ){
            next = makeMostConstrainingNextMove( curr );
            toReturn = forwardCheckingMostConstraining( next );
          }
          else {
            placeBlank( curr.getCombination().get(i) );
            if( next != null ){
              notVisitedBlocks.push( next );
              visitedBlocks.remove( next );
            }
          }
          board = copyBoard(originalBoard);
        }
      }
    }
    if( next != null ){
      notVisitedBlocks.push( next );
      visitedBlocks.remove( next );
    }
    board = copyBoard(originalBoard);
    return toReturn;
  }
  
  //=====================================================================
  // makeMostConstrainingNextMove( Node curr )
  //   returns a node that reduces the maximum number of blanks in the board
  //=====================================================================
  public static Node makeMostConstrainingNextMove( Node curr ){
    char [][] originalBoard = copyBoard(board);
    Node toReturn = null;
    visitedBlocks.push( curr );
    notVisitedBlocks.remove( curr );
    int minBlanks = (board.length-2) * (board[0].length -2);
    
    for( int i = 0; i < notVisitedBlocks.size(); i++ ){
      board = copyBoard(originalBoard);
      makeAllPossibleChildrenNodes( notVisitedBlocks.get(i));
      LinkedList<LinkedList<Node>> tempComb = notVisitedBlocks.get(i).getCombination();
      for( int j = 0; j < tempComb.size(); j++ ){
        LinkedList<Node> eachComb = tempComb.get(j);
        boolean validNextMove = placeBulb( eachComb );
        if( minBlanks > getNumBlanks(board) && validNextMove ){
          minBlanks = getNumBlanks(board);
          toReturn = notVisitedBlocks.get(i);
        }
        board = copyBoard(originalBoard); // make sure we have the original board before counting the next combination
      }
    }
    
    return toReturn;
  }
  
  
  //=====================================================================
  // doubleHeuristic()
  //   Forward checking algorithm with H2 + H3 heuristics.
  //=====================================================================
  public static void doubleHeuristic(){
    notVisitedBlocks = new Stack<Node>();
    visitedBlocks = new Stack<Node>();
    Node tempNode;
    Node [] tempBlocks = new Node[blockArr.length];
    for( int i = 0; i < blockArr.length; i++ ){
      if( board[blockArr[i].getI()][blockArr[i].getJ()] != '0' )
        notVisitedBlocks.add( blockArr[i] );
      tempBlocks[i] = blockArr[i];
    }
    
    doubleHeuristic( makeDoubleHeuristicNextMove( blockArr[0] ));
    
  }
  
  //==================================================================
  // forwardCheckingMostConstraining( Node curr )
  //  solves Akari puzzle by choosing a node that reduces the maximum
  //  number of _ on the board.
  //==================================================================
  public static boolean doubleHeuristic( Node curr ){
    numNodes++;
    Node next = null;
    // In this case, we have completed the cycle, and we now need to look for any '_' by ourselves
    char [][] originalBoard = copyBoard(board);
    boolean toReturn = false;
    if( (notVisitedBlocks.size() == 0 || curr == null ) && !isGoalBoard()  ){
      board = MyBackTrack.randomBackTracking( board, new Node(1, 1, null, null), blockArr);
    }
    
    if( isGoalBoard() ){
      solution = copyBoard(board);
      return true;
    }
    else if( curr != null && solution == null ){
      makeAllPossibleChildrenNodes( curr ); // make all combinations of the curr block's bulbs
      next = makeDoubleHeuristicNextMove( curr );
      if( curr.getCombination().size() == 0 && validateBlocks() )
        doubleHeuristic( next );
      else if(curr!= null) {
        for( int i = 0; i < curr.getCombination().size(); i++ ) {
          board = copyBoard(originalBoard);
          boolean boardStatus1 = placeBulb( curr.getCombination().get(i) );     // place bulbs for the ith combination around the current block.
          boolean boardStatus2 = validateBlocks();
          next = makeDoubleHeuristicNextMove( curr );
          if(  boardStatus1 && boardStatus2  ){
            next = makeDoubleHeuristicNextMove( curr );
            toReturn = doubleHeuristic( next );
          }
          else if( i == curr.getCombination().size()-1 && boardStatus2 ){
            next = makeDoubleHeuristicNextMove( curr );
            toReturn = doubleHeuristic( next );
          }
          else {
            placeBlank( curr.getCombination().get(i) );
            if( next != null ){
              notVisitedBlocks.push( next );
              visitedBlocks.remove( next );
            }
          }
          board = copyBoard(originalBoard);
        }
      }
    }
    if( next != null ){
      notVisitedBlocks.push( next );
      visitedBlocks.remove( next );
    }
    board = copyBoard(originalBoard);
    return toReturn;
  }
  
  
  //=====================================================================
  // doubleHeuristicNextMove( Node curr )
  //   first it selects node(s) with the least number of options left, and
  //   out of those it chooses the one that places the maximum number
  //   of lights.
  //=====================================================================
  public static Node makeDoubleHeuristicNextMove( Node curr ){
    Node [] toSort;
    char [][] originalBoard = copyBoard(board);
    visitedBlocks.push( curr );
    notVisitedBlocks.remove( curr );
    Node toReturn = null;
    
    if( notVisitedBlocks.size() != 0 )
      toSort = new Node[notVisitedBlocks.size()];
    else
      toSort = new Node[1];
    for( int i = 0; i < notVisitedBlocks.size(); i++ ){
      makeAllPossibleChildrenNodes( notVisitedBlocks.get(i));
      toSort[i] = notVisitedBlocks.get(i);
    }
    
    // sort in descending order
    Node toSwap = null;
    for( int i = 0; i < toSort.length - 1; i++ ){
      for( int j = i + 1 ; j < toSort.length ; j++ ){
        if( toSort[i].getCombination().size() > toSort[j].getCombination().size() ){
          toSwap = toSort[i];
          toSort[i] = toSort[j];
          toSort[j] = toSwap;
        }
      }
    }
    
    
    // Let's make the list of the nodes with the least options
    if( toSort[0] != null ){
      int index = 0;
      // we only want to choose the least option as 0 if there is no move to make possibly.
      // So, this while loop will go to the next least option other than 0.
      if( toSort[0].getCombination().size() == 0 && toSort[toSort.length-1].getCombination().size() != 0 ){
        while( toSort[index].getCombination().size() == 0 )
          index++;
      }
      // Next possible least option other than 0
      int leastOption = toSort[index].getCombination().size();
      LinkedList<Node> tempList = new LinkedList<Node>();
      while( index < toSort.length && toSort[index].getCombination().size()== leastOption ){
        tempList.add( toSort[index] );
        index++;
      }
      int minBlanks = (board.length-2)*(board[0].length-2);
      for( int i = 0; i < tempList.size(); i++ ){
        board = copyBoard(originalBoard);
        makeAllPossibleChildrenNodes( tempList.get(i));
        LinkedList<LinkedList<Node>> tempComb = tempList.get(i).getCombination();
        for( int j = 0; j < tempComb.size(); j++ ){
          LinkedList<Node> eachComb = tempComb.get(j);
          boolean validNextMove = placeBulb( eachComb );
          if( minBlanks > getNumBlanks(board) && validNextMove ){
            minBlanks = getNumBlanks(board);
            toReturn = tempList.get(i);
          }
          board = copyBoard(originalBoard); // make sure we have the original board before counting the next combination
        }
      }
    }
    
    return toReturn;
  }
  
  
  public static int getNumBlanks(char [][] inputBoard ){
    int countBlanks = 0;
    for( int i = 0; i < inputBoard.length; i++ ){
      for( int j = 0; j < inputBoard[0].length; j++ ){
        if( board[i][j] == '_' )
          countBlanks++;
      }
    }
    return countBlanks;
  }
  
  
  //==================================================================
  // randomlyPlaceBulb( LinkedList<Node> combination )
  //   places bulb(s) around the block depending on the number of the block
  //==================================================================
  public static boolean placeBulb( LinkedList<Node> combination ){
    boolean toReturn = true;
    for( int i = 0; i < combination.size(); i++ ){
      if( canPlaceBulb( combination.get(i) ) ){
        board[ combination.get(i).getI() ][ combination.get(i).getJ() ] = 'b';
        updateBoard( combination.get(i) );
      }
      else
        toReturn = false;
    }
    return toReturn;
  }
  
  
  //==================================================================
  // placeBlank( LinkedList<Node> combination )
  //   places blank(s) around the block depending on the number of the block
  //==================================================================
  public static void placeBlank( LinkedList<Node> combination ){
    for( int i = 1; i < combination.size()-1; i++ ){
      board[ combination.get(i).getI() ][ combination.get(i).getJ() ] = '_';
      updateBoard( combination.get(i) );
    }
  }
  
  
  
  //==================================================================
  // isGoalBoard()
  //   returns true if the board is reached to a goal.
  //==================================================================
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
    return (toReturn && validateBlocks());
  }
  
  
  
  //==================================================================
  // validateBlocks()
  //  this function validates if each block is in valid status, and returns true/false
  //  depending on its validty.
  //==================================================================
  /*********************************************************************
    * Checks every wall in the board to see if its still valid.
    *********************************************************************/
  public static boolean validateBlocks() {
    boolean toReturn1 = true;
    boolean toReturn2 = true;
    int count;
    int max;
    // check for every visited node.
    for( int i = 0; i < blockArr.length; i++ ){
      int rowPos = blockArr[i].getI();
      int colPos = blockArr[i].getJ();
      count = 0;
      max = 0;
      int numBlock = Character.getNumericValue(board[rowPos][colPos]);
      if( board[rowPos - 1][colPos] == 'b' || board[rowPos - 1][colPos] == '_' )
        count++;
      if( board[rowPos + 1][colPos] == 'b' || board[rowPos + 1][colPos] == '_' )
        count++;
      if( board[rowPos][colPos - 1] == 'b' || board[rowPos][colPos - 1] == '_' )
        count++;
      if( board[rowPos][colPos + 1] == 'b' || board[rowPos][colPos + 1] == '_' )
        count++;
      
      if( board[rowPos - 1][colPos] == 'b' )
        max++;
      if( board[rowPos + 1][colPos] == 'b' )
        max++;
      if( board[rowPos][colPos - 1] == 'b' )
        max++;
      if( board[rowPos][colPos + 1] == 'b' )
        max++;
      
      if( max > numBlock )
        toReturn2 = false;
      if(count< numBlock)
        toReturn1 = false;
    }
    return (toReturn1 && toReturn2 );
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
  
  
  //==================================================================
  // updateBoard( Node node )
  //  this function takes in a node and updates a board. Note that you need to make
  //  a change on the global board by manually.
  //==================================================================
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
        //NORTH -> EASTvali
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
      
      while( southI < board[0].length && board[southI][node.getJ()] != '@' && !Character.isDigit(board[southI][node.getJ()]) )
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
        while( eastJ < board[0].length && east_southI < board.length && board[east_southI][eastJ]!= '@' && !Character.isDigit(board[east_southI][eastJ]) )
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
        while( west_southI < board.length && westJ < board.length &&
              board[west_southI][westJ]!= '@' && !Character.isDigit(board[west_southI][westJ]) )
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
  
  //==================================================================
  // printBoard()
  //  this function prints the current board
  //==================================================================
  public static char[][] printBoard(char[][] currBoard) {
    
    for(int row=0;row<currBoard[0].length;row++){
      for(int column=0;column<currBoard.length;column++) {
        System.out.print(currBoard[row][column]);
      }
      System.out.println();
    }
    return currBoard;
  }
  
  //==================================================================
  // generateBoard()
  //  this function creates a global akari board with surrounding walls, @, on all four
  //  sides for convenience in other functions.
  //==================================================================
  public static void generateBoard(){
    try {
      Scanner scanner = new Scanner(new File("a1.txt"));
      
      while(scanner.hasNextLine())
      {
        String line = scanner.nextLine();
        if(line.length()>0 && line.charAt(0) == '#')
        {
          System.out.println( line);
          String [] tokens = scanner.nextLine().split("\\s+");
          board = new char [Integer.parseInt(tokens[0])+2][Integer.parseInt(tokens[1])+2];
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
          solve();
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  
  //====================================================================
  // randomlyMakeNextMove( Node node )
  //   this function returns the next block in line from the blockArr array
  //
  //====================================================================
  public static Node randomlyMakeNextMove( Node node )
  {
    int index = 0;
    Node toReturn;
    // check for the next block in line.
    for( int i = 0; i < blockArr.length; i++ ){
      if( blockArr[i].getI() == node.getI() && blockArr[i].getJ() == node.getJ() ){
        index = i;
        break;
      }
    }
    do {
      
      index++;
    } while( index < board.length && board[blockArr[index].getI()][blockArr[index].getJ()] == '0');
    if( index < blockArr.length )
      toReturn = blockArr[index];
    else if( index == blockArr.length-1 ){
      System.out.println("Have reached the end of the array");
      toReturn = blockArr[index];
    }
    else
      toReturn = null;
    return toReturn;
  }
  
  
  //=================================================================
  // makeAllPossibleChildrenNodes( Node node )
  //   generates nodes for the all possible cominations of nodes for bulbs around
  //   the current block.
  //=================================================================
  public static void makeAllPossibleChildrenNodes(Node node){
    int row = node.getI();
    int column = node.getJ();
    node.setCombinationNull();
    
    
    
    boolean north = false;
    boolean south = false;
    boolean east = false;
    boolean west = false;
    if( board[row-1][column] == '_' )
      north = true;
    if( board[row+1][column] == '_' )
      south = true;
    if( board[row][column+1] == '_' )
      east = true;
    if( board[row][column-1] == '_' )
      west = true;
    
    int index = 0;
    boolean canPlaceBulb2 = false;
    boolean north2 = false;
    boolean south2 = false;
    boolean east2 = false;
    boolean west2 = false;
    if( board[row-1][column] == 'b' )
      index++;
    if( board[row+1][column] == 'b' )
      index++;
    if( board[row][column+1] == 'b' )
      index++;
    if( board[row][column-1] == 'b' )
      index++;
    
    // 4: 0 BULB
    if( board[row][column] == '4' ){
      if( index == 0 && north && south && east && west ){
        LinkedList<Node> allCombinations = new LinkedList<Node>();
        allCombinations.add( new Node(row-1, column, node, null ));
        allCombinations.add( new Node(row+1, column, node, null ));
        allCombinations.add( new Node(row, column+1, node, null ));
        allCombinations.add( new Node(row, column-1, node, null ));
        node.addCombination( allCombinations );
      }
      //
      // 4: 1 BULB -> 1 COMB x 4 CASES
      //
      else if( index == 1 && !north && south && east && west ){
        LinkedList<Node> northAlreadyFilled = new LinkedList<Node>();
        northAlreadyFilled.add( new Node(row+1, column, node, null ));
        northAlreadyFilled.add( new Node(row, column+1, node, null ));
        northAlreadyFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( northAlreadyFilled );
      }
      else if( index == 1 && north && !south && east && west ){
        LinkedList<Node> southAlreadyFilled = new LinkedList<Node>();
        southAlreadyFilled.add( new Node(row-1, column, node, null ));
        southAlreadyFilled.add( new Node(row, column+1, node, null ));
        southAlreadyFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( southAlreadyFilled );
      }
      else if( index == 1 && north && south && !east && west ){
        LinkedList<Node> eastAlreadyFilled = new LinkedList<Node>();
        eastAlreadyFilled.add( new Node(row-1, column, node, null ));
        eastAlreadyFilled.add( new Node(row+1, column, node, null ));
        eastAlreadyFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( eastAlreadyFilled );
      }
      else if( index == 1 && north && south && east && !west ){
        LinkedList<Node> westAlreadyFilled = new LinkedList<Node>();
        westAlreadyFilled.add( new Node(row-1, column, node, null ));
        westAlreadyFilled.add( new Node(row+1, column, node, null ));
        westAlreadyFilled.add( new Node(row, column+1, node, null ));
        node.addCombination( westAlreadyFilled );
      }
      
      //
      // 4: 2 BULBS -> 1 COMB x 6 CASES
      //
      else if( index == 2 && !north && !south && east && west ){
        LinkedList<Node> northAndSouthAlreadyFilled = new LinkedList<Node>();
        northAndSouthAlreadyFilled.add( new Node(row, column+1, node, null ));
        northAndSouthAlreadyFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( northAndSouthAlreadyFilled );
      }
      else if( index == 2 && north && south && !east && !west ){
        LinkedList<Node> eastAndWestAlreadyFilled = new LinkedList<Node>();
        eastAndWestAlreadyFilled.add( new Node(row-1, column, node, null ));
        eastAndWestAlreadyFilled.add( new Node(row+1, column, node, null ));
        node.addCombination( eastAndWestAlreadyFilled );
      }
      else if( index == 2 && !north && south && !east && west ){
        LinkedList<Node> northAndEastAlreadyFilled = new LinkedList<Node>();
        northAndEastAlreadyFilled.add( new Node(row+1, column, node, null ));
        northAndEastAlreadyFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( northAndEastAlreadyFilled );
      }
      else if( index == 2 && north && !south && !east && west ){
        LinkedList<Node> eastAndSouthAlreadyFilled = new LinkedList<Node>();
        eastAndSouthAlreadyFilled.add( new Node(row-1, column, node, null ));
        eastAndSouthAlreadyFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( eastAndSouthAlreadyFilled );
      }
      else if( index == 2 && north && !south && east && !west ){
        LinkedList<Node> southAndWestAlreadyFilled = new LinkedList<Node>();
        southAndWestAlreadyFilled.add( new Node(row-1, column, node, null ));
        southAndWestAlreadyFilled.add( new Node(row, column+1, node, null ));
        node.addCombination( southAndWestAlreadyFilled );
      }
      else if( index == 2 && !north && south && east && !west ){
        LinkedList<Node> westAndNorthAlreadyFilled = new LinkedList<Node>();
        westAndNorthAlreadyFilled.add( new Node(row+1, column, node, null ));
        westAndNorthAlreadyFilled.add( new Node(row, column+1, node, null ));
        node.addCombination( westAndNorthAlreadyFilled );
      }
      
      //
      // 4: 3BULBS -> 1 COMB x 4 CASES
      //
      else if( index == 3 && north && !south && !east && !west ){
        LinkedList<Node> northBulb = new LinkedList<Node>();
        northBulb.add( new Node(row-1, column, node, null ));
        node.addCombination( northBulb );
      }
      else if( index == 3 && !north && south && !east && !west ){
        LinkedList<Node> southBulb = new LinkedList<Node>();
        southBulb.add( new Node(row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      else if( index == 3 && !north && !south && east && !west ){
        LinkedList<Node> eastBulb = new LinkedList<Node>();
        eastBulb.add( new Node(row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 3 && !north && !south && !east && west ){
        LinkedList<Node> westBulb = new LinkedList<Node>();
        westBulb.add( new Node(row, column+1, node, null ));
        node.addCombination( westBulb );
      }
    }
    
    // Here, I am totally assuming that we will be able to place 3 bulbs at least ( valid board ).
    // for 3 => there will be maximum of 4 combinations all the time.
    else if( board[row][column] == '3' ){
      int count = 0;
      
      // if all the 4 sides are _, then we can have 4 combinations. One side _ for each combination.
      if( index == 0 && north && south && east && west ){
        //NORTH MISSING
        LinkedList<Node> northMissing = new LinkedList<Node>();
        northMissing.add( new Node(row+1, column, node, null ));
        northMissing.add( new Node(row, column+1, node, null ));
        northMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northMissing );
        
        LinkedList<Node> southMissing = new LinkedList<Node>();
        southMissing.add( new Node(row-1, column, node, null ));
        southMissing.add( new Node(row, column+1, node, null ));
        southMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( southMissing );
        
        LinkedList<Node> eastMissing = new LinkedList<Node>();
        eastMissing.add( new Node(row-1, column, node, null ));
        eastMissing.add( new Node(row+1, column, node, null ));
        eastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( eastMissing );
        
        LinkedList<Node> westMissing = new LinkedList<Node>();
        westMissing.add( new Node(row-1, column, node, null ));
        westMissing.add( new Node(row+1, column, node, null ));
        westMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( westMissing );
      }
      //
      // 3 : 1 BULB & 3 BLANKS
      //
      else if( index == 1 && !north && south && west && east ){
        LinkedList<Node> northFilledAlready = new LinkedList<Node>();
        northFilledAlready.add( new Node(row+1, column, node, null ));
        northFilledAlready.add( new Node(row, column+1, node, null ));
        node.addCombination( northFilledAlready );
        
        northFilledAlready = new LinkedList<Node>();
        northFilledAlready.add( new Node(row+1, column, node, null ));
        northFilledAlready.add( new Node(row, column-1, node, null ));
        node.addCombination( northFilledAlready );
        
        northFilledAlready = new LinkedList<Node>();
        northFilledAlready.add( new Node(row, column-1, node, null ));
        northFilledAlready.add( new Node(row, column+1, node, null ));
        node.addCombination( northFilledAlready );
      }
      else if( index == 1 && north && !south && west && east ){
        LinkedList<Node> southFilledAlready = new LinkedList<Node>();
        southFilledAlready.add( new Node(row-1, column, node, null ));
        southFilledAlready.add( new Node(row, column+1, node, null ));
        node.addCombination( southFilledAlready );
        
        southFilledAlready = new LinkedList<Node>();
        southFilledAlready.add( new Node(row-1, column, node, null ));
        southFilledAlready.add( new Node(row, column-1, node, null ));
        node.addCombination( southFilledAlready );
        
        southFilledAlready = new LinkedList<Node>();
        southFilledAlready.add( new Node(row, column-1, node, null ));
        southFilledAlready.add( new Node(row, column+1, node, null ));
        node.addCombination( southFilledAlready );
      }
      else if( index == 1 && north && south && !west && east ){
        LinkedList<Node> westFilledAlready = new LinkedList<Node>();
        westFilledAlready.add( new Node(row-1, column, node, null ));
        westFilledAlready.add( new Node(row+1, column, node, null ));
        node.addCombination( westFilledAlready );
        
        westFilledAlready = new LinkedList<Node>();
        westFilledAlready.add( new Node(row-1, column, node, null ));
        westFilledAlready.add( new Node(row, column+1, node, null ));
        node.addCombination( westFilledAlready );
        
        westFilledAlready = new LinkedList<Node>();
        westFilledAlready.add( new Node(row+1, column, node, null ));
        westFilledAlready.add( new Node(row, column+1, node, null ));
        node.addCombination( westFilledAlready );
      }
      else if( index == 1 && north && south && west && !east ){
        LinkedList<Node> eastFilledAlready = new LinkedList<Node>();
        eastFilledAlready.add( new Node(row-1, column, node, null ));
        eastFilledAlready.add( new Node(row+1, column, node, null ));
        node.addCombination( eastFilledAlready );
        
        eastFilledAlready = new LinkedList<Node>();
        eastFilledAlready.add( new Node(row-1, column, node, null ));
        eastFilledAlready.add( new Node(row, column-1, node, null ));
        node.addCombination( eastFilledAlready );
        
        eastFilledAlready = new LinkedList<Node>();
        eastFilledAlready.add( new Node(row+1, column, node, null ));
        eastFilledAlready.add( new Node(row, column-1, node, null ));
        node.addCombination( eastFilledAlready );
      }
      
      //
      // 3: 1 BLOCK and 1 BULB  => 1 combination x 6 cases
      //
      else if( index == 1 && !north && !south && east && west ){
        LinkedList<Node> northOrSouthFilled = new LinkedList<Node>();
        northOrSouthFilled.add( new Node(row, column+1, node, null ));
        northOrSouthFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( northOrSouthFilled );
      }
      else if( index == 1 && north && south && !east && !west ){
        LinkedList<Node> eastOrWestFilled = new LinkedList<Node>();
        eastOrWestFilled.add( new Node(row-1, column, node, null ));
        eastOrWestFilled.add( new Node(row+1, column, node, null ));
        node.addCombination( eastOrWestFilled );
      }
      else if( index == 1 && !north && south && !east && west ){
        LinkedList<Node> northOrEastFilled = new LinkedList<Node>();
        northOrEastFilled.add( new Node(row+1, column, node, null ));
        northOrEastFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( northOrEastFilled );
      }
      else if( index == 1 && north && !south && !east && west ){
        LinkedList<Node> eastOrSouthFilled = new LinkedList<Node>();
        eastOrSouthFilled.add( new Node(row-1, column, node, null ));
        eastOrSouthFilled.add( new Node(row, column-1, node, null ));
        node.addCombination( eastOrSouthFilled );
      }
      else if( index == 1 && north && !south && east && !west ){
        LinkedList<Node> southOrWestFilled = new LinkedList<Node>();
        southOrWestFilled.add( new Node(row-1, column, node, null ));
        southOrWestFilled.add( new Node(row, column+1, node, null ));
        node.addCombination( southOrWestFilled );
      }
      else if( index == 1 && !north && south && east && !west ){
        LinkedList<Node> westOrNorthFilled = new LinkedList<Node>();
        westOrNorthFilled.add( new Node(row+1, column, node, null ));
        westOrNorthFilled.add( new Node(row, column+1, node, null ));
        node.addCombination( westOrNorthFilled );
      }
      
      //
      // 3: 2 BLOCKS and 2 BLANKS
      //
      else if( index == 2 && north && south && !west && !east ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      else if( index == 2 && north && !south && west && !east ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 2 && north && !south && !west && east ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 2 && !north && south && west && !east ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 2 && !north && south && !west && east ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 2 && !north && !south && west && east ){
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( westBulb );
      }
      
      //
      // 3: 2 BULBS, 1 BLOCK and 1 BLANK
      //
      else if( index == 2 && north && !south && !west && !east ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
      }
      else if( index == 2 && !north && south && !west && !east ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      else if( index == 2 && !north && !south && west && !east ){
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 2 && !north && !south && !west && east ){
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      
      // 3: No BULB and 1 BLOCK => 1 COMB x 4 CASES
      else if( index == 0 && !north ){
        LinkedList<Node> northMissing = new LinkedList<Node>();
        northMissing.add( new Node(row+1, column, node, null ));
        northMissing.add( new Node(row, column+1, node, null ));
        northMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northMissing );
      }
      else if( index == 0 && !south ){
        LinkedList<Node> southMissing = new LinkedList<Node>();
        southMissing.add( new Node(row-1, column, node, null ));
        southMissing.add( new Node(row, column+1, node, null ));
        southMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( southMissing );
      }
      else if( index == 0 && !east ){
        LinkedList<Node> eastMissing = new LinkedList<Node>();
        eastMissing.add( new Node(row-1, column, node, null ));
        eastMissing.add( new Node(row+1, column, node, null ));
        eastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( eastMissing );
      }
      else if( index == 0 && !west ){
        LinkedList<Node> westMissing = new LinkedList<Node>();
        westMissing.add( new Node(row-1, column, node, null ));
        westMissing.add( new Node(row+1, column, node, null ));
        westMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( westMissing );
      }
    }
    
    // 2!!!!!!
    // for 2 => 6 maximum combinations
    // Here again, I am assuming that at least 2 cells are opened.
    else if( board[row][column] == '2' ){
      //
      // 2: NO BLANK, NO BLOCK, NO BULBS -> 6 COMB x 1 CASE
      //
      if( index == 0 && north && east && south && west ){
        LinkedList<Node> northSouthMissing = new LinkedList<Node>();
        northSouthMissing.add( new Node(row, column+1, node, null ));
        northSouthMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northSouthMissing );
        
        LinkedList<Node> northEastMissing = new LinkedList<Node>();
        northEastMissing.add( new Node(row+1, column, node, null ));
        northEastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northEastMissing );
        
        LinkedList<Node> northWestMissing = new LinkedList<Node>();
        northWestMissing.add( new Node(row+1, column, node, null ));
        northWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( northWestMissing );
        
        LinkedList<Node> southEastMissing = new LinkedList<Node>();
        southEastMissing.add( new Node(row-1, column, node, null ));
        southEastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( southEastMissing );
        
        LinkedList<Node> southWestMissing = new LinkedList<Node>();
        southWestMissing.add( new Node(row-1, column, node, null ));
        southWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( southWestMissing );
        
        LinkedList<Node> eastWestMissing = new LinkedList<Node>();
        eastWestMissing.add( new Node(row-1, column, node, null ));
        eastWestMissing.add( new Node(row+1, column, node, null ));
        node.addCombination( eastWestMissing );
      }
      //
      // 2: 1 BLOCK && 3 BLANKS -> 3 combinations x 4 CASES = 12
      //
      else if( index == 0 && !north && east && south && west ){
        LinkedList<Node> northEastMissing = new LinkedList<Node>();
        northEastMissing.add( new Node(row+1, column, node, null ));
        northEastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northEastMissing );
        
        LinkedList<Node> northSouthMissing = new LinkedList<Node>();
        northSouthMissing.add( new Node(row, column+1, node, null ));
        northSouthMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northSouthMissing );
        
        LinkedList<Node> northWestMissing = new LinkedList<Node>();
        northWestMissing.add( new Node(row+1, column, node, null ));
        northWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( northWestMissing );
      }
      else if( index == 0 && north && east && !south && west ){
        LinkedList<Node> northSouthMissing = new LinkedList<Node>();
        northSouthMissing.add( new Node(row, column+1, node, null ));
        northSouthMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northSouthMissing );
        
        LinkedList<Node> southEastMissing = new LinkedList<Node>();
        southEastMissing.add( new Node(row-1, column, node, null ));
        southEastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( southEastMissing );
        
        LinkedList<Node> southWestMissing = new LinkedList<Node>();
        southWestMissing.add( new Node(row-1, column, node, null ));
        southWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( southWestMissing );
      }
      else if( index == 0 && north && !east && south && west ){
        LinkedList<Node> northEastMissing = new LinkedList<Node>();
        northEastMissing.add( new Node(row+1, column, node, null ));
        northEastMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( northEastMissing );
        
        LinkedList<Node> southEastMissing = new LinkedList<Node>();
        southEastMissing.add( new Node(row+1, column, node, null ));
        southEastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( southEastMissing );
        
        LinkedList<Node> eastWestMissing = new LinkedList<Node>();
        eastWestMissing.add( new Node(row-1, column, node, null ));
        eastWestMissing.add( new Node(row+1, column, node, null ));
        node.addCombination( eastWestMissing );
      }
      else if( index == 0 && north && east && south && !west ){
        LinkedList<Node> northWestMissing = new LinkedList<Node>();
        northWestMissing.add( new Node(row+1, column, node, null ));
        northWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( northWestMissing );
        
        LinkedList<Node> southWestMissing = new LinkedList<Node>();
        southWestMissing.add( new Node(row-1, column, node, null ));
        southWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( southWestMissing );
        
        LinkedList<Node> eastWestMissing = new LinkedList<Node>();
        eastWestMissing.add( new Node(row-1, column, node, null ));
        eastWestMissing.add( new Node(row+1, column, node, null ));
        node.addCombination( eastWestMissing );
      }
      
      //
      // 2: 1 BULB & 3 BLANKS: 3 COMB x 4 CASES
      //
      else if( index == 1 && !north && south && east && west ){
        LinkedList<Node> southBulb = new LinkedList<Node>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<Node>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> westBulb = new LinkedList<Node>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 1 && north && !south && east && west ){
        LinkedList<Node> northBulb = new LinkedList<Node>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<Node>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> westBulb = new LinkedList<Node>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 1 && north && south && !east && west ){
        LinkedList<Node> northBulb = new LinkedList<Node>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<Node>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> westBulb = new LinkedList<Node>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 1 && north && south && east && !west ){
        LinkedList<Node> northBulb = new LinkedList<Node>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<Node>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<Node>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      
      //
      // 2: 2 BLOCKS & 2 BLANKS: 1 Comb x 6 CASES
      //
      else if( index == 0 && !north && !south && east && west ){
        LinkedList<Node> northSouthMissing = new LinkedList<Node>();
        northSouthMissing.add( new Node(row, column+1, node, null ));
        northSouthMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northSouthMissing );
      }
      else if( index == 0 && !north && south && !east && west ){
        LinkedList<Node> northEastMissing = new LinkedList<Node>();
        northEastMissing.add( new Node(row+1, column, node, null ));
        northEastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( northEastMissing );
      }
      else if( index == 0 && !north && south && east && !west ){
        LinkedList<Node> northWestMissing = new LinkedList<Node>();
        northWestMissing.add( new Node(row+1, column, node, null ));
        northWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( northWestMissing );
      }
      else if( index == 0 && north && !south && !east && west ){
        LinkedList<Node> southEastMissing = new LinkedList<Node>();
        southEastMissing.add( new Node(row-1, column, node, null ));
        southEastMissing.add( new Node(row, column-1, node, null ));
        node.addCombination( southEastMissing );
      }
      else if( index == 0 && north && !south && east && !west ){
        LinkedList<Node> southWestMissing = new LinkedList<Node>();
        southWestMissing.add( new Node(row-1, column, node, null ));
        southWestMissing.add( new Node(row, column+1, node, null ));
        node.addCombination( southWestMissing );
      }
      else if( index == 0 && north && south && !east && !west ){
        LinkedList<Node> eastWestMissing = new LinkedList<Node>();
        eastWestMissing.add( new Node(row-1, column, node, null ));
        eastWestMissing.add( new Node(row+1, column, node, null ));
        node.addCombination( eastWestMissing );
      }
      
      //
      // 2: 1BULB and 1 BLOCK -> 2 COMB x 6 CASES
      //
      else if( index == 1 && !north && !south && east && west ){
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 1 && north && south && !east && !west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      else if( index == 1 && !north && south && !east && west ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 1 && north && !south && !east && west ){
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
        
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
      }
      else if( index == 1 && north && !south && east && !west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination( northBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 1 && !north && south && east && !west ){
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      
      //
      // 2: 1 BULB, 2 BLOCKS and 1 BLANK -> 1 COMB x 4 CASES
      //
      else if( !north && !south && !east && west && index == 1 ){
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( !north && !south && east && !west && index == 1 ){
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( !north && south && !east && !west && index == 1 ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      else if( north && !south && !east && !west && index == 1 ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
      }
      
      
    }
    
    //
    // for 1 => SIMPLE AS FUCK HERE FINALLY
    // Here again, I am assuming that at least 2 cells are opened.
    else if( board[row][column] == '1' ){
      // ALL 4 sides VALID
      if( index == 0 && north && south && east && west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      
      //
      // 1: 1BLOCK 3 COMB x 4 CASES = 12
      //
      else if( index == 0 && !north && south && east && west ){
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 0 && north && !south && east && west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 0 && north && south && !east && west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 0 &&  north && south && east && !west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      
      
      //
      // 1: 2 BLOCKS -> 2 COMB x 6 CASES = 12
      //
      else if( index == 0 && !north && !south && east && west ){
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 0 && !north && south && !east && west ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 0 && !north && south && east && !west ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 0 && north && !south && !east && west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
        
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
      else if( index == 0 && north && !south && east && !west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
        
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 0 && north && south && !east && !west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
        
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      
      //
      // 1: 3BLOCKS => 1 COMB x 4 CASES = 4
      //
      else if( index == 0 && north && !south && !east && !west ){
        LinkedList<Node> northBulb = new LinkedList<>();
        northBulb.add( new Node( row-1, column, node, null ));
        node.addCombination(northBulb );
      }
      else if( index == 0 && !north && south && !east && !west ){
        LinkedList<Node> southBulb = new LinkedList<>();
        southBulb.add( new Node( row+1, column, node, null ));
        node.addCombination( southBulb );
      }
      else if( index == 0 && !north && !south && east && !west ){
        LinkedList<Node> eastBulb = new LinkedList<>();
        eastBulb.add( new Node( row, column+1, node, null ));
        node.addCombination( eastBulb );
      }
      else if( index == 0 && !north && !south && !east && west ){
        LinkedList<Node> westBulb = new LinkedList<>();
        westBulb.add( new Node( row, column-1, node, null ));
        node.addCombination( westBulb );
      }
    }
  } // end of makeAllPossibleCombinations
  
  //============================================================================
  // printSolution( char [][] inputBoard )
  //   converts any 'o' on the board to '_' and prints the board
  //============================================================================
  public static void printSolution( char [][] inputBoard ){
    for( int i = 1; i < inputBoard.length-1; i++ ){
      for( int j = 1; j < inputBoard[0].length-1; j++ ){
        if( inputBoard[i][j] == 'o' )
          System.out.print('_');
        else
          System.out.print(inputBoard[i][j]);
      }
      System.out.println("");
    }
    
  }
  
  
  //==================================================================
  // copyBoard( char [][] inputBoard )
  //  simple deep copy method.
  //==================================================================
  public static char [][] copyBoard( char [][] inputBoard ){
    char [][] toCopy = new char [inputBoard.length][inputBoard[0].length];
    for( int i = 0; i < inputBoard.length; i++ ){
      for( int j = 0; j < inputBoard[0].length; j++ )
        toCopy[i][j] = inputBoard[i][j];
    }
    return toCopy;
  }
  
  
} // end of Forward_Checking class