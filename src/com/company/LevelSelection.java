package com.company;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.company.MainMenu.*;
import static com.company.MainMenu.scene;

public class LevelSelection {
    static final int AMMOUNT_OF_LEVELS = 5;
    static final int IMG_WIDTH = 640;
    static final int IMG_HEIGHT = 360;
    static final int GAP = 200;
    static final int BUTTON_SIZE = 100;
    public static void levelSelectionMenu(int currentPos) throws IOException {
        int id1 = currentPos + 1;
        double i1_xPos = WIDTH/2.0 - 1 - GAP/2.0 - IMG_WIDTH;
        double i1_yPos = HEIGHT/2.0 - 1 - GAP/2.0 - IMG_HEIGHT;
        int id2 = currentPos + 2;
        double i2_xPos = WIDTH/2.0 - 1 + GAP/2.0;
        double i2_yPos = HEIGHT/2.0 - 1 - GAP/2.0 - IMG_HEIGHT;
        int id3 = currentPos + 3;
        double i3_xPos = WIDTH/2.0 - 1 - GAP/2.0 - IMG_WIDTH;
        double i3_yPos = HEIGHT/2.0 - 1 + GAP/2.0;
        int id4 = currentPos + 4;
        double i4_xPos = WIDTH/2.0 - 1 + GAP/2.0;
        double i4_yPos = HEIGHT/2.0 - 1 + GAP/2.0;

        boolean leftArrow = currentPos > 0;
        double iL_xPos = WIDTH/2.0 - 1 - GAP* 1.5 - IMG_WIDTH - BUTTON_SIZE;
        double iL_yPos = HEIGHT/2.0 - 1 - BUTTON_SIZE/2.0;
        boolean rightArrow = currentPos+4 < AMMOUNT_OF_LEVELS;
        double iR_xPos = WIDTH/2.0 - 1 + GAP* 1.5 + IMG_WIDTH;
        double iR_yPos = HEIGHT/2.0 - 1 - BUTTON_SIZE/2.0;
        root.getChildren().clear();

        final ImageView selectedImage0 = new ImageView();
        Image image0 = new Image(Files.newInputStream(Paths.get("src/textures/level_screen.jpg")));
        selectedImage0.setImage(image0);
        selectedImage0.setFitHeight(HEIGHT);
        selectedImage0.setFitWidth(WIDTH);
        root.getChildren().add(selectedImage0);

        if(id1 <= AMMOUNT_OF_LEVELS){
            final ImageView selectedImage1 = new ImageView();
            Image image1 = new Image(Files.newInputStream(Paths.get("src/levels/level"+id1+"/background.jpg")));
            selectedImage1.setImage(image1);
            selectedImage1.setFitHeight(IMG_HEIGHT);
            selectedImage1.setFitWidth(IMG_WIDTH);
            selectedImage1.setX(i1_xPos);
            selectedImage1.setY(i1_yPos);
            final ImageView frame = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/level_frame.png"))));
            frame.setFitWidth(IMG_WIDTH*17.0/16);
            frame.setFitHeight(IMG_HEIGHT*10.0/9);
            frame.setX(i1_xPos-1.0/32*IMG_WIDTH);
            frame.setY(i1_yPos-1.0/18*IMG_HEIGHT);
            root.getChildren().add(frame);
            root.getChildren().add(selectedImage1);
        }
        if(id2 <= AMMOUNT_OF_LEVELS){
            final ImageView selectedImage2 = new ImageView();
            Image image2 = new Image(Files.newInputStream(Paths.get("src/levels/level"+id2+"/background.jpg")));
            selectedImage2.setImage(image2);
            selectedImage2.setFitHeight(IMG_HEIGHT);
            selectedImage2.setFitWidth(IMG_WIDTH);
            selectedImage2.setX(i2_xPos);
            selectedImage2.setY(i2_yPos);
            final ImageView frame = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/level_frame.png"))));
            frame.setFitWidth(IMG_WIDTH*17.0/16);
            frame.setFitHeight(IMG_HEIGHT*10.0/9);
            frame.setX(i2_xPos-1.0/32*IMG_WIDTH);
            frame.setY(i2_yPos-1.0/18*IMG_HEIGHT);
            root.getChildren().add(frame);
            root.getChildren().add(selectedImage2);
        }
        if(id3 <= AMMOUNT_OF_LEVELS){
            final ImageView selectedImage3 = new ImageView();
            Image image3 = new Image(Files.newInputStream(Paths.get("src/levels/level"+id3+"/background.jpg")));
            selectedImage3.setImage(image3);
            selectedImage3.setFitHeight(IMG_HEIGHT);
            selectedImage3.setFitWidth(IMG_WIDTH);
            selectedImage3.setX(i3_xPos);
            selectedImage3.setY(i3_yPos);
            final ImageView frame = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/level_frame.png"))));
            frame.setFitWidth(IMG_WIDTH*17.0/16);
            frame.setFitHeight(IMG_HEIGHT*10.0/9);
            frame.setX(i3_xPos-1.0/32*IMG_WIDTH);
            frame.setY(i3_yPos-1.0/18*IMG_HEIGHT);
            root.getChildren().add(frame);
            root.getChildren().add(selectedImage3);
        }
        if(id4 <= AMMOUNT_OF_LEVELS){
            final ImageView selectedImage4 = new ImageView();
            Image image4 = new Image(Files.newInputStream(Paths.get("src/levels/level"+id4+"/background.jpg")));
            selectedImage4.setImage(image4);
            selectedImage4.setFitHeight(IMG_HEIGHT);
            selectedImage4.setFitWidth(IMG_WIDTH);
            selectedImage4.setX(i4_xPos);
            selectedImage4.setY(i4_yPos);
            final ImageView frame = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/level_frame.png"))));
            frame.setFitWidth(IMG_WIDTH*17.0/16);
            frame.setFitHeight(IMG_HEIGHT*10.0/9);
            frame.setX(i4_xPos-1.0/32*IMG_WIDTH);
            frame.setY(i4_yPos-1.0/18*IMG_HEIGHT);
            root.getChildren().add(frame);
            root.getChildren().add(selectedImage4);
        }
        if(leftArrow){
            final ImageView selectedImageL = new ImageView();
            Image imageL = new Image(Files.newInputStream(Paths.get("src/textures/button_left.png")));
            selectedImageL.setImage(imageL);
            selectedImageL.setFitHeight(BUTTON_SIZE);
            selectedImageL.setFitWidth(BUTTON_SIZE);
            selectedImageL.setX(iL_xPos);
            selectedImageL.setY(iL_yPos);
            root.getChildren().add(selectedImageL);
        }
        if(rightArrow){
            final ImageView selectedImageR = new ImageView();
            Image imageR = new Image(Files.newInputStream(Paths.get("src/textures/button_right.png")));
            selectedImageR.setImage(imageR);
            selectedImageR.setFitHeight(BUTTON_SIZE);
            selectedImageR.setFitWidth(BUTTON_SIZE);
            selectedImageR.setX(iR_xPos);
            selectedImageR.setY(iR_yPos);
            root.getChildren().add(selectedImageR);
        }
        scene.setOnKeyPressed(keyEvent -> {
            if((keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) && leftArrow){
                try {
                    levelSelectionMenu(currentPos - 4);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if((keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) && rightArrow){
                try {
                    levelSelectionMenu(currentPos + 4);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(keyEvent.getCode() == KeyCode.ESCAPE){
                try {
                    mainMenu();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scene.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            if(x >= i1_xPos && x < i1_xPos+IMG_WIDTH && y >= i1_yPos && y < i1_yPos+IMG_HEIGHT && id1<=AMMOUNT_OF_LEVELS){
                Board.currentLevel = currentPos + 1;
                try {
                    GameController.runGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(x >= i2_xPos && x < i2_xPos+IMG_WIDTH && y >= i2_yPos && y < i2_yPos+IMG_HEIGHT && id2<=AMMOUNT_OF_LEVELS){
                Board.currentLevel = currentPos + 2;
                try {
                    GameController.runGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(x >= i3_xPos && x < i3_xPos+IMG_WIDTH && y >= i3_yPos && y < i3_yPos+IMG_HEIGHT && id3<=AMMOUNT_OF_LEVELS){
                Board.currentLevel = currentPos + 3;
                try {
                    GameController.runGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(x >= i4_xPos && x < i4_xPos+IMG_WIDTH && y >= i4_yPos && y < i4_yPos+IMG_HEIGHT && id4<=AMMOUNT_OF_LEVELS){
                Board.currentLevel = currentPos + 4;
                try {
                    GameController.runGame();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(x >= iL_xPos && x < iL_xPos+BUTTON_SIZE && y >= iL_yPos && y < iL_yPos+BUTTON_SIZE && leftArrow){
                try {
                    levelSelectionMenu(currentPos - 4);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(x >= iR_xPos && x < iR_xPos+BUTTON_SIZE && y >= iR_yPos && y < iR_yPos+BUTTON_SIZE && rightArrow){
                try {
                    levelSelectionMenu(currentPos + 4);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scene.setOnMousePressed(e -> {

        });
        scene.setOnMouseDragged(e -> {

        });
        scene.setOnMouseReleased(e -> {

        });
    }
}
