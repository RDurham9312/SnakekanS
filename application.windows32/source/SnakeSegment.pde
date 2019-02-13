/*****************************************
*class: SnakeSegment
*purpose: building block for overall snake
******************************************/
class SnakeSegment
{
  int x, y;                    //coordinates for segment/rect
  char direction = 'w';        //char to signify direction to move
  char prevdirection;          //hold previous direction after changing
  boolean justChanged;         //tell if just changed direction (for turning)
  
  //default constructor
  SnakeSegment()
  {
    
  }
  
  //constructor
  SnakeSegment(int newX, int newY)
  {
    x = newX;
    y = newY;
  }
  
  //2nd constructor with direction
  SnakeSegment(int newX, int newY, char newdir)
  {
    x = newX;
    y = newY;
    direction = newdir;
  }
  
  
  /******************************************************
  *name: display
  *purpose: display SnakeSegment
  *******************************************************/
  void display()
  {
    rect(x,y,10,10);
  }
  
  
  /******************************************************
  *name: move
  *args: int xbound - width of canvas
  *      int ybound - height of canvas
  *purpose: move segment depending on direction
  *******************************************************/
  void move(int xbound, int ybound)
  {
    if(direction == 'w')
    {
      //decrease y to move up
      y = y - 10;
      
      //move to bottom if go off canvas
      if(y < 0)
      {
        y = ybound - 10;
      }
    }
    else if(direction == 's')
    {
      //increase y to move down
      y = y + 10;
      
      //move to top if go off canvas
      if(y >= ybound)
      {
        y = 0;
      }
    }
    else if(direction == 'a')
    {
      //decrease x to move left
      x = x - 10;
      
      //move to right if go off canvas
      if(x < 0)
      {
        x = xbound - 10;
      }
    }
    else if(direction == 'd')
    {
      //increase x to move right
      x = x + 10;
      
      //move to left if go off canvas
      if(x >= xbound)
      {
        x = 0;
      }
    }
      
  }//end SnakeSegment move()


  /**************************************
  *name: adjustSegment
  *purpose: adjust segment to be last segment (for growing)
  ***************************************/
  void adjustSegment()
  {
    if(direction == 'w')
    {
      y += 10;
    }
    else if(direction == 'a')
    {
      x += 10;
    }
    else if(direction == 's')
    {
      y -= 10;
    }
    else if(direction == 'd')
    {
      x -= 10;
    }
  }//end adjustSegment
     
}//end SnakeSegment class 