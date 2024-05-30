package com.company;

import com.company.entities.Hero;

import java.util.ArrayList;
import java.util.List;

import static com.company.SaveLoader.*;

public class Board {
    public final static int XMARGIN = 300;
    public final static int YMARGIN = 200;
    public static short[][] loadedMap;
    public static int pixelLength;
    public static int pixelHeight;
    public static int currentLevel = 0;
    public static List<Missile> missiles = new ArrayList<>();
    public static List<Creature> creatures = new ArrayList<>();
    public static List<Effect> effects = new ArrayList<>();
    public static List<Buff> buffs = new ArrayList<>();
    public static String path_map;
    public static String path_entities;
    public static final int blockSize = 4;
    public static Hero hero;

    public static void load(){
        loadMap();
        loadEntities();
    }

    public static void save(double x, double y){
        saveEntities(x,y);
        saveMap();
    }

    public static void loadMap(){
        path_map = "src/levels/level"+currentLevel+"/map.txt";
        loadedMap = getMap(path_map);
        pixelLength = loadedMap[0].length*blockSize;
        pixelHeight = loadedMap.length*blockSize;
    }

    public static void loadEntities(){
        path_entities = "src/levels/level"+currentLevel+"/entities.txt";
        getEntities(path_entities);
    }


    public static void saveEntities(double x, double y){
        path_entities = "src/levels/level"+currentLevel+"/entities.txt";
        writeEntities(creatures,path_entities,x,y);
    }


    public static void saveMap(){
        path_map = "src/levels/level"+currentLevel+"/map.txt";
        writeMap(loadedMap, path_map);
    }

    public static boolean isSolid(int x, int y){
        if(x < 0 || y < 0 || x >= pixelLength/blockSize || y >= pixelHeight/blockSize) {
            return true;
        }
        return Block.isSolid(loadedMap[y][x]);
    }

    public static boolean isAir(int x, int y){
        if(x < 0 || y < 0 || x >= pixelLength/blockSize || y >= pixelHeight/blockSize) {
            return false;
        }
        return Block.isAir(loadedMap[y][x]);

    }

    public static boolean isWater(int x, int y){
        if(x < 0 || y < 0 || x >= pixelLength/blockSize || y >= pixelHeight/blockSize) {
            return false;
        }
        return Block.isWater(loadedMap[y][x]);

    }

    public static int getBlockDamage(int x, int y){
        if(x < 0 || y < 0 || x >= pixelLength/blockSize || y >= pixelHeight/blockSize) {
            return Block.getDamage(loadedMap[y][x]);
        }
        return 0;

    }
}
