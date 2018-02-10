import java.util.LinkedList;

public class Node
{
  private int i, j, lit;
  
  public Node( int i, int j, Node prev, LinkedList<Node> nextMoves)
  {
    this.i = i;
    this.j = j;
  }
  
  public Node(int i, int j, int lit) {
      this.i = i;
      this.j = j;
      this.lit = lit;
    }
  
  

  
  public void setI( int i ){ this.i = i; }
  public void setJ( int j ){ this.j = j; }

  public int getI(){ return this.i; }
  public int getJ(){ return this.j; }
  public int getLit() {return lit;}

} // end of Node class