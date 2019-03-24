/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplesnakeapplet;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;


/**
 *
 * @author Michał
 */
public class SimpleSnakeApplet extends Applet implements Runnable {
    
    private Image screenImage;
    private Graphics2D screenGraphics;
    private boolean stopFlag, turnFlag = false;
    private Dimension cellSize;
    private Thread gameThread;
    private int direction = 1, lastDirection = 1, score = 0;
    private int currentX = 11, currentY = 11, lastX, lastY;
    private final int maxX = 20, maxY = 20, minX = -1, minY = -1;
    private Point bonusPoint;
    private ArrayList<Point> snakeTail; 
    
    
    @Override
    public void init(){
        setSize(800,800);
                
        snakeTail = new ArrayList<>();
        
        cellSize = new Dimension(getWidth()/20 , getHeight()/20);
        
        screenImage = createImage(getWidth(), getHeight());
        screenGraphics = (Graphics2D) screenImage.getGraphics();
        screenGraphics.setColor(Color.BLACK);
        screenGraphics.fillRect(0, 0, getWidth(), getHeight());
        
        setBonusPointLocation();
        drawBonus();
        
        addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_D && lastDirection == 1){
                    stopFlag=true;
                    direction = 2;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_D && lastDirection == 2){
                    stopFlag=true;
                    direction = 3;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_D && lastDirection == 3){
                    stopFlag=true;
                    direction = 4;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_D && lastDirection == 4){
                    stopFlag=true;
                    direction = 1;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_A && lastDirection == 1){
                    stopFlag=true;
                    direction = 4;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_A && lastDirection == 4){
                    stopFlag=true;
                    direction = 3;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_A && lastDirection == 3){
                    stopFlag=true;
                    direction = 2;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_A && lastDirection == 2){
                    stopFlag=true;
                    direction = 1;
                    turnFlag=true;
                    stopFlag=false;
                }else if(e.getKeyCode() == KeyEvent.VK_SPACE && stopFlag){
                    
                    //tu chce jakos zrestartowac calą gierke :))
                }
                
                
            }
            
        });
        
    }
    
    @Override
    public void start(){
        gameStart();
        
    }
    
    public void gameStart(){
        stopFlag = false;
        
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void gameOver(){
        stopFlag = true;
        screenGraphics.setColor(Color.DARK_GRAY);
        screenGraphics.fillRect(getWidth()/4, getHeight()/4, getWidth()/2, getHeight()/2);
        screenGraphics.setColor(Color.WHITE);
        screenGraphics.setFont(new Font("Arial", Font.PLAIN, 50));
        screenGraphics.drawString("GAME OVER!", getWidth()/3  -20 ,  getHeight()/2);
        screenGraphics.setFont(new Font("Arial", Font.PLAIN, 30));
        screenGraphics.drawString("You scored: " + score +" points!", getWidth()/3,  getHeight()/2 + 50);
        repaint();
    }
    
    public void setBonusPointLocation(){
        int x = (int) (Math.random() * maxX);
        int y = (int) (Math.random() * maxY);
        
        bonusPoint = new Point(x, y);
        if(bonusPoint.equals(new Point(11,10)) || isCollision(x, y)) 
            setBonusPointLocation();
    }
    
    public void bonus(){
        score++;
        setBonusPointLocation();
        drawBonus();
    }
    
    public void moveHead(){
        if(direction == 1){
            currentY--;
        }else if(direction == 2){
            currentX++;
        }else if(direction == 3){
            currentY++;
        }else if(direction == 4){
            currentX--;
        }
        
        if(turnFlag)
            lastDirection = direction;
   }
    
    public void drawBonus(){
        screenGraphics.setColor(Color.MAGENTA);
        screenGraphics.fillOval(bonusPoint.x * cellSize.width + cellSize.width/3,
                bonusPoint.y * cellSize.height + cellSize.height/3, cellSize.width/3, cellSize.height/3 );
    } 
    
    public void drawCell(int x, int y, Color c){
        screenGraphics.setColor(c);
        screenGraphics.fillRect(cellSize.width *x , cellSize.height *y,
                cellSize.width -1, cellSize.height -1);
    }
    
    public void drawTail(){
        for(int i=0; i < score ; i++){
            drawCell(snakeTail.get(snakeTail.size() - 1 - i).x, 
                    snakeTail.get(snakeTail.size() - 1 - i).y, Color.GREEN);
        }
        
        drawCell(snakeTail.get(snakeTail.size() - 1 - score).x, 
                snakeTail.get(snakeTail.size() - 1 - score).y, Color.BLACK);
        
    }
    
    public boolean isCollision(int x, int y){
        for(int i=0; i < score ; i++){
            if(snakeTail.get(snakeTail.size() - 2 - i).x == x &&
            snakeTail.get(snakeTail.size() - 2 - i).y == y) 
                return true;
        }
        
        return false;
    }
       
    
    
    @Override
    public void run() {
        while(!stopFlag){
            //drawHead
            drawCell(currentX, currentY, Color.GREEN);
            
            snakeTail.add(new Point(currentX, currentY));
            
            lastX = currentX;
            lastY = currentY;
                
            moveHead();
            
            try {
                Thread.sleep(250);
            } catch (InterruptedException ie) { return;}  
            
            if(currentX >= maxX || currentX <= minX || currentY >= maxY || currentY <= minY){
                gameOver();
                break;
            }
            if(currentX == bonusPoint.x && currentY == bonusPoint.y){
                bonus();
            }
            if(isCollision(currentX, currentY)){
               gameOver();
               break;
            }
            
            
            drawTail();
            repaint();
        
        }
    }

    
    @Override
    public void paint(Graphics g){
        update(g);
    }
    
    @Override
    public void update(Graphics g){
        g.drawImage(screenImage, 0, 0, this);
    }
    
    
    
}
