/*****************************************
*class: ReverseFood
*purpose: Food type that reverses snake
******************************************/
class ReverseFood extends Food
{
  //constructor
  ReverseFood(int xbound, int ybound)
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
  
    triangle(x+2,y+2,x+9,y+2,x+2,y+9);
    
    fill(drawColor);
  }
  
  
  /******************************************************
  *name: eatenEffect
  *purpose: perform effect of eaten food
  *OVERRIDE
  *******************************************************/
  void eatenEffect(SnakeBody s)
  {
    s.bodyReverse();
    
    int tempcolor = drawColor;
    drawColor = oppositeDrawColor;
    oppositeDrawColor = tempcolor;
  }
    
}//end ReverseFood