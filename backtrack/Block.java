public class Block
{
  char number;
  int i;
  int j;
  
  public Block( char number, int i, int j)
  {
    this.number = number;
    this.i = i;
    this.j = j;
  }
  
  public int getI(){ return this.i; }
  public int getJ(){ return this.j; }
  public char getNumber() { return this.number; }
}