package com.company.entities;

import com.company.*;
import com.company.missles.HeroOrb;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.company.Board.*;
import static com.company.GameController.*;

public class Hero extends Creature {

    //constructor

    public Hero(double xPos, double yPos){
        super(xPos,yPos);
        hero = this;
        ENTITY_ID = 1;

        hitboxHeight = 32;
        hitboxWidth = 32;
        BASE_HP = 2000;
        BASE_DEF = 100;
        BASE_ATK = 50;
        BASE_MS = 400;
        BASE_JUMP_HEIGHT = 100;


        MAX_HP = BASE_HP;
        CURRENT_HP = MAX_HP;
        DEF = BASE_DEF;
        ATK = BASE_ATK;
        MS = BASE_MS;
        JUMP_HEIGHT = BASE_JUMP_HEIGHT;

        setTexture();
    }

    double attackCD = 0.5;

    int abilityID_1;
    int abilityID_2;
    int abilityID_3;
    String folderPath = "src/textures/icons";
    ImageView i1 = new ImageView();
    ImageView i2 = new ImageView();
    ImageView i3 = new ImageView();
    public ImageView getAbility1Icon(){
        if(abilityID_1 == 0) return null;
        try {
            i1.setImage(new Image(Files.newInputStream(Paths.get(folderPath+"/"+abilityID_1+".png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return i1;
    }
    public ImageView getAbility2Icon(){
        if(abilityID_2 == 0) return null;
        try {
            i2.setImage(new Image(Files.newInputStream(Paths.get(folderPath+"/"+abilityID_2+".png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return i2;
    }
    public ImageView getAbility3Icon(){
        if(abilityID_3 == 0) return null;
        try {
            i3.setImage(new Image(Files.newInputStream(Paths.get(folderPath+"/"+abilityID_3+".png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return i3;
    }

    public void getAbility(int creatureID){
        if(abilityID_1 == 0){
            abilityID_1 = creatureID;
            try {
                Painter.paintHealthbar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if(abilityID_2 == 0){
            abilityID_2 = creatureID;
            try {
                Painter.paintHealthbar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if(abilityID_3 == 0){
            abilityID_3 = creatureID;
            try {
                Painter.paintHealthbar();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void useAbility1(double mouseX, double mouseY){
        if(abilityID_1 == 0) return;
        switch (abilityID_1){
            case 2: lepersTouch();
                break;
            case 3: spearThrust(mouseX);
                break;
            case 4: fireball(mouseX,mouseY);
                break;
            case 5: deathSentence(mouseX);
                break;
            case 6: cryForWar(this);
                break;
            case 7: if(!startMD(mouseX,mouseY))return;
                break;
            case 8: placeDeathTotem();
                break;
        }
        abilityID_1 = 0;
        try {
            Painter.paintHealthbar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void useAbility2(double mouseX, double mouseY){
        if(abilityID_2 == 0) return;
        switch (abilityID_2){
            case 2: lepersTouch();
                break;
            case 3: spearThrust(mouseX);
                break;
            case 4: fireball(mouseX,mouseY);
                break;
            case 5: deathSentence(mouseX);
                break;
            case 6: cryForWar(this);
                break;
            case 7: if(!startMD(mouseX,mouseY))return;
                break;
            case 8: placeDeathTotem();
                break;
        }
        abilityID_2 = 0;
        try {
            Painter.paintHealthbar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void useAbility3(double mouseX, double mouseY){
        if(abilityID_3 == 0) return;
        switch (abilityID_3){
            case 2: lepersTouch();
                break;
            case 3: spearThrust(mouseX);
                break;
            case 4: fireball(mouseX,mouseY);
                break;
            case 5: deathSentence(mouseX);
                break;
            case 6: cryForWar(this);
                break;
            case 7: if(!startMD(mouseX,mouseY))return;
                break;
            case 8: placeDeathTotem();
                break;
        }
        abilityID_3 = 0;
        try {
            Painter.paintHealthbar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //methods made to perform actions

    @Override
    public void moveLeft(){
        if(grappleState > 2)return;
        xVel = isOnGround() ? Math.max(-MS,xVel-MS/ ACCELERATION_TIME / GameController.FPS-MS/ DECELERATION_TIME /GameController.FPS):Math.max(-MS,xVel-MS/ ACCELERATION_TIME /GameController.FPS* AIR_MOVEMENT_MODIFIER);
    }

    @Override
    public void moveRight(){
        if(grappleState > 2)return;
        xVel = isOnGround() ? Math.min(MS,xVel+MS/ ACCELERATION_TIME /GameController.FPS+MS/ DECELERATION_TIME /GameController.FPS) : Math.min(MS,xVel+MS/ ACCELERATION_TIME /GameController.FPS* AIR_MOVEMENT_MODIFIER);
    }

    @Override
    public void jump(){
        if(isOnGround()){
            yVel = -Math.sqrt(2* FALL_ACCELERATION * JUMP_HEIGHT);
            justJumped = Math.sqrt(2* FALL_ACCELERATION * JUMP_HEIGHT)/ FALL_ACCELERATION *GameController.FPS;
        }
    }

    @Override
    public void doubleJump(){
        if(justJumped > 0){
            yVel -= Math.sqrt(FALL_ACCELERATION * JUMP_HEIGHT);
            justJumped = 0;
        }
    }

    @Override
    public void diveDown(){
        if (!didDive && !isOnGround()){
            yVel += DIVE_RANGE;
            didDive = true;
            diveTimer = Math.max(yVel- MAX_FALL_SPEED,0);
        }
    }

    @Override
    public void execute(){
        lowerCooldowns();
        if(isInMD){
            dashMD();
            return;
        }
        if(grappleState < 2){
            justJumped = Math.max(0,justJumped-1);
            diveTimer = Math.max(0, DIVE_RANGE * DIVE_DURATION /GameController.FPS);
            move(xVel/GameController.FPS,yVel/GameController.FPS);
            decelerate();
        }
        if(grappleState != 0){
            grapple();
        }
        moveCameraToMargin();
    }

    @Override
    public void move(double xMovement, double yMovement){//just movement and stop while touching wall.
        for (int h = 0; h < hitboxHeight; h++){
            if(isSolid((int)((xLoc +xPos+ hitboxWidth)/blockSize),(int)((yLoc +yPos+h)/blockSize))){
                xVel = Math.min(xVel,0);
                xMovement = Math.min(xMovement,0);
            }
            if(isSolid((int)((xLoc +xPos-1)/Board.blockSize),(int)((yLoc +yPos+h)/Board.blockSize))){
                xVel = Math.max(xVel,0);
                xMovement = Math.max(xMovement,0);
            }
        }
        for (int w = 0; w < hitboxHeight; w++){
            if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos+hitboxHeight) / Board.blockSize))){
                yVel = Math.min(yVel,0);
                yMovement = Math.min(yMovement,0);
                if(grappleState > 1)
                    grappleCancel();
            }
            if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos - 1) / Board.blockSize))){
                yVel = Math.max(yVel,0);
                yMovement = Math.max(yMovement,0);
            }
        }
        if(xVel != 0 && yVel != 0){
            int xModifier = xVel > 0 ? hitboxWidth : -1;
            int yModifier = yVel > 0 ? hitboxHeight : -1;
            if ( isSolid((int) ((xLoc + xPos + xModifier) / Board.blockSize),(int) ((yLoc + yPos + yModifier)/ blockSize))){
                if (Math.abs(yVel) > Math.abs(xVel)){
                    yVel = 0;
                    yMovement = 0;
                } else {
                    xVel = 0;
                    xMovement = 0;
                }
            }
        }

        double[] movement = getPath(xPos, yPos, xMovement, yMovement, hitboxWidth, hitboxHeight);
        xPos += movement[0];
        yPos += movement[1];
    }

    @Override
    public void decelerate(){
        if(!isOnGround()){
            yVel = Math.min(yVel+ FALL_ACCELERATION *1.0/GameController.FPS,2* FALL_ACCELERATION +diveTimer);
        }else{
            didDive = false;
            yVel = Math.max(yVel,0);
            if (xVel > 0) {
                xVel = Math.max(0, xVel - MS / DECELERATION_TIME / GameController.FPS);
            } else {
                xVel = Math.min(0, xVel + MS / DECELERATION_TIME / GameController.FPS);
            }
        }
    }
    public void moveCameraToMargin(){
        int rightMargin = MainMenu.WIDTH - XMARGIN;
        if(xPos < XMARGIN){
            if(xLoc - XMARGIN + xPos > 0){
                xLoc += xPos - XMARGIN;
                for(Entity m : missiles){
                    m.xPos += XMARGIN - xPos;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.xPos += XMARGIN - xPos;
                }
                xPos = XMARGIN;
            }else {
                xPos += xLoc;
                for(Entity m : missiles){
                    m.xPos += xLoc;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.xPos += xLoc;
                }
                xLoc = 0;
            }
        }else if(xPos > rightMargin){
            if(xLoc - rightMargin + xPos < pixelLength-MainMenu.WIDTH){
                xLoc += xPos - rightMargin;
                for(Entity m : missiles){
                    m.xPos += rightMargin - xPos;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.xPos += rightMargin - xPos;
                }
                xPos = rightMargin;
            }else{
                xPos += xLoc - Math.max(0, pixelLength - MainMenu.WIDTH);
                for(Entity m : missiles){
                    m.xPos += xLoc - pixelLength + MainMenu.WIDTH;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.xPos += xLoc - Math.max(0, pixelLength - MainMenu.WIDTH);
                }
                xLoc = Math.max(0, pixelLength - MainMenu.WIDTH);
            }
        }

        int topMargin = MainMenu.HEIGHT - YMARGIN;
        if(yPos < YMARGIN){
            if(yLoc - YMARGIN + yPos > 0){
                yLoc += yPos - YMARGIN;
                for(Entity m : missiles){
                    m.yPos += YMARGIN - yPos;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.yPos += YMARGIN - yPos;
                }
                yPos = YMARGIN;
            }else {
                yPos += yLoc;
                for(Entity m : missiles){
                    m.yPos += yLoc;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.yPos += yLoc;
                }
                yLoc = 0;
            }
        }else if(yPos > topMargin){
            if(yLoc - topMargin + yPos < pixelHeight-MainMenu.HEIGHT){
                yLoc += yPos - topMargin;
                for(Entity m : missiles){
                    m.yPos += topMargin - yPos;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.yPos += topMargin - yPos;
                }
                yPos = topMargin;
            }else{
                yPos += yLoc - Math.max(0, pixelHeight - MainMenu.HEIGHT);
                for(Entity m : missiles){
                    m.yPos += yLoc - pixelHeight + MainMenu.HEIGHT;
                }
                for(Entity e : creatures){
                    if(e != this)
                        e.yPos += yLoc - Math.max(0, pixelHeight - MainMenu.HEIGHT);
                }
                yLoc = Math.max(0,pixelHeight - MainMenu.HEIGHT);
            }
        }
    }


    @Override
    public void cast(){}


    @Override
    public void takeDamage(int amount, boolean trueDmg) {
        super.takeDamage(amount, trueDmg);
        try {
            Painter.paintHealthbar();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void die(){

        try {
            Painter.paintHealthbar();
            GameController.deathScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void attack(double mouseX, double mouseY){
        if(CD == 0){
            new HeroOrb(this,xPos,yPos,mouseX,mouseY,1);
            CD = attackCD;
        }
    }

}