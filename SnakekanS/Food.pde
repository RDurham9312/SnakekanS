/***********************************************************************************
*class: Food
*purpose: object to be eaten by snake head and cause effect depending on food type
************************************************************************************/
class Food
{
  int x, y;          //coordinates for Food
  int score;        //score value for type of Food
  
  //constructor
  Food(int xbound, int ybound)
  {
    score = 1;
    respawn(xbound, ybound);
  }


  /************************************
  *name: respawn
  *purpose: change food coordinates
  ************************************/
  void respawn(int xbound, int ybound)
  {
    //create random coordinates inside screen
    int tempx = int(random(xbound));
    int tempy = int(random(ybound));
    
    //make coordinates in increments of 10 so inline with snake movement
    x = (tempx / 10) * 10;
    y = (tempy / 10) * 10;
  }
  
  
  /******************************************************
  *name: display
  *purpose: display food
  *******************************************************/
  void display()
  {
    rect(x,y,10,10);
  }
  
  
  /******************************************************
  *name: eatenEffect
  *purpose: perform effect of eaten food
  *******************************************************/
  void eatenEffect(SnakeBody s)
  {
    s.grow();
  }


  /*******************************************************
  *name: move
  *args: int xbound, int ybound - placeholders for MoveFood
  *purpose: blank move method to allow MoveFood without needing instanceof in main
  ********************************************************/
  void move(int xbound, int ybound)
  {
  }
  
}//end Food class