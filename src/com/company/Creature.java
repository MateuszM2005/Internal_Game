package com.company;

import com.company.effects.CryForWar;
import com.company.effects.DeathSentence;
import com.company.entities.DeathTotem;
import com.company.missles.Fireball;
import com.company.missles.GrapplingHook;
import com.company.effects.Spearstab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.company.Board.*;
import static com.company.GameController.*;

public abstract class Creature extends Entity{

    public int ENTITY_ID;

    public int BASE_HP;
    public int BASE_DEF;
    public int BASE_ATK;
    public int BASE_MS;
    public int BASE_JUMP_HEIGHT;

    public int BASE_FALL_ACCELERATION = 400;
    public int BASE_MAX_FALL_SPEED = 2*BASE_FALL_ACCELERATION;
    public int BASE_FALL_RANGE = BASE_FALL_ACCELERATION;
    public double BASE_DIVE_DURATION = 1;
    public double BASE_ACCELERATION_TIME = 0.5;
    public double BASE_DECELERATION_TIME = 0.2;
    public double BASE_AIR_MOVEMENT_MODIFIER = 0.5;

    //stats that are taken in calulation, they can be changed by items and levels

    public int MAX_HP = BASE_HP;
    public double CURRENT_HP = MAX_HP;
    public int DEF = BASE_DEF;
    public int ATK = BASE_ATK;
    public int MS = BASE_MS;
    public int JUMP_HEIGHT = BASE_JUMP_HEIGHT;
    public int FALL_ACCELERATION = BASE_FALL_ACCELERATION;
    public int MAX_FALL_SPEED = BASE_MAX_FALL_SPEED;
    public int DIVE_RANGE = BASE_FALL_RANGE;
    public double DIVE_DURATION = BASE_DIVE_DURATION;
    public double ACCELERATION_TIME = BASE_ACCELERATION_TIME;
    public double DECELERATION_TIME = BASE_DECELERATION_TIME;
    public double AIR_MOVEMENT_MODIFIER = BASE_AIR_MOVEMENT_MODIFIER;

    public Creature(double xPos, double yPos){
        super(xPos,yPos);
        creatures.add(this);
    }

    //Event Methods
    public int PRIOTITY;
    public int OFFENSE;
    public int PROTECTION;
    public boolean BACKLINE;
    public int TETHER_RANGE;
    public int TETHER_ALLY_RANGE;
    public int ATTACK_RANGE;
    public double MAX_CD;
    public double CD;
    public double SECONDARY_CD;
    public double ATTACK_CD;

    public int detectionRange = 500*blockSize;
    public int platformVision = detectionRange;
    public int maxFreeMovementDepth = 300;
    public int searchDepth = 3;

    public Instruction instructions;
    public Platform targetLastPlatform;
    public Creature target;
    public boolean isTargetEnemy;
    public double destX;
    public boolean hasDest;
    public boolean isWandering;
    public boolean aggro;

    public void execute(){
        if(!isActive()){
            stop();
            target = null;
            aggro = false;
            hasDest = false;
            isWandering = false;
            return;
        }
        if(isInMD){
            dashMD();
            lowerCooldowns();
            return;
        }
        Platform platform = getPlatform();
        if(platform == null) return;

        if(!aggro || switchTarget()){
            findTarget();
            if(target != null){//if it has found target
                targetLastPlatform = target.getPlatform();
                if(targetLastPlatform != null && platform.comparePlatforms(targetLastPlatform)){//if the target is on the same platform
                    aggro = true;
                    hasDest = false;
                    findBattlePosition();
                }else { //if not
                    instructions = findPath();
                    if(instructions == null || instructions.empty){ //if target is unnacessible
                        aggro = false;
                        wander();
                    }else { //if it is accessible
                        aggro = true;
                        hasDest = false;
                        followInstructions();
                    }
                }
            } else { //if no target then wander
                aggro = false;
                wander();
            }
        } //it works

        if(aggro){
            if(getDistance(target) > TETHER_RANGE) {
                Platform temp = target.getPlatform();
                if (temp != null) {
                    if (!targetLastPlatform.comparePlatforms(temp)) { //target platform changed
                        targetLastPlatform = temp;
                        if (platform.comparePlatforms(targetLastPlatform)) {//if the target is on the same platform
                            hasDest = false;
                            instructions = null;
                            findBattlePosition();
                        } else { //if not
                            instructions = findPath();
                            if (instructions == null || instructions.empty) { //if target is unnacessible
                                aggro = false;
                                wander();
                            } else { //if it is accessible
                                hasDest = false;
                                followInstructions();
                            }
                        }
                    } else {
                        targetLastPlatform = temp;
                        if (platform.comparePlatforms(targetLastPlatform)) {
                            findBattlePosition();
                        } else if (instructions == null || instructions.empty){
                            aggro = false;
                            wander();
                        }else {
                            followInstructions();
                        }
                    }
                } else {
                    aggro = false;
                    wander();
                }
            }else {
                findBattlePosition();
            }
        }
        move();
        lowerCooldowns();
        if(getDistance(target) < ATTACK_RANGE){
            if(isTargetEnemy) cast();
        }
    } //finally done

    public void findTarget(){
        if(getDistance(hero) > detectionRange) return;
        target = hero;
        isTargetEnemy = true;
        int tarVal = OFFENSE * 10;
        if(findPath() != null){
            if(getDistance(target) < 300) return;
            for(Creature c : creatures){
                if(c != hero && c != this && getDistance(c) < detectionRange){
                    if(c.PRIOTITY *PROTECTION > tarVal){
                        if(findPath(c)!= null){
                            target = c;
                            isTargetEnemy = false;
                            tarVal = c.PRIOTITY*PROTECTION;
                        }
                    }
                }
            }
        }

    } //done

    public boolean switchTarget(){
        if(target == hero){
            return getDistance(hero) > 800 ;
        }else{
            return  target.REMOVE||(getDistance(hero) < 300 && findPath(hero) != null);
        }
    }



    public class Platform{
        int y;
        int xLeft;
        int xRight;
        boolean isLeftEnded;
        boolean isRightEnded;
        public Platform(int y, int xLeft, boolean left, int xRight, boolean right){
            this.y = y;
            this.xLeft = xLeft;
            isLeftEnded = left;
            this.xRight = xRight;
            isRightEnded = right;
        }
        public boolean comparePlatforms(Platform p1){
            if(p1 == null) return false;
            if(this.y != p1.y) return false;
            if(this.xLeft <= p1.xRight && p1.xRight <= this.xRight){
                return true;
            }
            if(p1.xLeft <= this.xRight && this.xRight <= p1.xRight){
                return true;
            }
            return false;
        }
    }
    public Platform getPlatform(){  //returns null if no platform fix a bit
        int yLow = getYofBlockUnder(xPos,yPos,hitboxWidth);
        if(yLow == 0) return null;
        int yHigh = ((yLow*blockSize - hitboxHeight)/blockSize);
        int x = (int)((xLoc + xPos)/blockSize);
        for(;x < (xLoc + xPos+hitboxWidth-1)/blockSize;x++){
            if(Board.isSolid(x,yLow))break;
        }
        boolean left = true;
        boolean right = true;

        int maxLeft = x;
        boolean breakLft = false;
        int maxRight = x;
        boolean breakRight = false;
        //checkleft
        while(isSolid(maxLeft,yLow)){
            maxLeft--;
            if(x - maxLeft > platformVision /blockSize){
                left = false;
                break;
            }
            if(maxLeft < 0){
                break;
            }
            for(int y = yHigh;y < yLow;y++){
                if(isSolid(maxLeft,y)){
                    breakLft = true;
                    break;
                }
            }
            if(breakLft){
                break;
            }


        }
        //checkright
        while(isSolid(maxRight,yLow) && maxRight - x < detectionRange){
            maxRight++;
            if(maxRight - x > platformVision /blockSize){
                right = false;
                break;
            }
            if(maxRight > pixelLength/blockSize-1){
                break;
            }
            for(int y = yHigh;y < yLow;y++){
                if(isSolid(maxRight,y)){
                    breakRight = true;
                    break;
                }
            }
            if(breakRight){
                break;
            }
        }
        return new Platform(yLow,maxLeft+1,left,maxRight-1,right);
    }
    public Platform getPlatform(double xPos, double yPos){  //returns null if no platform
        int yLow = getYofBlockUnder(xPos,yPos,hitboxWidth);
        if(yLow == 0) return null;
        int yHigh = ((yLow*blockSize - hitboxHeight)/blockSize);
        int x = (int)((xLoc + xPos)/blockSize);
        for(;x < (xLoc + xPos+hitboxWidth-1)/blockSize;x++){
            if(Board.isSolid(x,yLow))break;
        }
        boolean left = true;
        boolean right = true;

        int maxLeft = x;
        boolean breakLft = false;
        int maxRight = x;
        boolean breakRight = false;
        //checkleft
        while(isSolid(maxLeft,yLow)){
            maxLeft--;
            if(x - maxLeft > platformVision /blockSize){
                left = false;
                break;
            }
            if(maxLeft < 0){
                break;
            }
            for(int y = yHigh;y < yLow;y++){
                if(isSolid(maxLeft,y)){
                    breakLft = true;
                    break;
                }
            }
            if(breakLft){
                break;
            }


        }
        //checkright
        while(isSolid(maxRight,yLow) && maxRight - x < detectionRange){
            maxRight++;
            if(maxRight - x > platformVision /blockSize){
                right = false;
                break;
            }
            if(maxRight > pixelLength/blockSize-1){
                break;
            }
            for(int y = yHigh;y < yLow;y++){
                if(isSolid(maxRight,y)){
                    breakRight = true;
                    break;
                }
            }
            if(breakRight){
                break;
            }
        }
        return new Platform(yLow,maxLeft+1,left,maxRight-1,right);
    }
    public Platform getFreeMovement(double xPos, double yPos, int xVelSign, double yVel){
        //check for cliffs - works
        int mapX = (int)((xPos + xLoc)/blockSize);
        int mapY = (int)((yPos + yLoc)/blockSize);
        if(xVelSign == 1){
            for(int x = mapX; x < pixelLength/blockSize && x <= (mapX*blockSize+hitboxHeight+3)/blockSize; x++){
                if(isSolid(x,mapY-1)) {
                    if(yVel == 0) return null;
                    int streak = 0;
                    for(int y = mapY -1; y > mapY - JUMP_HEIGHT/blockSize && y >= 0; y--){
                        if(isSolid(x-1,y))return null;
                        if(!isSolid(x,y)) streak++;
                        else streak = 0;
                        if(streak >= hitboxHeight/blockSize){
                            return getPlatform(x*blockSize-xLoc,y*blockSize-yLoc);
                        }
                    }
                    return null;
                }
            }
        } else {
            for(int x = mapX;x >= 0 && x < pixelLength/blockSize && x >= mapX-1; x--){
                if(isSolid(x,mapY-1)) {
                    if(yVel == 0) return null;
                    int streak = 0;
                    for(int y = mapY -1; y > mapY - JUMP_HEIGHT/blockSize && y >= 0; y--){
                        if(isSolid(x+1,y))return null;
                        if(!isSolid(x,y)) streak++;
                        else streak = 0;
                        if(streak >= hitboxHeight/blockSize){
                            return getPlatform(x*blockSize-xLoc,y*blockSize-yLoc-5);
                        }
                    }
                    return null;
                }
            }
        }

        //check
        int start = 0;
        int[] xData = new int[maxFreeMovementDepth -start];
        int[] yData = new int[maxFreeMovementDepth -start];
        for(int count = start; count < maxFreeMovementDepth; count++){
            double time = count*1.0*blockSize/(MS*AIR_MOVEMENT_MODIFIER);
            xData[count-start] = (int)((xPos+blockSize*xVelSign*count+xLoc+xVelSign*hitboxWidth)/blockSize);
            yData[count-start] = (int)((yPos + yVel * time + Math.min(FALL_ACCELERATION*time, MAX_FALL_SPEED-yVel)*Math.min(time,MAX_FALL_SPEED*1.0/FALL_ACCELERATION)/2.0 + Math.max(0.0,time-MAX_FALL_SPEED*1.0/FALL_ACCELERATION)*MAX_FALL_SPEED+ yLoc)/blockSize); //also improve this
        }


        if(xVelSign == 1){
            for(int count = start; count < maxFreeMovementDepth -1; count++){
                boolean stop = false;
                if(yData[count] < yData[count+1]){
                    for(int y = yData[count];y<=yData[count+1];y++){
                        if(isSolid(xData[count],y)) stop = true;
                        if(isSolid(xData[count],y+hitboxHeight/blockSize-1)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y+hitboxHeight/blockSize-1)) stop = true;
                    }
                }else {
                    for(int y = yData[count+1];y<=yData[count];y++){
                        if(isSolid(xData[count],y)) stop = true;
                        if(isSolid(xData[count],y+hitboxHeight/blockSize-1)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y+hitboxHeight/blockSize-1)) stop = true;
                    }
                }
                if (stop) return getPlatform(xData[count]*blockSize-xLoc,yData[count]*blockSize-yLoc);
            }
            return null;
        } else {
            for(int count = start+1; count < maxFreeMovementDepth; count++){
                boolean stop = false;
                if(yData[count] < yData[count-1]){
                    for(int y = yData[count];y<=yData[count-1];y++){
                        if(isSolid(xData[count],y)) stop = true;
                        if(isSolid(xData[count],y+hitboxHeight/blockSize-1)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y+hitboxHeight/blockSize-1)) stop = true;
                    }
                }else {
                    for(int y = yData[count-1];y<=yData[count];y++){
                        if(isSolid(xData[count],y)) stop = true;
                        if(isSolid(xData[count],y+hitboxHeight/blockSize-1)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y)) stop = true;
                        if(isSolid(xData[count]+hitboxWidth/blockSize - 1,y+hitboxHeight/blockSize-1)) stop = true;
                    }
                }
                if (stop){
                    return getPlatform(xData[count-1]*blockSize-xLoc,yData[count-1]*blockSize-yLoc);
                }
            }
            return null;
        }
    }
    public Instruction findPath(){
        return findPlatforms(getPlatform(),searchDepth,this.target.getPlatform());
    }
    public Instruction findPath(Creature c){
        return findPlatforms(getPlatform(),searchDepth,c.getPlatform());
    }
    public Instruction findPlatforms(Platform platform, int repetitions, Platform target){
        if(repetitions == 1){
            double y = platform.y*blockSize-yLoc-hitboxHeight;
            double leftX = platform.xLeft*blockSize-xLoc;
            double rightX = platform.xRight*blockSize-xLoc+blockSize-hitboxWidth;
            if(platform.isLeftEnded){
                Platform p1 = getFreeMovement(leftX,y,-1,0);
                if(p1!= null && p1.comparePlatforms(target)){
                    return new Instruction((leftX+xLoc)/blockSize,false,p1,false);
                }
                Platform p2 = getFreeMovement(leftX,y,-1,-Math.sqrt(2* FALL_ACCELERATION * JUMP_HEIGHT));
                if(p2!= null && p2.comparePlatforms(target)){
                    return new Instruction((leftX+xLoc)/blockSize,true,p2,false);
                }
            }
            if(platform.isRightEnded){
                Platform p3 = getFreeMovement(rightX,y,1,0);
                if(p3!= null && p3.comparePlatforms(target)){
                    return new Instruction((rightX+xLoc)/blockSize,false,p3,true);
                }
                Platform p4 = getFreeMovement(rightX,y,1,-Math.sqrt(2* FALL_ACCELERATION * JUMP_HEIGHT));
                if(p4!= null && p4.comparePlatforms(target)){
                    return new Instruction((rightX+xLoc)/blockSize,true,p4,true);
                }
            }
            return null;
        }else {
            double y = platform.y*blockSize-yLoc-hitboxHeight;
            double leftX = platform.xLeft*blockSize-xLoc;
            double rightX = platform.xRight*blockSize-xLoc+blockSize-hitboxWidth;
            Platform p1;
            Platform p2;
            Platform p3;
            Platform p4;
            Instruction i1 = null;
            Instruction i2 = null;
            Instruction i3 = null;
            Instruction i4 = null;
            if(platform.isLeftEnded){
                p1 = getFreeMovement(leftX,y,-1,0);
                if(p1 != null){
                    if(p1.comparePlatforms(target)) return new Instruction((leftX+xLoc)/blockSize,false,p1,false);
                    i1 = findPlatforms(p1,repetitions-1,target);
                    if(i1 != null){
                        Instruction temp = i1;
                        i1 = new Instruction((leftX+xLoc)/blockSize,false,p1,false);
                        i1.add(temp);
                    }
                }
                p2 = getFreeMovement(leftX,y,-1,-Math.sqrt(2* FALL_ACCELERATION * JUMP_HEIGHT));
                if(p2 != null){
                    if(p2.comparePlatforms(target)) return new Instruction((leftX+xLoc)/blockSize,true,p2,false);
                    i2 = findPlatforms(p2,repetitions-1,target);
                    if(i2 != null){
                        Instruction temp = i2;
                        i2 = new Instruction((leftX+xLoc)/blockSize,true,p2,false);
                        i2.add(temp);
                    }
                }
            }
            if(platform.isRightEnded){
                p3 = getFreeMovement(rightX,y,1,0);
                if(p3 != null){
                    if(p3.comparePlatforms(target)) return new Instruction((rightX+xLoc)/blockSize,false,p3,true);
                    i3 = findPlatforms(p3,repetitions-1,target);
                    if(i3 != null){
                        Instruction temp = i3;
                        i3 = new Instruction((rightX+xLoc)/blockSize,false,p3,true);
                        i3.add(temp);
                    }
                }

                p4 = getFreeMovement(rightX,y,1,-Math.sqrt(2* FALL_ACCELERATION * JUMP_HEIGHT));
                if(p4 != null){
                    if(p4.comparePlatforms(target)) return new Instruction((rightX+xLoc)/blockSize,true,p4,true);
                    i4 = findPlatforms(p4,repetitions-1,target);
                    if(i4 != null){
                        Instruction temp = i4;
                        i4 = new Instruction((rightX+xLoc)/blockSize,true,p4,true);
                        i4.add(temp);
                    }
                }
            }
            Instruction output = null;
            if(i1 != null) output = i1;
            if(i2 != null && (output == null || output.steps > i2.steps)) output = i2;
            if(i3 != null && (output == null || output.steps > i3.steps)) output = i3;
            if(i4 != null && (output == null || output.steps > i4.steps)) output = i4;
            return output;
        }
    }
    public boolean isShotClear(int checkRange, double targetX, double targetY, int width, int height){
        checkRange = Math.min(checkRange,100);
        double xDiff = targetX - xPos;
        double yDiff = targetY - yPos;
        int start = 0;
        int[] xData = new int[checkRange];
        int[] yData = new int[checkRange];
        for(int count = start; count < checkRange; count++){
            xData[count-start] = (int)((xPos+xLoc+count*blockSize*getSign(xDiff))/blockSize);
            yData[count-start] = (int)((yPos+yLoc + yDiff/Math.abs(xDiff)*count*blockSize)/blockSize); //also improve this
        }


        if(xDiff > 0){
            for(int count = start; count < checkRange -1; count++){
                if(yData[count] < yData[count+1]){
                    for(int y = yData[count];y<=yData[count+1];y++){
                        if(isSolid(xData[count],y)) return false;
                        if(isSolid(xData[count],y+height/blockSize-1)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y+height/blockSize-1)) return false;
                    }
                }else {
                    for(int y = yData[count+1];y<=yData[count];y++){
                        if(isSolid(xData[count],y)) return false;
                        if(isSolid(xData[count],y+height/blockSize-1)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y+height/blockSize-1)) return false;
                    }
                }
            }
        } else {
            for(int count = start+1; count < checkRange; count++){
                if(yData[count] < yData[count-1]){
                    for(int y = yData[count];y<=yData[count-1];y++){
                        if(isSolid(xData[count],y)) return false;
                        if(isSolid(xData[count],y+height/blockSize-1)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y+height/blockSize-1)) return false;
                    }
                }else {
                    for(int y = yData[count-1];y<=yData[count];y++){
                        if(isSolid(xData[count],y)) return false;
                        if(isSolid(xData[count],y+height/blockSize-1)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y)) return false;
                        if(isSolid(xData[count]+width/blockSize - 1,y+height/blockSize-1)) return false;
                    }
                }
            }
        }
        return true;
    }
    public class Instruction{
        int steps;
        Instruction next;
        double targetX;
        boolean jumpAtTarget;
        public boolean empty = true;
        boolean dir;
        Platform destination;
        Platform startingPlatform;
        public void print(){
            System.out.println("Target: " + targetX);
            System.out.println("Steps: " + steps);
            System.out.println("jump: " + jumpAtTarget);
            System.out.println("empty: " + empty);
        }
        public Instruction(double targetX, boolean jumpAtTarget, Platform destination, boolean dir){
            steps = 1;
            this.targetX = targetX;
            this.jumpAtTarget = jumpAtTarget;
            empty = false;
            this.destination = destination;
            this.dir = dir;
        }
        public void add(double targetX, boolean jumpAtTarget,Platform destination, boolean dir){
            steps+=1;
            if(this.empty){
                this.targetX = targetX;
                this.jumpAtTarget = jumpAtTarget;
                empty = false;
                return;
            }
            Instruction current = this;
            while(current.next != null){
                current = current.next;
            }
            current.next = new Instruction(targetX, jumpAtTarget,destination,dir);
        }
        public void add(Instruction instruction){
            if(this.empty){
                this.steps = instruction.steps;
                this.next = instruction.next;
                this.empty = instruction.empty;
                this.targetX = instruction.targetX;
                this.jumpAtTarget = instruction.jumpAtTarget;
                return;
            }
            steps += instruction.steps;
            Instruction current = this;
            while(current.next != null){
                current = current.next;
            }
            current.next = instruction;
        }
    }
    public void popInstruction(){
        if(!getPlatform().comparePlatforms(instructions.destination) || !isOnGround()) return;
        hasDest = false;
        if(instructions.next != null){
            instructions = instructions.next;
            readInstruction();
        } else{
            instructions.empty = true;
        }
    }

    public boolean isActive(){
        if(getDistance(hero) < detectionRange) return true;
        return false;
    }
    public double getDistance(Entity e){
        double xDist = xPos - e.xPos;
        double yDist = yPos - e.yPos;
        return Math.sqrt(xDist*xDist+yDist*yDist);
    }

    public void wander(){
        if(hasDest) {
            if (Math.abs(xPos-(destX*blockSize-xLoc))<1){
                stop();
                hasDest = false;
            }else if (destX*blockSize-xLoc < xPos) {
                xVel = -MS/2.0;
            } if (destX*blockSize-xLoc > xPos) {
                xVel = MS/2.0;
            }
        }else {
            Platform temp = getPlatform();
            destX = temp.xLeft + Math.random()* (temp.xRight+(blockSize-hitboxWidth)*1.0/blockSize-temp.xLeft);
            hasDest = true;
        }
    }
    public void readInstruction(){
        if(hasDest) return;
        destX = instructions.targetX;
        instructions.startingPlatform = getPlatform();
        if(instructions.jumpAtTarget){
            if(instructions.dir){
                if(destX*blockSize-xLoc <= xPos){
                    jump();
                }
            }else {
                if(destX*blockSize-xLoc >= xPos){
                    jump();
                }
            }
        }
        hasDest = true;
    }
    public void followInstructions(){ //following instructions while on different platform
        if(!hasDest){
            readInstruction();
        }
        if(getPlatform().comparePlatforms(instructions.destination)&&isOnGround()){
            popInstruction();
        }else if(isOnGround()&&!getPlatform().comparePlatforms(instructions.startingPlatform)){
            aggro = false;
            return;
        }
        if(Math.abs(xPos-(destX*blockSize-xLoc))<1){
            if(isOnGround()){
                if(instructions.empty){
                    wander();
                }else{
                    if(instructions.jumpAtTarget){
                        jump();
                    }
                }
            }
        }
        if(instructions.dir){
            if(xPos < instructions.destination.xRight*blockSize-xLoc - MS*1.0/FPS - hitboxWidth|| isOnGround()) moveRight();
            else if(xPos < instructions.destination.xRight*blockSize-xLoc - hitboxWidth) moveLeft(); else stop();
        }else {
            if(xPos > instructions.destination.xLeft*blockSize-xLoc + MS*1.0/FPS || isOnGround()) moveLeft();
            else if(xPos > instructions.destination.xLeft*blockSize-xLoc) moveRight(); else stop();
        }
    }
    public void findBattlePosition(){
        if(isTargetEnemy){
            destX = (target.xPos+xLoc)/blockSize;
            if(destX*blockSize-xLoc < xPos){
                if(getDistance(target) > TETHER_RANGE){
                    moveLeft();
                    if(!getPlatform().comparePlatforms(getPlatform(xPos+xVel/FPS,yPos+yVel/FPS))) stop();
                }else {
                    moveRight();
                    if(!getPlatform().comparePlatforms(getPlatform(xPos+xVel/FPS,yPos+yVel/FPS))) stop();
                }
            }else
            if(destX*blockSize-xLoc > xPos){
                if(getDistance(target) > TETHER_RANGE){
                    moveRight();
                    if(!getPlatform().comparePlatforms(getPlatform(xPos+xVel/FPS,yPos+yVel/FPS))) stop();
                }else {
                    moveLeft();
                    if(!getPlatform().comparePlatforms(getPlatform(xPos+xVel/FPS,yPos+yVel/FPS))) stop();
                }
            }else{
                stop();
            }
        } else {
            if(BACKLINE){
                wander();
            }else {
                if(target.BACKLINE){
                    int dir = target.xPos < hero.xPos ? 1 : -1; //from target to hero
                    destX = (target.xPos+xLoc + dir*TETHER_ALLY_RANGE)/blockSize;
                    if(destX*blockSize-xLoc < xPos){
                        moveLeft();
                    }else
                    if(destX*blockSize-xLoc > xPos){
                        moveRight();
                    }else{
                        stop();
                    }
                    if(!getPlatform().comparePlatforms(getPlatform(xPos+xVel/FPS,yPos+yVel/FPS))) stop();
                } else {
                    wander();
                }
            }
        }
    }

    public void lowerCooldowns()  {
        CD = Math.max(0,CD - 1.0/FPS);
        SECONDARY_CD = Math.max(0,SECONDARY_CD - 1.0/FPS);
    }

    public abstract void cast();


    public void takeDamage(int ammount, boolean trueDmg){
        if(INVURNELABLE) return;
        if(trueDmg){
            CURRENT_HP -= ammount;
        }else {
            CURRENT_HP -= ammount*100.0/(100+DEF);
        }
        if(CURRENT_HP <= 0) die();
    }
    public void die(){
        REMOVE = true;
    }


    //Check State Methods
    public boolean INVURNELABLE;

    public boolean isOnGround(){
        for (int w = 0; w < hitboxHeight; w++){
            if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos+hitboxHeight) / Board.blockSize))) return true;
        }
        return false;
    }

    private final ImageView currentTexture = new ImageView();
    public String texturePath;
    public String textureName = "default.png";

    public void getTextureName(){} //add later after client makes textures

    public void setTexture(){
        texturePath = "src/textures/creatures/"+ENTITY_ID+"/";
        try {
            currentTexture.setImage(new Image(Files.newInputStream(Paths.get(texturePath+textureName))));
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




    //Movement

    public double justJumped = 0;
    public boolean didDive = false;
    public double diveTimer = 0;
    public void moveLeft(){
        xVel = -MS;
    }
    public void moveRight(){
        xVel = MS;
    }
    public void jump(){
        if(isOnGround()) yVel  = -Math.sqrt(2* FALL_ACCELERATION * JUMP_HEIGHT);
    }
    public void stop(){
        xVel = 0;
    }
    public void doubleJump(){

    }
    public void diveDown(){

    }

    public void move(){
        for (int h = 0; h < hitboxHeight; h++){
            if(isSolid((int)((xLoc +xPos+ hitboxWidth)/blockSize),(int)((yLoc +yPos+h)/blockSize))){
                xVel = Math.min(xVel,0);
            }
            if(isSolid((int)((xLoc +xPos-1)/Board.blockSize),(int)((yLoc +yPos+h)/Board.blockSize))){
                xVel = Math.max(xVel,0);
            }
        }
        for (int w = 0; w < hitboxHeight; w++){
            if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos+hitboxHeight) / Board.blockSize))){
                yVel = Math.min(yVel,0);
            }
            if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos - 1) / Board.blockSize))){
                yVel = Math.max(yVel,0);
            }
        }
        if(xVel != 0 && yVel != 0){
            int xModifier = xVel > 0 ? hitboxWidth : -1;
            int yModifier = yVel > 0 ? hitboxHeight : -1;
            if (isSolid((int) ((xLoc + xPos + xModifier) / Board.blockSize),(int) ((yLoc + yPos + yModifier)/ blockSize))){
                if (Math.abs(yVel) > Math.abs(xVel)){
                    yVel = 0;
                } else {
                    xVel = 0;
                }
            }
        }
        double[] movement;
        if(Math.abs(xPos-destX*blockSize+xLoc) < MS*1.0/FPS && (instructions==null||instructions.empty)) {
            movement = getPath(xPos, yPos, -xPos + destX * blockSize - xLoc, yVel / FPS, hitboxWidth, hitboxHeight);
        } else {
            movement = getPath(xPos, yPos, xVel / FPS, yVel / FPS, hitboxWidth, hitboxHeight);
        }
        if(!isOnGround()){
            movement = getPath(xPos, yPos, xVel / FPS*AIR_MOVEMENT_MODIFIER, yVel / FPS, hitboxWidth, hitboxHeight);
        }
        xPos += movement[0];
        yPos += movement[1];
        decelerate();
    }
    public void move(double xMovement, double yMovement){
        for (int h = 0; h < hitboxHeight; h++){
            if(isSolid((int)((xLoc +xPos+ hitboxWidth)/blockSize),(int)((yLoc +yPos+h)/blockSize))){
                xMovement = Math.min(xMovement,0);
            }
            if(isSolid((int)((xLoc +xPos-1)/Board.blockSize),(int)((yLoc +yPos+h)/Board.blockSize))){
                xMovement = Math.max(xMovement,0);
            }
        }
        for (int w = 0; w < hitboxHeight; w++){
            if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos+hitboxHeight) / Board.blockSize))){
                yMovement = Math.min(yMovement,0);
            }
            if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos - 1) / Board.blockSize))){
                yMovement = Math.max(yMovement,0);
            }
        }
        if(xMovement != 0 && yMovement != 0){
            int xModifier = xMovement > 0 ? hitboxWidth : -1;
            int yModifier = yMovement > 0 ? hitboxHeight : -1;
            if ( isSolid((int) ((xLoc + xPos + xModifier) / Board.blockSize),(int) ((yLoc + yPos + yModifier)/ blockSize))){
                if (Math.abs(yMovement) > Math.abs(xMovement)){
                    yMovement = 0;
                } else {
                    xMovement = 0;
                }
            }
        }
        double[] movement;
        movement = getPath(xPos, yPos, xMovement, yMovement, hitboxWidth, hitboxHeight);
        xPos += movement[0];
        yPos += movement[1];
        for(Creature c : creatures){
            if(isColliding(c)){
                c.move(movement[0]/10,movement[1]/10);
                xPos -= movement[0]/10;
                yPos -= movement[1]/10;
            }
        }

        decelerate();
    }

    public void decelerate(){
        if(!isOnGround()) {
            yVel = Math.min(yVel + FALL_ACCELERATION * 1.0 / GameController.FPS, 2 * FALL_ACCELERATION);
        }else yVel = Math.min(0,yVel);
    }
    //Grappling Ability

    public double swingAccelerationFactor = 0.13;
    public double swingDeaccelerationFactor = 0;
    public int hookRange = 800;


    public int grappleState;  //0-none  1-shoot   2-set on terrain    3-in swing  4-set on enemy
    public GrapplingHook grapple;
    public double grappleLenght;
    public void grapple(){
        if(grappleState == 1){
            grapple.move();
        }
        if(grappleState == 2 || grappleState == 3){
            swing();
        }
    }
    public void grapple(double tarX, double tarY){
        if(grappleState == 0){
            shootGrapple(tarX,tarY);
        }
    }
    public void shootGrapple (double tarX, double tarY){
        if(isOnGround())return;
        grappleState = 1;
        double xDif = tarX - xPos;
        double yDif = tarY - yPos;
  //      double denominator = Math.abs(xDif) + Math.abs(yDif);
        double denominator = Math.sqrt(xDif*xDif + yDif*yDif);
        grapple = new GrapplingHook(this,xPos,yPos,tarX,tarY,1.0);
    }
    public void grappleCancel () {
        if(grapple == null){
            grappleState = 0;
            return;
        }
        grapple.terminate();
    }
    public void swing () {
        justJumped = 0;
        didDive = true;
        move(xVel / GameController.FPS, yVel / GameController.FPS);
        if(grapple == null) grappleState = 0;
        if(grappleState == 0) return;
        double xDiff = xPos - grapple.xPos;
        double yDiff = yPos - grapple.yPos;
        double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        if (distance > grappleLenght) {
            double ratio = grappleLenght / distance;
            move((xDiff * ratio - xDiff), (yDiff * ratio - yDiff));
            if(grappleState == 0) return;
            xDiff = xPos - grapple.xPos;
            yDiff = yPos - grapple.yPos;
            distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
            if ((int) distance > grappleLenght) {
                grappleCancel();
            }
        }
        if (((int) distance >= (int) grappleLenght || grappleState == 3) && yDiff >= 0) {
            if (!(grappleState == 3)) {
                grappleState = 3;
                xVel = xVel * Math.abs(yDiff) / distance;
                yVel = yVel * Math.abs(xDiff) / distance;
                int dirX;    // -1 - up ; 1 - down
                int dirY;    // -1 - left ; 1 - right
                if (Math.abs(xVel) < Math.abs(yVel)) {
                    dirY = getSign(yVel);
                    dirX = dirY * -getSign(xDiff);
                } else {
                    dirX = getSign(xVel);
                    dirY = dirX * getSign(xDiff);
                }
                xVel = Math.abs(xVel) * dirX;
                yVel = Math.abs(yVel) * dirY;
            }
            double acceleration = Math.abs(FALL_ACCELERATION * xDiff / grappleLenght / GameController.FPS);
            double ratio = acceleration / distance;
            xVel -= Math.abs(yDiff) * ratio * getSign(xDiff);
            yVel += Math.abs(xDiff) * ratio;
            if (GameController.isApressed) {
                xVel -= Math.abs(yDiff) / distance * (MS / ACCELERATION_TIME / GameController.FPS) * swingAccelerationFactor * Math.abs(yDiff) / distance;
            }
            if (GameController.isDpressed) {
                xVel += Math.abs(yDiff) / distance * (MS / ACCELERATION_TIME / GameController.FPS) * swingAccelerationFactor * Math.abs(yDiff) / distance;
            }
            int dirX = getSign(xVel);
            int dirY = getSign(yVel);
            if (xVel != 0) {
                if (dirX == getSign(xDiff)) {
                    dirY = -1;
                } else {
                    dirY = 1;
                }
            }
            double speed = Math.sqrt(xVel * xVel + yVel * yVel) * Math.pow(1 - swingDeaccelerationFactor, 1.0 / GameController.FPS); //improve in the end
            ratio = speed / grappleLenght;
            xVel = Math.abs(yDiff) * ratio * dirX;
            yVel = Math.abs(xDiff) * ratio * dirY;
            for (int h = -1; h <= hitboxHeight; h++) {
                if(isSolid((int) ((xLoc + xPos + hitboxWidth) / blockSize),(int) ((yLoc + yPos + h) / blockSize))) { // right
                    xVel = Math.min(0, xVel);
                    yVel = Math.max(0, yVel);
                }
                if(isSolid((int) ((xLoc + xPos - 1) / blockSize),(int) ((yLoc + yPos + h) / blockSize))) {//left
                    xVel = Math.max(0, xVel);
                    yVel = Math.max(0, yVel);
                }
            }
            for (int w = 0; w < hitboxWidth; w++) {
                if(isSolid((int) ((xLoc + xPos + w) / Board.blockSize),(int) ((yLoc + yPos - 1) / Board.blockSize))) {
                    xVel = xDiff > 0 ? Math.min(0, xVel) : Math.max(0, xVel);
                    yVel = Math.max(yVel, 0);
                }
            }
        } else {
            grappleState = 2;
            decelerate();
            if ((int) distance >= (int) grappleLenght) {
                double refXVel = xVel;
                double refYVel = yVel;
                if (refYVel < 0) {
                    yVel -= Math.abs(refYVel) * yDiff / distance / GameController.FPS; //always +, works
                    xVel += Math.abs(refYVel) * yDiff / distance * getSign(xDiff) / GameController.FPS; //towards the center, works
                }
                if (getSign(refXVel) == getSign(xDiff) && refXVel != 0 && xDiff != 0) {
                    xVel -= refXVel * xDiff / distance * getSign(refXVel) / GameController.FPS;  //in opposite dir of xVel, works
                    yVel += refXVel * xDiff / distance / GameController.FPS; //down, works
                }
            }
        }
    }

    //Spear Thrust

    public double cdSpear = 1;
    public int heightSpear = 10;
    public int lengthSpear = 50;
    public double dmgModifierSpear = 1;
    public boolean spearThrust(double tarX){

        boolean dir;
        double widthEnd;
        double high;
        if(tarX < xPos){
            dir = false;
            widthEnd = xPos - lengthSpear + hitboxWidth - 1;
        } else {
            dir = true;
            widthEnd = xPos ;
        }

        high = yPos;

        boolean willHit = false;
            if(hero.isColliding(widthEnd,high, lengthSpear, heightSpear)){
                hero.takeDamage((int)(ATK*dmgModifierSpear),false);
                willHit = true;
        }
        if( this != hero &&!willHit) return false;
        new Spearstab(widthEnd,high, lengthSpear, heightSpear,dir, this);
        return true;
    }

    //Lepers Touch

    public double cdLepers = 0.5;
    public double modifierLepers = 1;
    public boolean lepersTouch(){
        boolean output = false;
        for(Creature c : creatures){
            if (c != this && isColliding(c)){
                c.takeDamage((int) (ATK * modifierLepers),true);
                output = true;
            }
        }
        return output;
    }
    public boolean lepersTouch(Creature target){
        if (isColliding(target)){
            target.takeDamage((int) (ATK * modifierLepers),true);
            return true;
        }
        return false;
    }

    //Fireball
    public double cdFireball = 1;
    public int explosionFB = 100;
    public double modifierFB = 2;
    public void fireball(double targetX, double targetY){
        new Fireball(this,xPos,yPos,targetX,targetY,99);
    }

    //Death Sentence
    public double cdDeathSentence = 10;
    public double executionThresholdDS = 0.3;
    public int rangeDS = 50;
    public int widthDS = 50;
    public int heightDS = 50;
    public boolean deathSentence(double x){
        x = Math.max(xPos - rangeDS,x);
        x = Math.min(xPos + rangeDS, x);
        double y = getYofBlockUnder(x,this.yPos,1)*blockSize-yLoc-1;
        if(y - yPos > rangeDS) return false;
        new DeathSentence(x - widthDS, y+1- heightDS, 2* widthDS, heightDS,false);
        for(Creature c : creatures){
            if(c != this && c.isColliding(x - widthDS, y+1- heightDS, 2* widthDS, heightDS)){
                if(c.CURRENT_HP <= c.MAX_HP* executionThresholdDS){
                    c.die();
                }else {
                    c.takeDamage(ATK,false);
                }
            }
        }
        return true;
    }

    //Cry for war
    public int rangeCFW = 200;
    public int attackCFW = 20;
    public int defenseCFW = 50;
    public double durationCFW = 10;
    public double cooldownCFW = 20;
    public void cryForWar(Creature target){
        new CryForWar(xPos-rangeCFW,yPos-rangeCFW,2*rangeCFW,2*rangeCFW,false,this);
        new Buff(target,durationCFW,attackCFW,defenseCFW);
    }
    public void cryForWar(){
        new CryForWar(xPos-rangeCFW,yPos-rangeCFW,2*rangeCFW,2*rangeCFW,false,this);
        for(Creature c : creatures){
            if(c.ENTITY_ID > 1 && c.ENTITY_ID < 100 && getDistance(c) < rangeCFW) new Buff(c,durationCFW,attackCFW,defenseCFW);
        }
    }

    //Malevolent Dusk
    public boolean isInMD = false;
    public double cdMD = 30;
    public int msMD = 800;

    public double xSpeedMD;
    public double ySpeedMD;
    Creature targetMD;
    public boolean startMD(double x, double y){
        for(Creature c : creatures){
            if(c != this && x > c.xPos && x < c.xPos + c.hitboxWidth && y > c.yPos && y < c.yPos + c.hitboxHeight){
                startMD(c);
                return true;
            }
        }
        return false;
    }
    public void startMD(Creature target){
        targetMD = target;
        this.isInMD = true;

    }
    public void dashMD(){
        if(getDistance(targetMD) <= msMD*1.0/ FPS){
            xPos = targetMD.xPos;
            yPos = targetMD.yPos+targetMD.hitboxHeight-hitboxHeight;
            endMD();
        } else{
            double xDiff =  targetMD.xPos - xPos;
            double yDiff =  targetMD.yPos - yPos;
            double denominator = Math.sqrt(xDiff*xDiff+yDiff*yDiff);
            xSpeedMD = xDiff * msMD / denominator;
            ySpeedMD = yDiff * msMD / denominator;
            xPos += xSpeedMD/FPS;
            yPos += ySpeedMD/FPS;
        }
    }
    public void endMD(){
        targetMD.takeDamage(ATK,true);
        isInMD = false;
    }

    //Death Totem
    public int deathTotemCount;
    public void placeDeathTotem(){
        if(deathTotemCount != 0) return;
        new DeathTotem(xPos, getYofBlockUnder(xPos,yPos,hitboxWidth)*blockSize-yLoc-40,this);
    }

    //Attack
    public void attack(Creature c){
        if(SECONDARY_CD == 0){
            c.takeDamage(ATK,false);
            SECONDARY_CD = ATTACK_CD;
        }
    }
}
