
package fishtank;

/*
Christopher Badolato
COP 3330 0001
10/24/2017
This assignment will add fish to the fishtank when a spot on the grid is clicked.
You can drag and drop the fish anywhere on the grid. If you click simulate 
the fish will swim around in the fishtank. 
*/



//Imports are listed in full to show what's being used
//could just import javax.swing.* and java.awt.* etc..
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JFrame;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.border.EtchedBorder;
import java.awt.Polygon;

class GlobalVariables {
    public ArrayList<Fish> mFish;
    public FishTank mFishTank;

    private GlobalVariables() {
        mFish = new ArrayList<Fish>();
        mFishTank = new FishTank();
    }

    private static GlobalVariables instance;

    public static GlobalVariables getInstance() {
        if (instance == null){
            instance = new GlobalVariables();
        }
        return instance;
    }
}

class Fish implements Comparable<Fish>{
    
    int mX;
    int mY;
    int mId;
    Color mColor;
    
    public Fish(int id, int x, int y, Color color){
        
        mId = id;
        mX = x;
        mY = y;
        mColor = color;
    }
    
    
    public void paint(Graphics g){
        
            //get integers for fish body.
        int bodyX = (this.mX * 30) + 10;
        int bodyY = (this.mY * 30) + 10;
        int bodyWidth = 15;
        int bodyHeight = 10;
            //create arrays for the tail shape based off of the body position
            //and the values in each box.
        int[] tailX = new int[]{ bodyX - 5, bodyX + 2, bodyX - 5};
        int[] tailY = new int[]{ bodyY, bodyY + (bodyHeight/2), bodyY + bodyHeight};
        int tailPoints = 3;
            //set the color for the tail and body shapes.
        g.setColor(this.mColor);
        g.fillOval(bodyX, bodyY, bodyWidth, bodyHeight);
            //created a new polygon object with the array values
        Polygon polygon = new Polygon(tailX, tailY, tailPoints);
            //fill in the polygon witht the selected color.
        g.fillPolygon(polygon);
                                    
    }
    
    
    public void move(){               
            //creates new random x and y values within one space of the
            //current x and y and adds them to the current x and y
            // moving them within one space of the previous position.
        Random rand = new Random();
        int randx = rand.nextInt(3)-1;
        int randy = rand.nextInt(3)-1;       
        int newx = this.mX + randx;
        int newy = this.mY + randy;
        
        if(!GlobalVariables.getInstance().mFishTank.isOccupied(newx, newy)){ 
                //if the value is outside of the tank return it to the previous spot
                //other wise change it to the new spot in the tank.
            if(newx >=20 || newx <=0){
                return;
            }
            if(newy >= 20 || newy <=0){
                return;
            }
            else{
                this.mX = newx;
                this.mY = newy;                  
            }
        }      
    }
 
    @Override
    public int compareTo(Fish o) {
            //if our current mX and mY of the object we are comparing it to have
            //the same X and Y coordindates will return 0 otherwise 
            //return 1.
        if(this.mX == o.mX && this.mY == o.mY){
            return 0;  
        }          
        return 1;
    }
}

class FishTick extends TimerTask{

    @Override
    public void run() {
     
        if (FishTank.mSimulateStatus){
            
            for (int x=0;x<GlobalVariables.getInstance().mFish.size();x++){
                
                Fish f = GlobalVariables.getInstance().mFish.get(x);
                f.move();
                GlobalVariables.getInstance().mFish.set(x, f);
            }              
            GlobalVariables.getInstance().mFishTank.mDrawPanel.paint();
        }
    }
}

public class FishTank extends javax.swing.JFrame implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener{
    
    private final int mNumRows = 20;
    private final int mNumCols = 20;
    private final int mGridSz = 30;
    
    private int mSelectedFishIndex = -1;
    private boolean mDragged = false;
    
    
    private final int mTopHeight;
           
    JToolBar mToolbar;
    JToggleButton mSimulationButton;
    DrawPanel mDrawPanel;
    
    private int mFishIndex = 0;
    
    static public boolean mSimulateStatus = false;
    
    public static void main(String[] args) {
 
        GlobalVariables global = GlobalVariables.getInstance();
        
        if (global == null){
            System.out.println("Cannot initialize, exiting ....");
            return;
        }
        
    }

    private JToggleButton addButton(String title){
        
        JToggleButton button = new JToggleButton(title);
        button.addItemListener(new ItemListener() {
            
           @Override
           public void itemStateChanged(ItemEvent ev) {
               mSimulateStatus = !mSimulateStatus;
           }
        }); 
        
        this.mToolbar.add(button);
        
        return (button);
    }
    
    public FishTank()
    {  
        JFrame guiFrame = new JFrame();
 
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("MY FISH TANK");
        
        // Create a toolbar and give it an etched border.
        this.mToolbar = new JToolBar();
        this.mToolbar.setBorder(new EtchedBorder());
        
        mSimulationButton = addButton("Simulate");
        this.mToolbar.add(mSimulationButton);
 
        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);
    
        this.mDrawPanel = new DrawPanel(mNumRows, mNumCols, mGridSz);
        
        this.mDrawPanel.setBackground(Color.cyan); 
        this.mDrawPanel.paint();
        
        guiFrame.add(mDrawPanel);
        guiFrame.add(this.mToolbar, BorderLayout.NORTH);
        
        // Add the Exit Action
        JButton button = new JButton("Quit");
        button.setToolTipText("Quit the program");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mToolbar.add(button);
      
        guiFrame.addMouseListener(this);
        guiFrame.addMouseMotionListener(this);
        
        //make sure the JFrame is visible
        guiFrame.setVisible(true);
        
        mTopHeight = guiFrame.getInsets().top + mToolbar.getHeight();               
        
        guiFrame.setSize(mNumRows * mGridSz, mNumCols * mGridSz + mTopHeight);
        
        Timer timer = new Timer("tick", true);
        timer.scheduleAtFixedRate(new FishTick(), Calendar.getInstance().get(Calendar.MILLISECOND), 500);
    }
    
    public boolean isOccupied(int x, int y){       
        boolean occupied = false;
            //create temporary fish object to compare to each fish object to 
            //see if the space is occupied.
        Fish tempFish = new Fish(-1, x, y, Color.BLACK);       
        for(Fish F : GlobalVariables.getInstance().mFish){           
            if(F.compareTo(tempFish) == 0){
                occupied = true;
            }                  
        }       
            //returns whether the space is occupied or not.
        return occupied;       
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
            //checks where on the grid the mouse is being clicked.
        int gridX = (e.getX() / mGridSz);
        int gridY = ((e.getY() - mTopHeight) / mGridSz);
            //creates a random value
        Random rand = new Random();       
        if(!mSimulateStatus){
                //creates random color values for our fish color
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();        
            Color randomColor = new Color(r,g,b);    
                //if the grid is not occupied it will create a new fish object
                //with the selected by mouse x and y values and our selected color
            if(!isOccupied(gridX,gridY)){            
                Fish newFish = new Fish(0, gridX, gridY, randomColor);           
                GlobalVariables.getInstance().mFish.add(newFish);
                    //adds newfish to the arry list.           
                    //prints the fish in the tank with the print method.
                GlobalVariables.getInstance().mFishTank.mDrawPanel.paint();            
            }                              
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {          
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
            //while not in simulation mode
        if(!mSimulateStatus){
                //if drag is true.
            if(mDragged){              
                    //get our dragged x and y values for the grid.
                int gridX = (e.getX() / mGridSz);
                int gridY = ((e.getY() - mTopHeight) / mGridSz);  
            
                if(!isOccupied(gridX, gridY)){             
                        //create a new global Variable
                    GlobalVariables Global  = GlobalVariables.getInstance();
                        //create a temp fish object with the same object attriubutes
                        //as our selected fish object
                    if(mSelectedFishIndex == -1){
                        return;
                    }
                    Fish tempFish = Global.mFish.get(mSelectedFishIndex);
                        //Remove old fish
                    Global.mFish.remove(mSelectedFishIndex);
                        //change the location values of the old fish to the new location
                    tempFish.mX = gridX;
                    tempFish.mY = gridY;   
                       //add that fish to the drawpanel
                    Global.mFish.add(tempFish);                                            
                    GlobalVariables.getInstance().mFishTank.mDrawPanel.paint();
                        //reset dragged to false since we are no longer dragging.
                    mDragged = false;
                    mSelectedFishIndex = -1;
                }       
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
        if(!mSimulateStatus){
                
                //This is a check to see if something is already being dragged.
            if(mDragged == true){
                return;
            }
                //Set drag to true.
            mDragged = true;             
            int gridX = (e.getX() / mGridSz);
            int gridY = ((e.getY() - mTopHeight) / mGridSz);
                //if the current space is occupied
            if(isOccupied(gridX, gridY)){
                    //we will create a temp
                Fish tempFish = new Fish(-1, gridX, gridY, Color.BLACK);
                    //and go through each fish on the list
                for(int i = 0; i < GlobalVariables.getInstance().mFish.size(); i++){            
                    Fish fish =  GlobalVariables.getInstance().mFish.get(i);                    
                        //once we find  our current fish store it to mSelectedIndex
                        //to be used in our mouse released function.                         
                    if( fish.compareTo(tempFish) == 0){                    
                        mSelectedFishIndex = i;                        
                        break;               
                    }              
                }
            }
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        
    }
}

class DrawPanel extends JPanel{

    int mRows;
    int mCols;
    int mGridSz;
    int maxGridSz;
    
    ArrayList<Fish> mFish;
    
    public DrawPanel(int numberOfRows, int numberOfCols, int gridSz){
        
        mGridSz = gridSz;
        mRows = numberOfRows;
        mCols = numberOfCols;
        maxGridSz = mGridSz * mRows;
    }
    
    private void paintBackground(Graphics g){
        
        for (int i = 1; i < mRows; i++) { 
            g.drawLine(i * mGridSz, 0, i * mGridSz, maxGridSz); 
        }
        
        for (int mAnimateStatus = 1; mAnimateStatus < mCols; mAnimateStatus++) { 
            g.drawLine(0, mAnimateStatus * mGridSz, maxGridSz, mAnimateStatus * mGridSz); 
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        
        super.paintComponent(g);
        
        paintBackground(g);       
        for(Fish f:GlobalVariables.getInstance().mFish){  
            f.paint(g);
        }
        
    }

    public void paint(){ 
        repaint();
    }
}
