import java.util.Scanner;
import java.util.Random; 
import java.io.IOException;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.graphics.TextGraphics;

import mypackages.shape;
import mypackages.board;
import mypackages.offset;

//class Main starts
public class Main{

    Node undo_h;  // head of undo list
    Node redo_h;  //head of redo list
    static int R; //Rows of output board
    static int C; //Cols of output board
    private static int score; //game score
    
	//private DefaultTerminalFactory terminalFactory = null;
	//private Terminal terminal = null;

    Main()  
    {
        undo_h = redo_h = null;
        board bd = new board();
        R = bd.getRows();
        C = bd.getCols();
        score = 0;
    }
    /* Linked list Node.  This inner class is made static so that 
       main() can access it */
    static class Node { 
        int block[][] = new int[4][2];
        char mat[][] = new char[R][C];
        int colsOccupied[] = new int[R-1];
        int ox, oy; //px, py;
        int score;
        Node next; 

        // Constructor
        Node(int block[][], char mat[][], int colsOccupied[],int ox, int oy, int score,Node h)
        {
            for(int i = 0; i < 4; i++)
            {
                this.block[i][0] = block[i][0];
                this.block[i][1] = block[i][1];
            }
            for(int i = 0;  i < R; i++)
                for(int j = 0; j < C; j++)
                    this.mat[i][j] = mat[i][j];
            for(int i = 0; i < R-1; i++)
               this.colsOccupied[i] = colsOccupied[i];
            
            this.ox = ox;
            this.oy = oy;
            this.score = score;
            next = h; 
        }  
    } 
    
    public static void main(String[] args) throws IOException
    {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
	    Terminal terminal = terminalFactory.createTerminal();
        terminal.enterPrivateMode();
        terminal.setCursorVisible(false);
        final TextGraphics textGraphics = terminal.newTextGraphics();

        Random rand = new Random();  //creating object of class Random
        int ran_num = randomNum(rand); //getting a random no between 0-6 
        
        shape shp = new shape();     //creating object of class shape
        shp.initBlock(ran_num);//initializing block array using random no between 0-6
        
        offset offs = new offset();  //creating object of class offset
        offs.set(ran_num);  //setting the values of offset and prev
        
        board bd = new board();  //creating object of class board
        //transferring the block to the board
        bd.moveToBoard(shp.block, offs.offsetX, offs.offsetY);
        
        //System.out.println("SCORE:" + score);
        bd.display(terminal, textGraphics, score);

        Main undo_redo = new Main();
        undo_redo.undo_h = new Node(shp.block,bd.mat,bd.colsOccupied,offs.offsetX,
                                    offs.offsetY, score, undo_redo.undo_h);
        
        char option = 's'; //var to store user input key
        int flag = 1;//flag tht keeps record if there is space on the board for the next move 
        Scanner sc = new Scanner(System.in);
        
        
        
         while(score >= 0)
         {
              KeyType key;
              KeyStroke ks;
              try {
                //while(true) {
                    //game.displayBoard();
                    ks = getBlockingInput(terminal);
                    key = ks.getKeyType();
                    if(key == KeyType.ArrowUp) {
                        option = 'w';
                    } else if(key == KeyType.ArrowLeft) {
                        option = 'a';
                    } else if(key == KeyType.ArrowRight) {
                        option = 'd';
                        // game.moveShapeRight();
                    } else if(key == KeyType.ArrowDown) {
                        option = 's';
                    }else if(key == KeyType.Character && ks.getCharacter().equals('z')){
                        option = 'z';
                    }else if(key == KeyType.Character && ks.getCharacter().equals('x')){
                        option = 'x';
                    }
    
            } catch(Exception e) {
                e.printStackTrace();
            }
           /* String tempStr = sc.next();
            option = tempStr.charAt(0);*/
            
            if(option == 'z' || option == 'x')
            {
              if(option == 'z' && undo_redo.undo_h != null)
              {  
                  shp.setStackBlock(undo_redo.undo_h.block);
                  bd.setStackBoard(undo_redo.undo_h.mat);
                  bd.setStackColsOccupied(undo_redo.undo_h.colsOccupied);
                  offs.set(undo_redo.undo_h.ox,undo_redo.undo_h.oy);
                  score = undo_redo.undo_h.score;
                /*System.out.println("Saved one-");
                for(int i =0 ; i < R; i++)
                {
                    for(int j = 0; j < C; j++)
                        System.out.print(undo_redo.undo_h.mat[i][j]);
                    System.out.println();    
                }*/
                  Node tempN = undo_redo.undo_h;
                  undo_redo.undo_h = undo_redo.undo_h.next;
                  tempN.next = undo_redo.redo_h;
                  undo_redo.redo_h = tempN;
              }
              else if(option == 'x' && undo_redo.redo_h != null)
              {
                  shp.setStackBlock(undo_redo.redo_h.block);
                  bd.setStackBoard(undo_redo.redo_h.mat);
                  bd.setStackColsOccupied(undo_redo.redo_h.colsOccupied);
                  offs.set(undo_redo.redo_h.ox,undo_redo.redo_h.oy);
                  score = undo_redo.redo_h.score;

                  Node tempN = undo_redo.redo_h;
                  undo_redo.redo_h = undo_redo.redo_h.next;
                  tempN.next = undo_redo.undo_h;
                  undo_redo.undo_h = tempN;
              }
              
              flag = 1; 
            }

          else
          {
            if(option == 'w' || option == 'e')
            {
                shp.copyOfBlock(); //to make a current copy of the block   
                
                if(option == 'w')
                    shp.leftRotate(); //for 'w' rotate left
                else
                    shp.rightRotate(); //for 'e' rotate right
                
             //making space on the board at the prev pos of the block i.e at temp coords
                bd.makePrevSpace(shp.temp, offs.offsetX, offs.offsetY); 
            }
            //remember its OR here,so the condition can run even if flag = 0 && option = 's'
            if(flag == 1 || option == 's')//flag is 1 only if block moves from prev position
            {
                move(option, offs, flag);  //to change offset & prev values acc to dir & flag
                
                //making space on the board at the prev position of the block
                bd.makePrevSpace(shp.block, offs.prevx, offs.prevy);
            }

            //if there is space on the board to move then move the block
            if(bd.isPossibleToMove(shp.block, offs.offsetX, offs.offsetY) ){   
                System.out.println("In moving");
                bd.moveToBoard(shp.block, offs.offsetX, offs.offsetY);
                undo_redo.undo_h = new Node(shp.block,bd.mat,bd.colsOccupied,offs.offsetX,
                                    offs.offsetY, score, undo_redo.undo_h);
                undo_redo.redo_h = null;                    
                flag = 1;
            }
            else  //else move the prev block to the board 
            {                                                               
                flag = 0;  //flag is set to zero if we are unable to move the block
                if(option == 'w' || option == 'e'){
                    bd.moveToBoard(shp.temp, offs.offsetX, offs.offsetY);
                    undo_redo.undo_h = new Node(shp.temp,bd.mat,bd.colsOccupied,offs.offsetX,
                                    offs.offsetY, score, undo_redo.undo_h);
                }
                else    
                    bd.moveToBoard(shp.block, offs.prevx, offs.prevy);
                
                if(option == 's')  //if block cannot movdown then generate new block randomly
                {
                //updating the no of cols in each row the block occupies
                    bd.updateColsOccupied(shp.block, offs.prevx, offs.prevy);
                    
                //checking if some lines are to be removed & shited & accord updating score
                    score = bd.checkForShifting(score, offs.prevx); 
                    
                    //initializing new block and then repeating the same steps
                    ran_num = randomNum(rand);
                    shp.initBlock(ran_num);
                    offs.set(ran_num); 

                    if(bd.isPossibleToMove(shp.block, offs.offsetX, offs.offsetY)){
                        bd.moveToBoard(shp.block,offs.offsetX, offs.offsetY);
                        System.out.println("Block Created");
                    }
                    else{  //if it is not possible to add more blocks then game ends
                        //System.out.println("Game Ends");
                        score = -1;
                    }
                    undo_redo.undo_h = new Node(shp.block,bd.mat,bd.colsOccupied,
                                            offs.offsetX, offs.offsetY, score, undo_redo.undo_h);
                    undo_redo.redo_h = null;                                
                    flag = 1;
                }
            }
          } 
            System.out.println("SCORE:" + score);
            bd.display(terminal, textGraphics, score);
            bd.displayColsOcuupied();
        }
        sc.close(); //closing the scanner
    }

    //returning random no between 0 to 6 
    public static int randomNum(Random rand)
    {
        return rand.nextInt(7);
    }

    public static KeyStroke getBlockingInput(Terminal terminal) throws IOException {
		return terminal.readInput();
	}

	public static KeyStroke getNonBlockingInput(Terminal terminal) throws IOException {
		return terminal.pollInput();
	}

    //updating offset and prev values acc to option and flag
    public static void move(char option, offset offs, int flag)
    {
        if(flag == 1){   //if block moves i.e flag=1, then store offset values in prev
            offs.prevx = offs.offsetX;
            offs.prevy = offs.offsetY;
        }
        else{                 //else if flag is zero means block prev position is not changed
            offs.offsetX = offs.prevx; //and that means offset values equals to prev values
            offs.offsetY = offs.prevy;
        } 

        if(option == 's'){ //for s block moves downwards
            offs.offsetX++;
        }
        else if(option == 'a'){ //for a block moves left
            offs.offsetY--;
        }
        else if(option == 'd'){  //for d block moves right
            offs.offsetY++;
        }
    }
}