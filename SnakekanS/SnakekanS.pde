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
color green;
color red;

PFont font; 

boolean needReset;   //bool to signal reset after death


//-----------------------------------------------------------------

void setup()
{
  size(500,600);
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

void draw()
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
void keyPressed()
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
char[] removeFirst(char array[])
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
void ifEaten()
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
 void death()
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
void resetGame()
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