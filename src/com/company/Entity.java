package com.company;

import static com.company.Board.*;
import static com.company.GameController.*;

public abstract class Entity {
    public boolean REMOVE;
    public double xPos;
    public double yPos;
    public double xVel;
    public double yVel;
    public int hitboxWidth;
    public int hitboxHeight;
    public Entity(double xPos, double yPos){
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public double[] getPath(){
        double[] prev = {0,0};
        double xVel = this.xVel/ FPS;
        double yVel = this.yVel/ FPS;
        double xShift = 0;
        double yShift = 0;
        double distanceToNearestX;
        double distanceToNearestY;
        boolean isColliding;
        while(true){
            distanceToNearestX = xVel > 0 ?
                    Math.ceil(xLoc + xPos + prev[0] + (xLoc + xPos+prev[0] == (int)(xLoc +xPos+prev[0]) ? 1 : 0)) - (xLoc +xPos) - prev[0]
                    : Math.floor(xLoc +xPos + prev[0] + (xLoc +xPos+prev[0] == (int)(xLoc +xPos+prev[0]) ? -1 : 0)) - (xLoc +xPos) - prev[0];
            distanceToNearestY = yVel > 0 ?
                    Math.ceil(yLoc+yPos + prev[1] + (yLoc+yPos+prev[1] == (int)(yLoc+yPos+prev[1]) ? 1 : 0)) - (yLoc+yPos) - prev[1] :
                    Math.floor(yLoc+yPos + prev[1] + (yLoc+yPos+prev[1] == (int)(yLoc+yPos+prev[1]) ? -1 : 0)) - (yLoc+yPos) - prev[1] ;
            if(xVel == 0){
                yShift = prev[1] + distanceToNearestY;
            } else if(yVel == 0){
                xShift = prev[0] + distanceToNearestX;
            } else if (distanceToNearestX / xVel > distanceToNearestY / yVel){
                yShift = prev[1] + distanceToNearestY;
                xShift = prev[0] + (distanceToNearestY * xVel / yVel);
            } else {
                xShift = prev[0] + distanceToNearestX;
                yShift = prev[1] + (distanceToNearestX * yVel / xVel);
            }
            isColliding = false;
            for (int w = 0; w < hitboxWidth; w++) {
                if(Board.isSolid((int)((xShift + xLoc + xPos+w)/Board.blockSize),(int)((yShift+ yLoc +yPos)/Board.blockSize))){
                    isColliding = true;
                    break;
                }
                if(Board.isSolid((int)((xShift + xLoc + xPos+w)/Board.blockSize),(int)((yShift+ yLoc +yPos+hitboxHeight-1)/Board.blockSize))){
                    isColliding = true;
                    break;
                }
            }
            for (int h = 0; h < hitboxHeight; h++) {
                if(isSolid((int)((xShift + xLoc + xPos)/Board.blockSize),(int)((yShift+ yLoc +yPos+h)/Board.blockSize))) {
                    isColliding = true;
                    break;
                }

                if(isSolid((int)((xShift + xLoc + xPos+hitboxWidth-1)/Board.blockSize),(int)((yShift+ yLoc +yPos+h)/Board.blockSize))) {
                    isColliding = true;
                    break;
                }
            }
            if(xShift+xPos+xLoc < 0 || xShift+xPos+xLoc+hitboxWidth > pixelLength){
                xShift = prev[0];
                xVel = prev[0];
                if(yVel==0)return prev;
            }
            if(yShift+yPos+yLoc< 0 || yShift+yPos+hitboxHeight+yLoc > pixelHeight) {
                yShift = prev[1];
                yVel = prev[1];
                if (xVel == 0) return prev;
            }

            if (isColliding) {
                return prev;
            }else{
                prev[0] = xShift;
                prev[1] = yShift;
            }

            if(Math.abs(xShift) > Math.abs(xVel) || Math.abs(yShift) > Math.abs(yVel)){
                return (new double[]{xVel,yVel});
            }
        }
    }

    public static double[] getPath(double xPos, double yPos, double xVel, double yVel, int hitboxWidth, int hitboxHeight){
        double[] prev = {0,0};
        double xShift = 0;
        double yShift = 0;
        double distanceToNearestX;
        double distanceToNearestY;
        boolean isColliding;
        while(true){
            distanceToNearestX = xVel > 0 ?
                    Math.ceil(xLoc + xPos + prev[0] + (xLoc + xPos+prev[0] == (int)(xLoc +xPos+prev[0]) ? 1 : 0)) - (xLoc +xPos) - prev[0]
                    : Math.floor(xLoc +xPos + prev[0] + (xLoc +xPos+prev[0] == (int)(xLoc +xPos+prev[0]) ? -1 : 0)) - (xLoc +xPos) - prev[0];
            distanceToNearestY = yVel > 0 ?
                    Math.ceil(yLoc+yPos + prev[1] + (yLoc+yPos+prev[1] == (int)(yLoc+yPos+prev[1]) ? 1 : 0)) - (yLoc+yPos) - prev[1] :
                    Math.floor(yLoc+yPos + prev[1] + (yLoc+yPos+prev[1] == (int)(yLoc+yPos+prev[1]) ? -1 : 0)) - (yLoc+yPos) - prev[1] ;
            if(xVel == 0){
                yShift = prev[1] + distanceToNearestY;
            } else if(yVel == 0){
                xShift = prev[0] + distanceToNearestX;
            } else if (distanceToNearestX / xVel > distanceToNearestY / yVel){
                yShift = prev[1] + distanceToNearestY;
                xShift = prev[0] + (distanceToNearestY * xVel / yVel);
            } else {
                xShift = prev[0] + distanceToNearestX;
                yShift = prev[1] + (distanceToNearestX * yVel / xVel);
            }
            isColliding = false;
            for (int w = 0; w < hitboxWidth; w++) {
                if(Board.isSolid((int)((xShift + xLoc + xPos+w)/Board.blockSize),(int)((yShift+ yLoc +yPos)/Board.blockSize))){
                    isColliding = true;
                    break;
                }
                if(Board.isSolid((int)((xShift + xLoc + xPos+w)/Board.blockSize),(int)((yShift+ yLoc +yPos+hitboxHeight-1)/Board.blockSize))){
                    isColliding = true;
                    break;
                }
            }
            for (int h = 0; h < hitboxHeight; h++) {
                if(isSolid((int)((xShift + xLoc + xPos)/Board.blockSize),(int)((yShift+ yLoc +yPos+h)/Board.blockSize))) {
                    isColliding = true;
                    break;
                }

                if(isSolid((int)((xShift + xLoc + xPos+hitboxWidth-1)/Board.blockSize),(int)((yShift+ yLoc +yPos+h)/Board.blockSize))) {
                    isColliding = true;
                    break;
                }
            }
            if(xShift+xPos+xLoc < 0 || xShift+xPos+xLoc+hitboxWidth > pixelLength){
                xShift = prev[0];
                xVel = prev[0];
                if(yVel==0)return prev;
            }
            if(yShift+yPos+yLoc< 0 || yShift+yPos+hitboxHeight+yLoc > pixelHeight) {
                yShift = prev[1];
                yVel = prev[1];
                if (xVel == 0) return prev;
            }

            if (isColliding) {
                return prev;
            }else{
                prev[0] = xShift;
                prev[1] = yShift;
            }

            if(Math.abs(xShift) > Math.abs(xVel) || Math.abs(yShift) > Math.abs(yVel)){
                return (new double[]{xVel,yVel});
            }
        }
    }

    public static int getSign(double val){
        return val >= 0 ? 1 : -1;
    }

    public static int getYofBlockUnder(double xPos, double yPos, int hitboxWidth){
        int xStart = (int)((xLoc+ xPos)/blockSize);
        int xEnd = (int)((xLoc+xPos+hitboxWidth-1)/blockSize);
        int y = (int)((yLoc + yPos)/blockSize);
        int count = 0;

        while(true){
            y++;
            if(y == pixelHeight/blockSize) return 0;
            count++;
            for(int x = xStart; x <= xEnd; x++){
                if(isSolid(x,y)) return y;
            }
            if (count > 750)return 0;
        }
    }

    public static boolean checkMapCollision(double xPos, double yPos, int hitboxWidth, int hitboxHeight){
        int xStart = (int)((xLoc+ xPos)/blockSize);
        int xEnd = (int)((xLoc+xPos+hitboxWidth-1)/blockSize);
        int yStart = (int)((yLoc+yPos)/blockSize);
        int yEnd = (int)((yLoc + yPos+hitboxHeight-1)/blockSize);
        for(int y = yStart; y <= yEnd;y++){
            if(!Board.isSolid(xStart,y) || !Board.isSolid(xEnd,y)) return false;
        }
        for(int x = xStart; x <= xEnd;x++){
            if(!Board.isSolid(x,yEnd) || !Board.isSolid(x,yStart)) return false;
        }
        return true;
    }
    public boolean checkMapCollision(){
        int xStart = (int)((xLoc+ xPos)/blockSize);
        int xEnd = (int)((xLoc+xPos+hitboxWidth-1)/blockSize);
        int yStart = (int)((yLoc+yPos)/blockSize);
        int yEnd = (int)((yLoc + yPos+hitboxHeight-1)/blockSize);
        for(int y = yStart; y <= yEnd;y++){
            if(!Board.isSolid(xStart,y) || !Board.isSolid(xEnd,y)) return false;
        }
        for(int x = xStart; x <= xEnd;x++){
            if(!Board.isSolid(x,yEnd) || !Board.isSolid(x,yStart)) return false;
        }
        return true;
    }


    public boolean isColliding(Entity e){
        double xDiff = Math.abs(this.xPos + this.hitboxWidth/2.0 - e.xPos - e.hitboxWidth/2.0);
        double yDiff = Math.abs(this.yPos + this.hitboxHeight/2.0 - e.yPos - e.hitboxWidth/2.0);
        return 2 * xDiff < this.hitboxWidth + e.hitboxWidth && 2 * yDiff < this.hitboxHeight + e.hitboxHeight;
    }
    public boolean isColliding(double x, double y, int width, int height){
        double xDiff = Math.abs(this.xPos + this.hitboxWidth/2.0 - x - width/2.0);
        double yDiff = Math.abs(this.yPos + this.hitboxHeight/2.0 - y- height/2.0);
        return 2 * xDiff < this.hitboxWidth + width && 2 * yDiff < this.hitboxHeight + height;
    }
}