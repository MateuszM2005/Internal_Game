package com.company;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.company.Board.*;
import static com.company.LevelMaker.*;
import static com.company.MainMenu.*;
import static com.company.GameController.*;

public class Painter {



    public static void initialPainting() throws IOException {
        isDrawn = false;
        root.getChildren().clear();
        Painter.paintBackground();
        root.getChildren().add(canvas);
        Painter.paintHealthbar();
        paintCreatures();

    }
    public static void paintCreatures(){
        for(Creature c : creatures){
            if(!root.getChildren().contains(c.getTexture())){
                root.getChildren().add(c.getTexture());
            }
        }
    }
    public static void paintMissles(){
        for(Missile m : missiles){
            if(!root.getChildren().contains(m.getTexture())){
                root.getChildren().add(m.getTexture());
            }
        }
    }



    public static void paintGame() throws IOException {
        clear();
        paintBoard();
        paintMissles();
        paintCreatures();
    }

    public static void initialLevelMaker() throws IOException {
        root.getChildren().clear();
        paintBackground();
        root.getChildren().add(canvas);
        paintToolbarInitial();
    }

    public static void paintSelect(LevelMaker.Select select, double xLoc, double yLoc){
        int startX = select.startX;
        int endX = select.endX;
        int startY = select.startY;
        int endY = select.endY;

        int top = (int) (Math.min(startY,endY)*blockSize-yLoc - 1);
        int bottom = (int) (Math.max(startY,endY)*blockSize-yLoc+4);

        int left = (int) (Math.min(startX,endX)*blockSize-xLoc-1);
        int right = (int) (Math.max(startX,endX)*blockSize-xLoc+4);
        gc.setFill(Color.BLACK);
        if(top >= 0){
            for(int x = left+1; x < right; x += 8){
                gc.fillRect(x,top, 4 ,1);
            }
        }
        if(bottom < HEIGHT){
            for(int x = left+1; x < right; x += 8){
                gc.fillRect(x,bottom, 4 ,1);
            }
        }
        if(left >= 0){
            for(int y = top + 1; y < bottom; y += 8){
                gc.fillRect(left, y, 1, 4);
            }
        }
        if(right < WIDTH-TB_WIDTH){
            for(int y = top + 1; y < bottom; y += 8){
                gc.fillRect(right, y, 1, 4);
            }
        }
    }

    public static void paintLevelMaker() throws IOException {
        clear();
        for (int x = (LevelMaker.locX/blockSize); x*blockSize < (LevelMaker.locX+WIDTH-TB_WIDTH) && x < pixelLength/blockSize;x++){
            for(int y = (LevelMaker.locY/blockSize); y*blockSize < (LevelMaker.locY+HEIGHT) && y < pixelHeight/blockSize;y++){
                int val = Board.loadedMap[y][x];
                if(val != 0) paintBlock((x*blockSize-LevelMaker.locX),(y*blockSize-LevelMaker.locY),val,x,y);
            }
        }
        if(isEditing) paintSelect(currentSelect, locX,locY);
        paintCreatures();
        paintToolbar();
    }

    static final int TB_WIDTH = 100;
    static final int TB_ICON_SIZE = 50;
    static final int TB_GAP = 20;
    static final int TB_MARGIN_DIVISOR = 7;
    static public int TB_ICON_X;
    static public int TB_LABEL_X;
    static public int TB_Y1;
    static public int TB_Y2;
    static public int TB_Y3;
    static public int TB_Y4;
    static public int TB_Y5;
    static public int TB_Y6;
    static public int TB_Y7;
    static public int TB_Y8;
    static public int TB_Y9;
    static public int TB_Y10;
    static public int TB_Y11;
    static public int TB_Y12;

    static ImageView back_toolbar = new ImageView();
    static ImageView adding_entity = new ImageView();
    static Label block_number_label = new Label();
    static Label width_label = new Label();
    static Label height_label = new Label();
    static ImageView tb_up1 = new ImageView();
    static ImageView tb_up2 = new ImageView();
    static ImageView tb_up3 = new ImageView();
    static ImageView tb_up4 = new ImageView();
    static ImageView tb_down1 = new ImageView();
    static ImageView tb_down2 = new ImageView();
    static ImageView tb_down3 = new ImageView();
    static ImageView tb_down4 = new ImageView();
    public static void paintToolbarInitial() throws IOException {
        TB_ICON_X = WIDTH - (TB_WIDTH + TB_ICON_SIZE)/2 + TB_WIDTH/(2*TB_MARGIN_DIVISOR);
        TB_LABEL_X = WIDTH - TB_WIDTH * (TB_MARGIN_DIVISOR-1)/TB_MARGIN_DIVISOR;
        TB_Y1 = HEIGHT/8 - TB_GAP - 3 * TB_ICON_SIZE/2;
        TB_Y2 = HEIGHT/8 -  TB_ICON_SIZE/2;
        TB_Y3 = HEIGHT/8 + TB_GAP + TB_ICON_SIZE/2;
        TB_Y4 = 7 * HEIGHT/8 - TB_GAP - 3 * TB_ICON_SIZE/2;
        TB_Y5 = 7 * HEIGHT/8 -  TB_ICON_SIZE/2;
        TB_Y6 = 7 * HEIGHT/8 + TB_GAP + TB_ICON_SIZE/2;
        TB_Y7 = 3 * HEIGHT/8 - TB_GAP - 3 * TB_ICON_SIZE/2;
        TB_Y8 = 3 * HEIGHT/8 -  TB_ICON_SIZE/2;
        TB_Y9 = 3 * HEIGHT/8 + TB_GAP + TB_ICON_SIZE/2;
        TB_Y10 = 5 * HEIGHT/8 - TB_GAP - 3 * TB_ICON_SIZE/2;
        TB_Y11 = 5 * HEIGHT/8 -  TB_ICON_SIZE/2;
        TB_Y12 = 5 * HEIGHT/8 + TB_GAP + TB_ICON_SIZE/2;
        back_toolbar.setImage(new Image(Files.newInputStream(Paths.get("src/textures/side_bar_background.png"))));
        back_toolbar.setFitWidth(TB_WIDTH);
        back_toolbar.setFitHeight(HEIGHT);
        back_toolbar.setX(WIDTH-TB_WIDTH);
        root.getChildren().add(back_toolbar);
        tb_up1.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_up.png"))));
        tb_up1.setFitWidth(TB_ICON_SIZE);
        tb_up1.setFitHeight(TB_ICON_SIZE);
        tb_up1.setY(TB_Y1);
        tb_up1.setX(TB_ICON_X);
        root.getChildren().add(tb_up1);
        tb_up2.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_up.png"))));
        tb_up2.setFitWidth(TB_ICON_SIZE);
        tb_up2.setFitHeight(TB_ICON_SIZE);
        tb_up2.setY(TB_Y4);
        tb_up2.setX(TB_ICON_X);
        root.getChildren().add(tb_up2);
        tb_up3.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_up.png"))));
        tb_up3.setFitWidth(TB_ICON_SIZE);
        tb_up3.setFitHeight(TB_ICON_SIZE);
        tb_up3.setY(TB_Y7);
        tb_up3.setX(TB_ICON_X);
        root.getChildren().add(tb_up3);
        tb_up4.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_up.png"))));
        tb_up4.setFitWidth(TB_ICON_SIZE);
        tb_up4.setFitHeight(TB_ICON_SIZE);
        tb_up4.setY(TB_Y10);
        tb_up4.setX(TB_ICON_X);
        root.getChildren().add(tb_up4);
        tb_down1.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_down.png"))));
        tb_down1.setFitWidth(TB_ICON_SIZE);
        tb_down1.setFitHeight(TB_ICON_SIZE);
        tb_down1.setY(TB_Y3);
        tb_down1.setX(TB_ICON_X);
        root.getChildren().add(tb_down1);
        tb_down2.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_down.png"))));
        tb_down2.setFitWidth(TB_ICON_SIZE);
        tb_down2.setFitHeight(TB_ICON_SIZE);
        tb_down2.setY(TB_Y6);
        tb_down2.setX(TB_ICON_X);
        root.getChildren().add(tb_down2);
        tb_down3.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_down.png"))));
        tb_down3.setFitWidth(TB_ICON_SIZE);
        tb_down3.setFitHeight(TB_ICON_SIZE);
        tb_down3.setY(TB_Y9);
        tb_down3.setX(TB_ICON_X);
        root.getChildren().add(tb_down3);
        tb_down4.setImage(new Image(Files.newInputStream(Paths.get("src/textures/button_down.png"))));
        tb_down4.setFitWidth(TB_ICON_SIZE);
        tb_down4.setFitHeight(TB_ICON_SIZE);
        tb_down4.setY(TB_Y12);
        tb_down4.setX(TB_ICON_X);
        root.getChildren().add(tb_down4);
        block_number_label.setFont(Font.font("Impact",TB_ICON_SIZE - 5));
        block_number_label.setTextFill(Color.WHITE);
        block_number_label.setAlignment(Pos.CENTER);
        block_number_label.setMinWidth(TB_WIDTH * (TB_MARGIN_DIVISOR-1)/TB_MARGIN_DIVISOR);
        block_number_label.setMaxWidth(TB_WIDTH * (TB_MARGIN_DIVISOR-1)/TB_MARGIN_DIVISOR);
        block_number_label.setMinHeight(TB_ICON_SIZE);
        block_number_label.setMaxHeight(TB_ICON_SIZE);
        block_number_label.setLayoutX(TB_LABEL_X);
        block_number_label.setLayoutY(TB_Y5);
        block_number_label.setText("" + currentBlock);
        root.getChildren().add(block_number_label);

        width_label.setFont(Font.font("Impact",TB_ICON_SIZE - 10));
        width_label.setTextFill(Color.WHITE);
        width_label.setAlignment(Pos.CENTER);
        width_label.setMinWidth(TB_WIDTH * (TB_MARGIN_DIVISOR-1)/TB_MARGIN_DIVISOR);
        width_label.setMaxWidth(TB_WIDTH * (TB_MARGIN_DIVISOR-1)/TB_MARGIN_DIVISOR);
        width_label.setMinHeight(TB_ICON_SIZE);
        width_label.setMaxHeight(TB_ICON_SIZE);
        width_label.setLayoutX(TB_LABEL_X);
        width_label.setLayoutY(TB_Y8);
        width_label.setText("" + currentMapWidth);
        root.getChildren().add(width_label);

        height_label.setFont(Font.font("Impact",TB_ICON_SIZE - 10));
        height_label.setTextFill(Color.WHITE);
        height_label.setAlignment(Pos.CENTER);
        height_label.setMinWidth(TB_WIDTH * (TB_MARGIN_DIVISOR-1)/TB_MARGIN_DIVISOR);
        height_label.setMaxWidth(TB_WIDTH * (TB_MARGIN_DIVISOR-1)/TB_MARGIN_DIVISOR);
        height_label.setMinHeight(TB_ICON_SIZE);
        height_label.setMaxHeight(TB_ICON_SIZE);
        height_label.setLayoutX(TB_LABEL_X);
        height_label.setLayoutY(TB_Y11);
        height_label.setText("" + currentMapHeight);
        root.getChildren().add(height_label);

        adding_entity.setImage(new Image(Files.newInputStream(Paths.get("src/textures/creatures/"+currentEntity+"/default.png"))));
        adding_entity.setFitWidth(TB_ICON_SIZE);
        adding_entity.setFitHeight(TB_ICON_SIZE);
        adding_entity.setY(TB_Y2);
        adding_entity.setX(TB_ICON_X);
        root.getChildren().add(adding_entity);

    }

    public static void paintToolbar() throws IOException {


        adding_entity.setImage(new Image(Files.newInputStream(Paths.get("src/textures/creatures/"+currentEntity+"/default.png"))));
        block_number_label.setText("" + currentBlock);
        width_label.setText("" + currentMapWidth);
        height_label.setText("" + currentMapHeight);


    }


    public static void clear(){
        gc.clearRect(0,0,WIDTH,HEIGHT);
    }





    public static void paintBackground() throws IOException {
        ImageView image = new ImageView(new Image(Files.newInputStream(Paths.get("src/levels/level"+currentLevel+"/background.jpg"))));
        image.setFitHeight(HEIGHT);
        image.setFitWidth(WIDTH);
        root.getChildren().add(image);
    }

    public static void paintBoard() throws IOException {
        for (int x =(int)(xLoc/blockSize); x < (xLoc+WIDTH)/blockSize && x < pixelLength/blockSize;x++){
            for(int y = (int)(yLoc/blockSize); y < (yLoc+HEIGHT)/blockSize && y < pixelHeight/blockSize;y++){
                int val = Board.loadedMap[y][x];
                if(val != 0) paintBlock((int)(x*blockSize-xLoc),(int)(y*blockSize-yLoc) ,val,x,y);
            }
        }
    }

    public static void paintBlock(int screenX, int screenY,int val, int x, int y){
        double widthBlock = blockSize;
        double heightBlock = blockSize;
        if (screenX < 0){
            widthBlock = 4+screenX;
            screenX = 0;
        }
        if(screenY < 0){
            heightBlock = 4+screenY;
            screenY = 0;
        }
        gc.setFill(Block.getColor(val,x,y));
        gc.fillRect(screenX,screenY,widthBlock,heightBlock);
    }

    static ImageView healthbar = new ImageView();
    static boolean isDrawn = false;
    static final int HB_WIDTH = 470;
    static final int HB_HEIGHT = 230;
    static final int HB_GAP = 30;
    static int HB_X;
    static int HB_Y;
    static final int i1_xShift = 40;
    static final int i2_xShift = 140;
    static final int i3_xShift = 240;
    static final int i_yShift = 40;

    static ImageView icon1;
    static ImageView icon2;
    static ImageView icon3;
    public static void paintHealthbar() throws IOException {
        int value = (int) Math.max(Math.ceil(hero.CURRENT_HP*31/hero.MAX_HP),0);
        root.getChildren().remove(icon1);
        root.getChildren().remove(icon2);
        root.getChildren().remove(icon3);
        if(isDrawn) root.getChildren().remove(healthbar);
        else {
            isDrawn = true;
            HB_X = WIDTH - HB_WIDTH - HB_GAP;
            HB_Y = HEIGHT - HB_HEIGHT - HB_GAP;
        }
        healthbar.setImage(new Image(Files.newInputStream(Paths.get("src/textures/healthbars/"+value+".png"))));
        healthbar.setX(HB_X);
        healthbar.setY(HB_Y);
        root.getChildren().add(healthbar);
        icon1 = hero.getAbility1Icon();
        if(icon1 != null){
            icon1.setFitHeight(80);
            icon1.setFitWidth(80);
            icon1.setX(HB_X + i1_xShift);
            icon1.setY(HB_Y + i_yShift);
            root.getChildren().add(icon1);
        }

        icon2 = hero.getAbility2Icon();
        if(icon2 != null){
            icon2.setFitHeight(80);
            icon2.setFitWidth(80);
            icon2.setX(HB_X + i2_xShift);
            icon2.setY(HB_Y + i_yShift);
            root.getChildren().add(icon2);
        }

        icon3 = hero.getAbility3Icon();
        if(icon3 != null){
            icon3.setFitHeight(80);
            icon3.setFitWidth(80);
            icon3.setX(HB_X + i3_xShift);
            icon3.setY(HB_Y + i_yShift);
            root.getChildren().add(icon3);
        }

    }
}
