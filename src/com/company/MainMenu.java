package com.company;

import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainMenu {

    public static int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    public static final Timeline timeline = new Timeline();

    public static AnchorPane root = new AnchorPane();
    public static Scene scene = new Scene(root,WIDTH,HEIGHT);
    public static Stage stage = new Stage();
    public static final Canvas canvas = new Canvas(WIDTH,HEIGHT);
    public static final GraphicsContext gc = canvas.getGraphicsContext2D();
    public static final int MARGIN = 20;


    public static void startUp() throws IOException {
        mainMenu();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
    }
    public static void mainMenu() throws IOException {
        root.getChildren().clear();
        final ImageView selectedImage1 = new ImageView();
        Image image1 = new Image(Files.newInputStream(Paths.get("src/textures/starting_screen.jpg")));
        selectedImage1.setFitHeight(HEIGHT);
        selectedImage1.setFitWidth(WIDTH);
        selectedImage1.setImage(image1);
        root.getChildren().add(selectedImage1);

        final ImageView selectedImage2 = new ImageView();
        Image image2 = new Image(Files.newInputStream(Paths.get("src/textures/play_button.png")));
        double b2_width = image2.getWidth();
        double b2_height = image2.getHeight();
        double b2_xPos = WIDTH/2.0 - b2_width/2 - 1;
        double b2_yPos = HEIGHT/2.0 - b2_height*1.5 - 1 - MARGIN;
        selectedImage2.setImage(image2);
        selectedImage2.setX(b2_xPos);
        selectedImage2.setY(b2_yPos);
        root.getChildren().add(selectedImage2);

        final ImageView selectedImage3 = new ImageView();
        Image image3= new Image(Files.newInputStream(Paths.get("src/textures/make_button.png")));
        double b3_width = image3.getWidth();
        double b3_height = image3.getHeight();
        double b3_xPos = WIDTH/2.0 - b3_width/2 - 1;
        double b3_yPos = HEIGHT/2.0 - b3_height*0.5 - 1;
        selectedImage3.setImage(image3);
        selectedImage3.setX(b3_xPos);
        selectedImage3.setY(b3_yPos);
        root.getChildren().add(selectedImage3);

        final ImageView selectedImage4 = new ImageView();
        Image image4= new Image(Files.newInputStream(Paths.get("src/textures/exit_button.png")));
        double b4_width = image4.getWidth();
        double b4_height = image4.getHeight();
        double b4_xPos = WIDTH/2.0 - b4_width/2 - 1;
        double b4_yPos = HEIGHT/2.0 + b4_height*0.5 - 1 + MARGIN;
        selectedImage4.setImage(image4);
        selectedImage4.setX(b4_xPos);
        selectedImage4.setY(b4_yPos);
        root.getChildren().add(selectedImage4);

        scene.setOnMousePressed(mouseEvent -> {});
        scene.setOnMouseDragged(mouseEvent -> {});
        scene.setOnMouseReleased(mouseEvent -> {});
        scene.setOnKeyPressed(keyEvent -> {});
        scene.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            if(x >= b2_xPos && x < b2_xPos + b2_width && y >= b2_yPos && y < b2_yPos + b2_height) {
                try {
                    LevelSelection.levelSelectionMenu(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(x >= b3_xPos && x < b3_xPos + b3_width && y >= b3_yPos && y < b3_yPos + b3_height) {
                try {
                    LevelMaker.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(x >= b4_xPos && x < b4_xPos + b4_width && y >= b4_yPos && y < b4_yPos + b4_height) stage.close();
        });
    }

}
