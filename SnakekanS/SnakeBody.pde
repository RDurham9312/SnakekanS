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
 void printSnake()
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
 void display()
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
 void move(int xbound, int ybound)
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
 void changeDirection(int index)
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
 void headChange(char input)
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
 boolean collisionDetection(int objX, int objY )
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
 boolean checkBodyHit()
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
 void reset(int x, int y)
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
 void grow()
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
 void setNewSegment(SnakeSegment newSS, SnakeSegment prevSS)
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
 void bodyShrink()
 {
   size = 1;
 }
  
  
 /******************************************************
 *name: bodyReverse
 *purpose: reverse snake body
 *******************************************************/
 void bodyReverse()
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