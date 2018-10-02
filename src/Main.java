//import java.util.Scanner;
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
    private static int play;  //a flag variable that becomes 0 when game needs to be over
    
	Main()  
    {
        undo_h = redo_h = null;
        board bd = new board();
        R = bd.getRows();
        C = bd.getCols();
    }

    /* This inner class is made static so that main() can access it */
    static class Node {     //Linked list Node for undo redo stack
        int block[][] = new int[4][2];
        char mat[][] = new char[R][C];
        int colsOccupied[] = new int[R-1];
        int ox, oy; //px, py;
        int score;
        Node next; 

        // Constructor
        Node(int block[][], char mat[][], int colsOccupied[],int ox, int oy, int score,Node h)
        {
            for(int i = 0; i < 4; i++){  //storing coords of block on board
                this.block[i][0] = block[i][0];
                this.block[i][1] = block[i][1];
            }
            for(int i = 0;  i < R; i++){  //storing snapshot of matrix
                for(int j = 0; j < C; j++)
                    this.mat[i][j] = mat[i][j];
            }
            for(int i = 0; i < R-1; i++) //storing cols occupied in each row
               this.colsOccupied[i] = colsOccupied[i];
            //storing offset values and score
            this.ox = ox;  
            this.oy = oy;
            this.score = score;
            next = h; 
        }  
    } 
    
    public static void main(String[] args) throws IOException
    {
        //DefaultTerminalFactory class auto-detects that which terminal is most suitable
        //acc to system environment (e.g UnixTerminal or SwingTerminalFrame) 
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
	    Terminal terminal = terminalFactory.createTerminal();
        terminal.enterPrivateMode();  //screen is cleared and scrolling is disabled
        terminal.setCursorVisible(false);
        final TextGraphics textGraphics = terminal.newTextGraphics();
        play = 1;  //initial play is set to 1

        Random rand = new Random();  //creating object of class Random
        int ran_num = randomNum(rand); //getting a random no between 0-6 
        
        shape shp = new shape();     //creating object of class shape
        shp.initBlock(ran_num);//initializing block array using random no between 0-6
        
        offset offs = new offset();  //creating object of class offset
        offs.set(ran_num);  //setting the values of offset and prev
        
        board bd = new board();  //creating object of class board
        //transferring the block to the board
        bd.moveToBoard(shp.block, offs.offsetX, offs.offsetY);
        
        bd.display(terminal, textGraphics, score, play);

        Main undo_redo = new Main();
        undo_redo.undo_h = new Node(shp.block,bd.mat,bd.colsOccupied,offs.offsetX,
                                    offs.offsetY, score, undo_redo.undo_h);
        
        char option = 's'; //var to store user input key
        int flag = 1;//flag tht keeps record if there is space on the board for the next move 
        
        while(play == 1)
        {
            KeyType key;
            KeyStroke ks;
            try {
                ks = getBlockingInput(terminal); //getting input in form of KeyStroke
                key = ks.getKeyType();           //getting KeyType of KeyStroke
                if(key == KeyType.ArrowLeft) {
                    option = 'a';
                } else if(key == KeyType.ArrowRight) {
                    option = 'd';
                } else if(key == KeyType.ArrowDown) {
                    option = 's'; 
                }else if(key == KeyType.Character && ks.getCharacter().equals('n')){
                    option = 'n';  //left rotate
                }else if(key == KeyType.Character && ks.getCharacter().equals('m')){
                    option = 'm';  //right rotate
                }else if(key == KeyType.Character && ks.getCharacter().equals('z')){
                    option = 'z';  //undo
                }else if(key == KeyType.Character && ks.getCharacter().equals('x')){
                    option = 'x';  //redo
                }else if(key == KeyType.Character && ks.getCharacter().equals('e')){
                    option = 'e';  //redo
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            
            if(option == 'z' || option == 'x')   //i.e undo or redo
            {
                if(option == 'z' && undo_redo.undo_h != null) //undo operation
                {  
                  shp.setStackBlock(undo_redo.undo_h.block);
                  bd.setStackBoard(undo_redo.undo_h.mat);
                  bd.setStackColsOccupied(undo_redo.undo_h.colsOccupied);
                  offs.set(undo_redo.undo_h.ox,undo_redo.undo_h.oy);
                  score = undo_redo.undo_h.score;
                
                  Node tempN = undo_redo.undo_h;
                  undo_redo.undo_h = undo_redo.undo_h.next;
                  tempN.next = undo_redo.redo_h;
                  undo_redo.redo_h = tempN;
                }
                else if(option == 'x' && undo_redo.redo_h != null) //redo operation
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
            else if(option == 'e'){
                terminal.exitPrivateMode(); //to exit from terminal
            }

            else   //if not undo or redo
            {
                if(option == 'n' || option == 'm') //if to rotate
                {
                  shp.copyOfBlock(); //to make a current copy of the block in temp   
                
                  if(option == 'n')
                    shp.leftRotate(); //for 'n' rotate left
                  else
                    shp.rightRotate(); //for 'm' rotate right
                
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
                if(bd.isPossibleToMove(shp.block, offs.offsetX, offs.offsetY) )
                {   
                  bd.moveToBoard(shp.block, offs.offsetX, offs.offsetY);
                  undo_redo.undo_h = new Node(shp.block,bd.mat,bd.colsOccupied,offs.offsetX,
                                            offs.offsetY, score, undo_redo.undo_h);
                  //whenever something new is added to the undo stack,redo stack is set NULL
                  undo_redo.redo_h = null;                   
                  flag = 1;
                }
                else  //else move the prev block to the board 
                {                                                               
                  flag = 0;  //flag is set to zero if we are unable to move the block
                  if(option == 'n' || option == 'm')
                  {
                    //if rotation is not possible, move the prev block stored in temp  
                    bd.moveToBoard(shp.temp, offs.offsetX, offs.offsetY);
                    undo_redo.undo_h = new Node(shp.temp,bd.mat,bd.colsOccupied,offs.offsetX,
                                            offs.offsetY, score, undo_redo.undo_h);
                  }
                  else //if movement is not possible, then move the block at prev offset value    
                    bd.moveToBoard(shp.block, offs.prevx, offs.prevy);
                
                  if(option == 's') //if block cannot move down, thn generate new block
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
                    }
                    else{  //if it is not possible to add more blocks then game ends
                        play = 0;   //and play is set to zero
                    }
                    undo_redo.undo_h = new Node(shp.block,bd.mat,bd.colsOccupied,
                                            offs.offsetX, offs.offsetY, score, undo_redo.undo_h);
                    undo_redo.redo_h = null;                                
                    flag = 1;
                  }
                }
            } 
            bd.display(terminal, textGraphics, score, play);
        }
    }
    //main() ends.

    //returning random no between 0 to 6 
    public static int randomNum(Random rand){
        return rand.nextInt(7);
    }
    
    //getting input in form of KeyStroke and returning it
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