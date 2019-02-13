import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SnakekanS extends PApplet {

/************************************************************
*   Program: SnakekanS
*    Author: Robert Durham
*   Purpose: personal version of snake with a reverse twist
************************************************************/

SnakeBody snake;          //container for snake

char[] moveInput;         //array to get user input for movement

Food food1;
ReverseFood rfood1;
ShrinkFood sfood1;
MoveFood mfood1;

Food foodObj;      //overall pointer to current food

int score;          //score of Food eaten

int drawColor;          //holders for color switching when reversing
int oppositeDrawColor;
int green;
int red;

PFont font; 

boolean needReset;   //bool to signal reset after death


//-----------------------------------------------------------------

public void setup()
{
  
  frameRate(10);
  
  //initialize objects
  snake = new SnakeBody(width/2, height/2);
  food1 = new Food(width, height);
  rfood1 = new ReverseFood(width, height);
  sfood1 = new ShrinkFood(width, height);
  mfood1 = new MoveFood(width, height);

  foodObj = food1;
  
  font = createFont("Arial",16,true);
  
  moveInput = new char[0];
  
  needReset = false;
  
  score = 0;
  
  green = color(0, 255, 0);
  red = color(212, 0, 11);
  
  drawColor = 0;
  oppositeDrawColor = 255;
}

//---------------------------------------------------------

public void draw()
{
  background(oppositeDrawColor);
  textFont(font);
  
  stroke(drawColor);
  fill(drawColor);
  
  //display score message
  String scoreMessage = "Score:  " + score;
  text(scoreMessage,10,20);
  
  //if statement so head moves once per drawing/frame
  if(moveInput.length != 0)
  {
    //use movement/user input to change head direction
    //  ->working with array to fix issue of head disconnecting if input keys pressed too quickly
    //change head's direction when have user input
    snake.headChange(moveInput[0]);
    
    //remove first element in moveInput
    moveInput = removeFirst(moveInput);
  }

  //move body/head after changing direction of head
  snake.move(width, height);

  //check if snake eaten food
  ifEaten();
  
  //move food (does nothing if not move food object)
  foodObj.move(width, height);
  
  //check if MoveFood moved into snake's mouth/head
  ifEaten();
  
  //display objects
  snake.display();
  foodObj.display();
  
  //check if head hit snake body
  if(snake.checkBodyHit())
  {
    //game over
    death();
  }
  
  //make sure head doesn't say just turned [see SnakeBody.move()]
  snake.snakeContainer[0].justChanged = false;
  
}//end draw() 


/******************************************************
*name: keyPressed
*purpose: check for keyboard input for movement
            and add input to moveInput array
*******************************************************/
public void keyPressed()
{
  if(needReset == true)
  {
    //if space pressed
    if(key == ' ')
      resetGame();
  }
  else //accept input
  {
    if(snake.snakeContainer[0].direction != 'w' && (key == 'w' || keyCode == UP) && snake.snakeContainer[0].direction != 's')
    {
      moveInput = append(moveInput, 'w');
    }
    else if(snake.snakeContainer[0].direction != 'a' && (key == 'a' || keyCode == LEFT) && snake.snakeContainer[0].direction != 'd')
    {
      moveInput = append(moveInput, 'a');
    }
    else if(snake.snakeContainer[0].direction != 's' && (key == 's' || keyCode == DOWN) && snake.snakeContainer[0].direction != 'w')
    {
      moveInput = append(moveInput, 's');
    }
    else if(snake.snakeContainer[0].direction != 'd' && (key == 'd' || keyCode == RIGHT) && snake.snakeContainer[0].direction != 'a')
    {
      moveInput = append(moveInput, 'd');
    }
  }
}//end keyPressed


/******************************************************
*name: removeFirst
*arg: char[] - char array to edit
*return: char[] - editted char array 
*purpose: remove first element in given char array (moveInput)
*******************************************************/
public char[] removeFirst(char array[])
{
  //move each element down in array (decrease index location)
  for(int i = 1; i < array.length; i++)
  {
    array[i-1] = array[i];
  }
  
  //remove last element from array
  return shorten(array);
}


/************************************************
*name: ifEaten
*purpose: check if snake eaten food
*************************************************/
public void ifEaten()
{
  if(snake.collisionDetection(foodObj.x, foodObj.y))
  {
    score += foodObj.score;
    foodObj.eatenEffect(snake);

    //spawn/set new food
    //decide food type through random percent
    float tempf = random(100);  

    //normal food
    if(tempf < 70)
    {
      foodObj = food1;
    }
    //reverse food
    else if(tempf < 85)
    {
      foodObj = rfood1;
    }
    //shrink food
    else if(tempf < 95)
    {
      foodObj = mfood1;
    }
    //move food
    else
    {
      foodObj = sfood1;
    }
    
    //change food coordinates
    foodObj.respawn(width, height);
  }
}


/******************************************************
 *name: death
 *purpose: end game since snake bit itself
 *******************************************************/
 public void death()
 {
   //change color
   fill(red);
  
   //increase font size
   textSize(30);
  
   //display game over message
   text("GAME OVER",170,200);
    
   //reset font size
   textSize(20);
   
   //say to press space to reset
   text("Press SPACE to restart...",170,225);
   
   //signal reset
   needReset = true;
   
   //change color back
   fill(drawColor);
   
   //stop game until reset
   noLoop();
 }


/****************************************
*name: resetGame
*purpose: reset game after death
****************************************/
public void resetGame()
{
  //reset snake
  snake.reset(width/2, height/2);
  
  //reset score
  score = 0;
  
  //reset colors
  drawColor = 0;
  oppositeDrawColor = 255;
  
  //respawn food
  foodObj = food1;
  foodObj.respawn(width, height);
  
  needReset = false;
  loop();
}
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
  public void respawn(int xbound, int ybound)
  {
    //create random coordinates inside screen
    int tempx = PApplet.parseInt(random(xbound));
    int tempy = PApplet.parseInt(random(ybound));
    
    //make coordinates in increments of 10 so inline with snake movement
    x = (tempx / 10) * 10;
    y = (tempy / 10) * 10;
  }
  
  
  /******************************************************
  *name: display
  *purpose: display food
  *******************************************************/
  public void display()
  {
    rect(x,y,10,10);
  }
  
  
  /******************************************************
  *name: eatenEffect
  *purpose: perform effect of eaten food
  *******************************************************/
  public void eatenEffect(SnakeBody s)
  {
    s.grow();
  }


  /*******************************************************
  *name: move
  *args: int xbound, int ybound - placeholders for MoveFood
  *purpose: blank move method to allow MoveFood without needing instanceof in main
  ********************************************************/
  public void move(int xbound, int ybound)
  {
  }
  
}//end Food class
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
  public void display()
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
  public void eatenEffect(SnakeBody s)
  {
    s.grow();
  }
  
  
  /******************************************************
  *name: move
  *args: int xbound - width of canvas
  *      int ybound - height of canvas
  *purpose: move food to new coordinates, decide how far in one direction
  *******************************************************/
  public void move(int xbound, int ybound)
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
      int tempd = PApplet.parseInt(random(4));
      
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
      distance = PApplet.parseInt(random(1, 10));
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
  public void display()
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
  public void eatenEffect(SnakeBody s)
  {
    s.bodyReverse();
    
    int tempcolor = drawColor;
    drawColor = oppositeDrawColor;
    oppositeDrawColor = tempcolor;
  }
    
}//end ReverseFood
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
  public void display()
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
  public void eatenEffect(SnakeBody s)
  {
    s.bodyShrink();
  }
}//end ShrinkFood
/*****************************************
*class: SnakeBody
*purpose: overall snake - container of SnakeSegments
********************************************/
class SnakeBody
{
 //SnakeSegment container
 SnakeSegment[] snakeContainer;

 int size;
  
 //constructor
 //args: int x - x coordinate to start snake, int y - y coordinate to start snake
 SnakeBody(int x, int y)
 {
   snakeContainer = new SnakeSegment[1];
   snakeContainer[0] = new SnakeSegment(x, y);
   size = 1;
 }
  
 /*******************************
 *name:printSnake
 *purpose: print debug info
 ********************************/
 public void printSnake()
 {
   println("Size: "+Integer.toString(size));
   println("x, y, dir, prevdir, jc");
    
   for(SnakeSegment ss : snakeContainer)
   {
     println(Integer.toString(ss.x)+" : "+Integer.toString(ss.y)+" : "+ss.direction+" : "+ss.prevdirection+" : "+ss.justChanged);//+" : "+ss.visible);
   }
    
   println("");
 }
  
 /******************************************************
 *name: display
 *purpose: display all SnakeSegments
 *******************************************************/
 public void display()
 {
   for(int i = 0; i < size; i++)
   {
     snakeContainer[i].display();
   }
 }
  
  
 /******************************************************
 *name: move
 *args: int xbound - width of canvas
 *      int ybound - height of canvas
 *purpose: move all SnakeSegments
 *******************************************************/
 public void move(int xbound, int ybound)
 {
   for(int i = 0; i < size; i++)
   {
     //see if need to change direction of segment (if not first segment)
     if(i != 0)
     {
       changeDirection(i);
     }
      
     //move SnakeSegment
     snakeContainer[i].move(xbound, ybound);
   }
 }
  
  
 /******************************************************
 *name: changeDirection
 *arg: int - index of SnakeSegment in snakeContainer   
 *purpose: compare current segment (index) with previous segment
             to decide if current segment needs to change direction
 *******************************************************/
 public void changeDirection(int index)
 {
   //create previous and current segment objects for easier names
   SnakeSegment prevSeg = snakeContainer[index-1];
   SnakeSegment currSeg = snakeContainer[index];
    
   //check if direction is NOT same and previous did NOT just change direction
   if(prevSeg.direction != currSeg.direction && prevSeg.justChanged == false)
   { 
     //change current's prevdirection
     currSeg.prevdirection = currSeg.direction;
      
     //change direction to previous' new direction
     currSeg.direction = prevSeg.direction;
      
     //set just changed to true
     currSeg.justChanged = true;
   }
   //check if previous' prevdirection is NOT same and previous DID just change direction
   else if(prevSeg.prevdirection != currSeg.direction && prevSeg.justChanged == true)
   {
     //change current's prevdirection
     currSeg.prevdirection = currSeg.direction;
      
     //change direction to previous' previous new direction
     currSeg.direction = prevSeg.prevdirection;
      
     //set just changed to true
     currSeg.justChanged = true;
   }
   else
   {
     //set current's just changed to false
     currSeg.justChanged = false;
   }
 }
  
  
 /******************************************************
 *name: headChange
 *arg: char - user input for new direction
 *purpose: change head (first segment) direction
 *******************************************************/
 public void headChange(char input)
 {
   //set justChanged to true for following segments [see SnakeBody.changeDirection()]
   snakeContainer[0].justChanged = true;
    
   //set previous direction
   snakeContainer[0].prevdirection = snakeContainer[0].direction;
    
   //set new direction
   snakeContainer[0].direction = input;
 }
  
  
 /******************************************************
 *name: collisionDetection
 *arg: int,int - x and y of object to compare to head
 *return: boolean - true if head hit object
 *purpose: detect if head hits other object
 *******************************************************/
 public boolean collisionDetection(int objX, int objY )
 {
   //check if object's coordinates equal snake head's coordinates
   if(objX == snakeContainer[0].x && objY == snakeContainer[0].y)
     return true;
   else
     return false;
 }
  
  
 /******************************************************
 *name: checkBodyHit
 *return: boolean - true if snake hit itself
 *purpose: compare each SnakeSegment to head to check if hit
 *******************************************************/
 public boolean checkBodyHit()
 {
   //for each segment in snake
   for(int i = 1; i < size; i++)
   {
     //if head hit segment, cause death
     if(collisionDetection(snakeContainer[i].x, snakeContainer[i].y))
     {
       return true;
     }
   }
   
   //no collision
   return false;
 }
  
  
 /****************************************************
 *name: reset
 *args: int x - x coordinate to start snake
 *      int y - y coordinate to start snake
 *purpose: reset snake after death
 *******************************************************/
 public void reset(int x, int y)
 {
   //set size back to default size
   size = 1;

   //reset coordinates and direction of head
   snakeContainer[0].direction = 'w';
   snakeContainer[0].x = x;
   snakeContainer[0].y = y;
 }
  
  
 /******************************************************
 *name: grow
 *purpose: increase snake by one SnakeSegment
 *******************************************************/
 public void grow()
 {
   //get last segment of snake
   SnakeSegment prevSeg = snakeContainer[size-1];
    
   //create segment pointer
   SnakeSegment newSeg = null;
    
   //if need new segment object, create it
   if(size == snakeContainer.length)
   {
     //create new segment
     newSeg = new SnakeSegment();
     
     //add new segment to body
     snakeContainer = (SnakeSegment[]) append(snakeContainer, newSeg);
   }
   //else reuse next segment from container
   else
   {
     //get next segement
     newSeg = snakeContainer[size];
   }
    
   //set segment attributes
   setNewSegment(newSeg, prevSeg);
    
   //increase size
   size++;
 }


 /****************************************
 *name: setNewSegment
 *arg: SnakeSegment newSS - segment to set with new values
       SnakeSegment prevSS - segment to get values from
 *purpose: set segment to end of snake for growing
 ******************************************/
 public void setNewSegment(SnakeSegment newSS, SnakeSegment prevSS)
 {
   //set values of newSS
   newSS.x = prevSS.x;
   newSS.y = prevSS.y;
   newSS.direction = prevSS.direction;
    
   //adjust newSS
   newSS.adjustSegment(); 
 }


 /***************************************************
 *name: bodyShrink
 *purpose: shrink snake body back to size 1
 ***************************************************/
 public void bodyShrink()
 {
   size = 1;
 }
  
  
 /******************************************************
 *name: bodyReverse
 *purpose: reverse snake body
 *******************************************************/
 public void bodyReverse()
 {
   //for all segments
   for(int i = size-1; i >= 0; i--)
   {
     //if not first segment (head)
     if(i != 0)
     {
       //if previous segment just changed direction
       if(snakeContainer[i-1].justChanged == true)
       {
         //change current segment's direction
         snakeContainer[i].direction = snakeContainer[i-1].direction;
        
         //change current segment's previous direction
         if(snakeContainer[i-1].direction == 'w')
         {
           snakeContainer[i].prevdirection = 's';
         }
         else if(snakeContainer[i-1].direction == 's')
         {
           snakeContainer[i].prevdirection = 'w';
         }
         else if(snakeContainer[i-1].direction == 'a')
         {
           snakeContainer[i].prevdirection = 'd';
         }
         else if(snakeContainer[i-1].direction == 'd')
         {
           snakeContainer[i].prevdirection = 'a';
         }

         //set previous' just changed to false
         snakeContainer[i-1].justChanged = false;
       }//end if2
     }//end if1

     //reverse direction of segment
     if(snakeContainer[i].direction == 'w')
     {
       snakeContainer[i].direction = 's';
     }
     else if(snakeContainer[i].direction == 's')
     {
       snakeContainer[i].direction = 'w';
     }
     else if(snakeContainer[i].direction == 'a')
     {
       snakeContainer[i].direction = 'd';
     }
     else if(snakeContainer[i].direction == 'd')
     {
       snakeContainer[i].direction = 'a';
     }

   }//end for
    
   //reverse order of segments in array
   SnakeSegment tempseg;
    
   //remember end index
   int end = size - 1;
    
   for(int i = 0; i < size/2; i++)
   {
     tempseg = snakeContainer[i];
     snakeContainer[i] = snakeContainer[end-i];
     snakeContainer[end-i] = tempseg;
   }
 }//end reverse
    
}//end Snake class
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
  public void display()
  {
    rect(x,y,10,10);
  }
  
  
  /******************************************************
  *name: move
  *args: int xbound - width of canvas
  *      int ybound - height of canvas
  *purpose: move segment depending on direction
  *******************************************************/
  public void move(int xbound, int ybound)
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
  public void adjustSegment()
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
  public void settings() {  size(500,600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SnakekanS" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
