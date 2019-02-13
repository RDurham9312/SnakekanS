/*****************************************
*class: ShrinkFood
*purpose: Food type that shrinks snake
******************************************/
class ShrinkFood extends Food
{
  //constructor
  ShrinkFood(int xbound, int ybound)
  {
    super(xbound, ybound);
    score = 5;
  }
  
  
  /******************************************************
  *name: display
  *purpose: display food
  *OVERRIDE
  *******************************************************/
  void display()
  {
    rect(x,y,10,10);
  
    fill(oppositeDrawColor);
  
    rect(x+2,y+2,6,6);
    
    fill(drawColor);
  }
  
  
  /******************************************************
  *name: eatenEffect
  *purpose: perform effect of eaten food
  *OVERRIDE
  *******************************************************/
  void eatenEffect(SnakeBody s)
  {
    s.bodyShrink();
  }
}//end ShrinkFood