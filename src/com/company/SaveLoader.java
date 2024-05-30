package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class SaveLoader {
    public static short[][] getMap(String filePath){
        ArrayList<ArrayList<Short>> data = new ArrayList<>();
        try {
            Scanner reader = new Scanner(new File(filePath));
            while (reader.hasNext()){
                ArrayList<Short> list = new ArrayList<>();
                String[] s = reader.nextLine().split(",");
                for (String value : s) {
                    list.add(Short.parseShort(value));
                }
            data.add(list);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return listToTable(data);
    }

    public static short[][] listToTable(ArrayList<ArrayList<Short>> data){
        short[][] output = new short[data.size()][];
        for (int i = 0; i < data.size(); i++){
            short[] processed = new short[data.get(i).size()];
            for (int j = 0; j < data.get(i).size(); j++){
                processed[j] = data.get(i).get(j);
            }
            output[i] = processed;
        }
        return output;
    }

    public static void writeMap(short[][] input,String filePath){
        try {
            FileWriter writer = new FileWriter(filePath);
            for(int i = 0; i < input.length; i++){
                StringBuilder builder = new StringBuilder();
                for(int j = 0; j < input[i].length - 1;j++){
                    builder.append(input[i][j]).append(",");
                }
                builder.append(input[i][input[i].length-1]).append(i == input.length-1 ? "" : "\n");
                String line = builder.toString();
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeEntities(List<Creature> creatures, String filePath, double x, double y){
        try {
            FileWriter writer = new FileWriter(filePath);
            for(Creature c : creatures){
                String line = c.ENTITY_ID + ";" + (c.xPos + x) +";"+ (c.yPos + y) + "\n";
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getEntities(String filePath){
        try {
            Scanner reader = new Scanner(new File(filePath));
            while (reader.hasNext()){
                String[] s = reader.nextLine().split(";");
                int ID;
                double xPos;
                double yPos;
                ID = Integer.parseInt(s[0]);
                xPos = Double.parseDouble(s[1]);
                yPos = Double.parseDouble(s[2]);
                Creatures.getByID(ID,xPos,yPos);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
