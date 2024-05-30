package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Effect {
    public Effect(double xPos, double yPos, int width, int height, boolean dir, String path){
        this.xPos = xPos;
        this.yPos = yPos;
        this.height = height;
        this.width = width;
        this.dir = dir;
        this.path = path;
        Board.effects.add(this);
        paint();
    }
    public boolean remove;
    public boolean dir;
    public double duration;
    public double xPos;
    public double yPos;
    public int width;
    public int height;
    public String path;
    public int phase = 1;
    public ImageView image = new ImageView();
    public void paint(){
        //later code here for phase adjustment
        try {
            image.setImage(new Image(Files.newInputStream(Paths.get(path+"/"+phase+".png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        image.setX(xPos);
        image.setY(yPos);
        image.setFitWidth(width);
        image.setFitHeight(height);
        if(dir) {
            image.setRotate(180);
        }
        MainMenu.root.getChildren().add(image);
    }

    public boolean execute(){
        duration -= 1.0/GameController.FPS;
        if(duration < 0){
            remove = true;
            MainMenu.root.getChildren().remove(image);
        }
        return !(duration < 0);
    }


}
