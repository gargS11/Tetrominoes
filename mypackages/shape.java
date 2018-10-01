package mypackages;

public class shape
{
    public int block[][]; //to store coords of block of any shape
    public int temp[][]; //to store copy of block before rotation
    
    public shape()
    {
        this.block = new int[4][2];
        this.temp = new int [4][2];
    }
    public void initBlock(int k)
    {
        block[0][0] = block[0][1] = 0;
        if(k == 0){         // ****
            block[1][0] = 0;
            block[1][1] = 1;
            block[2][0] = 0;
            block[2][1] = -1;
            block[3][0] = 0;
            block[3][1] = -2;
        }                     
        else if(k == 1){
            block[1][0] = 1;
            block[1][1] = 0;
            block[2][0] = 0;
            block[2][1] = 1;
            block[3][0] = -1;
            block[3][1] = 1;
        }
        else if(k == 2){
            block[1][0] = 1;
            block[1][1] = 1;
            block[2][0] = 0;
            block[2][1] = 1;
            block[3][0] = 0;
            block[3][1] = -1;
        }
        else if(k == 3){       // *** 
            block[1][0] = 1;   //  *
            block[1][1] = 0;
            block[2][0] = 0;
            block[2][1] = 1;
            block[3][0] = 0;
            block[3][1] = -1;
        }
        else if(k == 4){     // ** 
            block[1][0] = 1; // **
            block[1][1] = 0;
            block[2][0] = 1;
            block[2][1] = 1;
            block[3][0] = 0;
            block[3][1] = 1;
        }
        else if(k == 5){     // ***
            block[1][0] = 1; // *
            block[1][1] = 0;
            block[2][0] = 0;
            block[2][1] = 1;
            block[3][0] = 0;
            block[3][1] = 2;
        }
        else{
            block[1][0] = 2;
            block[1][1] = 0;
            block[2][0] = 1;
            block[2][1] = 0;
            block[3][0] = 0;
            block[3][1] = 1;
        }
    }

    //making a copy of block in the temp
    public void copyOfBlock()
    {
        for(int i = 0; i < 4; i++)
        {
            temp[i][0] = block[i][0];
            temp[i][1] = block[i][1];
        }
    }
    public void rightRotate()
    {
        for(int i = 0; i < 4; i++){
            int t = block[i][0];
            block[i][0] = block[i][1];
            block[i][1] = t * -1;
        }
    }
    public void leftRotate()
    {
        for(int i = 0; i < 4; i++){
            int t = block[i][0];
            block[i][0] = -block[i][1];
            block[i][1] = t;
        }
    }
    public void setStackBlock(int block[][])
    {
        for(int i = 0; i < 4; i++)
        {
            this.block[i][0] = block[i][0];
            this.block[i][1] = block[i][1];
        }
    }
}
