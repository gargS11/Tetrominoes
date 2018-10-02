package mypackages;

//class for offset values
public class offset{
    public int offsetX, prevx;
    public int offsetY, prevy;
    
    public void set(int shape_no)  //setting initial offset value according to the shape
    {
        prevx = prevy = 1;
        if(shape_no == 1 || shape_no == 2){ //for shape 1 and 2
            offsetX = 2; 
            offsetY = 7;
        }
        else{                 //for other shapes
            offsetX = 1; 
            offsetY = 7;
        }
    }
    public void set(int ox, int oy){
        prevx = prevy = 1;
        offsetX = ox; 
        offsetY = oy;
    }
}