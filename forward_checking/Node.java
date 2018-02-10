import java.util.LinkedList;

public class Node
{
  private Node prev;
  private LinkedList<Node> nextMoves;
  private int i, j;
  private boolean isBulb;
  private LinkedList<LinkedList<Node>> allPossibleCombinations;
  private int numBlanks;
  
  public Node( int i, int j, Node prev, LinkedList<Node> nextMoves)
  {
    this.i = i;
    this.j = j;
    this.prev = prev;
    this.nextMoves = nextMoves;
    this.isBulb = false;
    this.numBlanks = 0;
    this.allPossibleCombinations = new LinkedList<LinkedList<Node>>();
  }

  
  
  public void printNextMoves()
  {
    for( int i = 0; i < nextMoves.size(); i++ )
    {
      System.out.println( "Next move "+ i + " :" + nextMoves.get(i).getI() + ", " + nextMoves.get(i).getJ());
    }
  }
  
  public boolean isLeaf()
  {
    return ( nextMoves.size() == 0 );
  }
  
  public void addNextNode( Node node )
  {
    this.nextMoves.add( node );
  }
  
  public void setCombinationNull(){
    this.allPossibleCombinations = null;
    this.allPossibleCombinations = new LinkedList<LinkedList<Node>>();
  }
  
  public void setNumBlanks(int numBlanks ){ this.numBlanks = numBlanks; }
  public void addCombination( LinkedList<Node> oneComb ){ this.allPossibleCombinations.add( oneComb ); }
  public void setI( int i ){ this.i = i; }
  public void setJ( int j ){ this.j = j; }
  public void setNextMoves( LinkedList<Node> nextMoves ){ this.nextMoves = nextMoves; }
  public void setIsBulb( boolean p ){ this.isBulb = p; }
  
  public int getNumBlanks(){ return this.numBlanks; }
  public LinkedList<LinkedList<Node>> getCombination(){ return this.allPossibleCombinations; }
  public int getI(){ return this.i; }
  public int getJ(){ return this.j; }
  public Node getPrev(){ return this.prev; }
  public boolean getIsBulb(){ return this.isBulb; }
  public LinkedList<Node> getNextMoves(){ return this.nextMoves; }
} // end of Node class