package com.company.missles;

import com.company.Creature;
import com.company.GameController;
import com.company.MainMenu;
import com.company.Missile;

import static com.company.Board.missiles;

public class GrapplingHook extends Missile {
    public GrapplingHook(Creature root, double xPos, double yPos, double xTar, double yTar, double duration){
        super(root, xPos,yPos,xTar,yTar,duration, 800);
        MISSILE_ID = 1;
        hitboxHeight = 12;
        hitboxWidth = 12;
        setTexture();
    }

    @Override
    public void execute(){}

    @Override
    public void terminate(){
        MainMenu.root.getChildren().remove(getTexture());
        root.grapple = null;
        root.grappleState = 0;
        duration = 0;
        missiles.remove(this);
    }

    @Override
    public void move(){
        double[] movement = getPath();
        xPos += movement[0];
        yPos += movement[1];
        duration = Math.max(0,duration - 1.0/GameController.FPS);
        if (Math.abs(xVel / GameController.FPS) > Math.abs(movement[0]) && Math.abs(yVel / GameController.FPS) > Math.abs(movement[1])) {
            duration = 0;
            root.grappleState = 2;
            double xDiff = root.xPos - xPos;
            double yDiff = root.yPos - yPos;
            root.grappleLenght = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        }
        if(root.grappleState == 1 && duration == 0){
            root.grappleCancel();
        }
    }

    @Override
    public void pop(){
        terminate();
    }
}
