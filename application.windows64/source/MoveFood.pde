/*****************************************
*class: MoveFood
*purpose: Food type that moves around
******************************************/
class MoveFood extends Food
{
  //create variables for moving
  int distance;
  char direction;
  boolean shouldMove;
  
  //constructor
  MoveFood(int xbound, int ybound)
  {
    super(xbound, ybound);
    score = 10;
    shouldMove = true;
    
    //start distance at 0 so random direction chosen
    distance = 0;
  }
  
  /******************************************************
  *name: display
  *purpose: display food
  *OVERRIDE
  *******************************************************/
  void display()
  {
    fill(green);

    rect(x,y,10,10);
    
    fill(drawColor);
  }
  
  
  /******************************************************
  *name: eatenEffect
  *purpose: perform effect of eaten food
  *OVERRIDE
  *******************************************************/
  void eatenEffect(SnakeBody s)
  {
    s.grow();
  }
  
  
  /******************************************************
  *name: move
  *args: int xbound - width of canvas
  *      int ybound - height of canvas
  *purpose: move food to new coordinates, decide how far in one direction
  *******************************************************/
  void move(int xbound, int ybound)
  {
    if(shouldMove == false)
    {
      shouldMove = true;
      return;
    }
    
    //if distance at 0
    if(distance == 0)
    {
      //decide which direction to move
      int tempd = int(random(4));
      
      if(tempd == 0)
      {
        direction = 'w';
      }
      else if(tempd == 1)
      {
        direction = 'a';
      }
      else if(tempd == 2)
      {
        direction = 's';
      }
      else if(tempd == 3)
      {
        direction = 'd';
      }
      
      //decide distance to move in direcion
      distance = int(random(1, 10));
    }
    //else
    else
    {
      //move
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
      
      //decrement distance
      distance--;
    }
    
    //set move to false to slow food down
    shouldMove = false;
  }
}//end MoveFood