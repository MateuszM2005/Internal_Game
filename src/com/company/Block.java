package com.company;


import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.newInputStream;

public enum Block {
    AIR(0,0,0,0,false,0),
    DIRT(1,100/255.0,45/255.0,0/255.0, true,0),
    SCORCHED_EARTH(2,53/255.0,31/255.0,25/255.0, true,0),
    ROCK(3,136/255.0,140/255.0,141/255.0, true,0),
    WATER(4,128/255.0,197/255.0,222 /255.0,false,0),
    LAVA(5,220/255.0,37/255.0,0/255.0, false,100),
    HELL_SOIL(6,114/255.0,50/255.0,50/255.0, false,1);

    private final int index;
    private final double r;
    private final double g;
    private final double b;
    private final boolean isSolid;
    private final int damage;

    public static final int AMMOUNT_OF_BLOCKS = 7;
    private static final Block[] blocks = Block.values();


    Block(int index, double r, double g, double b, boolean isSolid, int damage){
        this.index = index;
        this.r = r;
        this.g = g;
        this.b = b;
        this.isSolid = isSolid;
        this.damage = damage;
    }
    public static Color getColor(int id, int x, int y){
        double range = 0.05;

        if(Board.isAir(x,y-1)){
            if(id == 1){
                double r = Math.max(Math.min(1,5/255.0 + SimplexNoise.noise[y][x]*2*range),0);
                double g = Math.max(Math.min(1,64/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                double b = Math.max(Math.min(1,5/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                return new Color(r,g,b,1);
            }
            if(id == 2){
                range = 0.1;
                double r = Math.max(Math.min(1,106/255.0 + SimplexNoise.noise[y][x]*2*range),0);
                double g = Math.max(Math.min(1,108/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                double b = Math.max(Math.min(1,109/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                return new Color(r,g,b,1);
            }
            if(id == 4){
                range = 0.02;
                double r = Math.max(Math.min(1,175/255.0 + SimplexNoise.noise[y][x]*2*range),0);
                double g = Math.max(Math.min(1,210/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                double b = Math.max(Math.min(1,250/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                return new Color(r,g,b,0.6 + range * SimplexNoise.noise[y][x]);
            }
        }
        if(id == 1) {
            if(Board.isWater(x+1,y) || Board.isWater(x- 1,y) ||Board.isWater(x,y-1) ||Board.isWater(x,y+1) || Board.isWater(x+1,y+1) ||Board.isWater(x+1,y-1) ||Board.isWater(x-1,y+1) ||Board.isWater(x-1,y-1)){
                double r = Math.max(Math.min(1,70/255.0 + SimplexNoise.noise[y][x]*2*range),0);
                double g = Math.max(Math.min(1,46/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                double b = Math.max(Math.min(1,34/255.0  + SimplexNoise.noise[y][x]*2*range),0);
                return new Color(r,g,b,1);
            }
        }

        if(id == 3) range = 0.02;

        if(id == 4){
            range = 0.01;
            double r = Math.max(Math.min(1,blocks[id].r  + SimplexNoise.noise[y][x]*2*range),0);
            double g = Math.max(Math.min(1,blocks[id].g  + SimplexNoise.noise[y][x]*2*range),0);
            double b = Math.max(Math.min(1,blocks[id].b  + SimplexNoise.noise[y][x]*2*range),0);
            return new Color(r,g,b,0.7 + range * (SimplexNoise.noise[y][x]+1)/2);
        }
        if(id == 5) range = 0.1;


        double r = Math.max(Math.min(1,blocks[id].r  + SimplexNoise.noise[y][x]*2*range),0);
        double g = Math.max(Math.min(1,blocks[id].g  + SimplexNoise.noise[y][x]*2*range),0);
        double b = Math.max(Math.min(1,blocks[id].b  + SimplexNoise.noise[y][x]*2*range),0);
        return new Color(r,g,b,1); //usage of simplex noise in order to achieve color variation
    }
    public static boolean isAir(int id){
        return id == 0;
    }
    public static boolean isSolid(int id){
        return blocks[id].isSolid;
    }
    public static boolean isWater(int id){
        return id == 4;
    }
    public static int getDamage(int id){
        return blocks[id].damage;
    }
}
