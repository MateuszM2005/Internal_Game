package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.company.GameController.*;

public abstract class Missile extends Entity{

    public boolean TERRAIN_COLLIDING;
    public boolean ENTITY_COLLIDING;
    public int MISSILE_ID;

    public Creature root;
    public double duration;
    public int SPEED;
    public Missile(Creature root, double xPos, double yPos, double xTarget, double yTarget, double duration, int speed){
        super(xPos,yPos);
        SPEED = speed;
        this.root = root;
        double xDiff = xTarget - xPos;
        double yDiff = yTarget - yPos;
        double denominator = SPEED/Math.sqrt(xDiff*xDiff+yDiff*yDiff);
        xVel = xDiff * denominator;
        yVel = yDiff * denominator;
        this.duration = duration;
        Board.missiles.add(this);
    }

    public void execute(){
        move();
        lowerDur();
    }

    public void lowerDur(){
        duration -= 1.0/FPS;
        if(duration <= 0) terminate();
    }

    public void move(){
        double[] movement = getPath();
        xPos += movement[0];
        yPos += movement[1];
        if(checkCollision(movement[0],movement[1])){
            pop();
            terminate();
        }
    }

    public boolean checkCollision(double xMovement, double yMovement){
        if(TERRAIN_COLLIDING){
            if(Math.abs(xMovement) < Math.abs(xVel/ FPS) || Math.abs(yMovement) < Math.abs(yVel/FPS)){
                return true;
            }
        }
        if(ENTITY_COLLIDING){
            for( Creature c : Board.creatures){
                if(c != root && isColliding(c)){
                    return true;
                }
            }
        }
        return false;
    }

    public abstract void pop();

    public void terminate(){
        MainMenu.root.getChildren().remove(getTexture());
        duration = 0;
        REMOVE = true;
    }
    private final ImageView currentTexture = new ImageView();


    public void setTexture(){
        try {
            currentTexture.setImage(new Image(Files.newInputStream(Paths.get("src/textures/missiles/"+MISSILE_ID+".png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentTexture.setFitWidth(hitboxWidth);
        currentTexture.setFitHeight(hitboxHeight);
    }
    public ImageView getTexture(){
        currentTexture.setX(xPos);
        currentTexture.setY(yPos);
        return currentTexture;
    }

}
