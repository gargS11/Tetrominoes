package mypackages;

import java.io.IOException;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.SGR;

public class board{
    private final int R = 16;  //fixed board length
    private final int C = 15;  //fixed board width
    public char mat[][];      //board matrix
    public int colsOccupied[];//arr to record how many columns in each row are occupied

    //initializing the board through the constructor
    public board()
    {
        this.mat = new char[20][15];
        this.colsOccupied = new int[R-1];

        for(int i = 0; i < C; i++)
            mat[0][i] = mat[R-1][i] = '@';
        for(int i = 1; i < R-1; i++)
            mat[i][0] = mat[i][C-1] = '|';
        for(int i = 1; i < R-1; i++)
            for(int j = 1; j < C-1; j++)
                mat[i][j] = ' ';         
    }
    public int getRows()
    {
        return this.R;
    }
    public int getCols()
    {
        return this.C;
    }
    //adding block to the board
    public void moveToBoard(int block[][], int offx, int offy)
    {
        int x, y;
        for(int i = 0; i < 4; i++){
            x = block[i][0] + offx;
            y = block[i][1] + offy;
            mat[x][y] = '+';
        }
    }
    //displaying board
    /*public void display(){
        for(int i = 0; i < R; i++){
            for(int j = 0; j < C; j++)
                System.out.print(" " + mat[i][j]);
            System.out.println();
        }    
    }*/
    
    //putting spaces at the board in req places
    public void makePrevSpace(int block[][], int prevx, int prevy)
    {
        int x, y;
        for(int i = 0; i < 4; i++) //putting spaces at the prev position of the block
        {
            x = block[i][0] + prevx;
            y = block[i][1] + prevy;
            if(mat[x][y] == ' ' || mat[x][y] == '+'){
                mat[x][y] = ' ';
            }
        }
    } 

    //verifying if movement of block is possible on the board
    public boolean isPossibleToMove(int block[][], int offsetX, int offsetY)
    {
        int x, y;
        for(int i = 0; i < 4; i++) //putting block on the board if all the rqd pos are empty
        {
            x = block[i][0] + offsetX;
            y = block[i][1] + offsetY;
            System.out.println(x + " "+y+" "+mat[x][y]);
        
            if(x > 0 &&  x < R-1 && y < C-1 && y > 0 && mat[x][y] == ' ')
                continue;
            else{
                System.out.println("False");
                return false;    //if not possible return false
            }
        }   
        return true;  //else return true
    }
    //checking if any row is completely filled,if yes,then removing it and incrementing score
    public void updateColsOccupied(int block[][], int prevx, int prevy)
    {
        for(int i = 0; i < 4; i++)
        {
            int x = block[i][0] + prevx;
            colsOccupied[x]++;
        }
    }
    //checking and shifting rows in the output board and returning updated score
    public int checkForShifting(int score, int x)
    {
        x += 2;
        if(x > R-2)
            x = R-2;
        int flag = 0;
        int temp = 1; 
        System.out.println("Shifting x = " + x);   
        for(int i = x; i > 0 ; i--)
        {
            if(colsOccupied[i] == C-2)
            {
                score += 2;
                temp = i;
                flag = 1;
                break;
            }
        }
        if(flag == 1)
        {
            for(int j = temp-1; j > 0; j--)
            {
                if(colsOccupied[j] == 0)
                    break;
                if(colsOccupied[j] != C-2)
                {
                    for(int k = 1; k < C-1; k++)
                        mat[temp][k] = mat[j][k];
                    colsOccupied[temp] = colsOccupied[j];
                    temp--;
                }
                else
                {
                    score += 2;
                }
            }
            for(int j = temp; j > 0; j--)
            {
                if(colsOccupied[j] > 0)
                {
                    for(int k = 1; k < C-1; k++)
                        mat[j][k] = ' ';
                    colsOccupied[j] = 0;
                }    
            }
            //System.out.println("Shifting Completed");
        }
        return score;
    }
    public void displayColsOcuupied()
    {
        for(int i = 0; i < R-1; i++)
        {
            System.out.print(colsOccupied[i] + " ");
        }
        System.out.println();
    }
    public void setStackBoard(char mat[][])
    {
        for(int i = 0; i < R; i++)
        {
            for(int j = 0; j < C; j++)
            {
                this.mat[i][j] = mat[i][j];
            }
        }
    }
    public void setStackColsOccupied(int colsOccupied[])
    {
        for(int i = 0; i < R-1; i++)
        {
            this.colsOccupied[i] = colsOccupied[i];
        }
    }
    /*public void displayBoard() throws IOException {
		this.pasteShape();
		this.terminal.clearScreen();
		int i, j;
		int cols = this.board.getCols(), rows = this.board.getRows();
		for(i = 0, cols += 2; i < cols; i++) {
			this.terminal.putCharacter('-');
		}
		this.terminal.putCharacter('\n');

		for(i = 0, cols -= 2; i < rows; i++) {
			this.terminal.putCharacter('|');
			for(j = 0; j < cols; j++) {
				if(this.board.get(i, j)) {
					this.terminal.putCharacter('*');
				} else {
					this.terminal.putCharacter(' ');
				}
			}
			this.terminal.putCharacter('|');
			this.terminal.putCharacter('\n');
		}

		for(i = 0, cols += 2; i < cols; i++) {
			this.terminal.putCharacter('-');
		}

		this.terminal.putCharacter('\n');
		this.terminal.flush();
		this.clearShape();
    }*/
    public void display(Terminal terminal,TextGraphics textGraphics,int score)throws IOException
    {
        terminal.clearScreen();
        for(int i = 0; i < R; i++)
        {
            for(int j = 0; j < C; j++)
                terminal.putCharacter(mat[i][j]);
            terminal.putCharacter('\n');
        }
        terminal.putCharacter('\n');
        if(score >= 0){
            String s = "Score : " + Integer.toString(score);
            textGraphics.putString(0, C+2, s, SGR.BOLD);
        }
        else{
            textGraphics.putString(3, C/2, "GAME OVER", SGR.BOLD);
        }
        terminal.flush();
    }
  
}