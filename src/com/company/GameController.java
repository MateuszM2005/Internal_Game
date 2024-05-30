package com.company;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.company.Board.creatures;
import static com.company.Board.hero;
import static com.company.MainMenu.*;


public class GameController {

    public static final int FPS = 100;
    public static final int millis = 1000/FPS;
    public static double mouseX;
    public static double mouseY;
    public static double xLoc = 0; //top left corner
    public static double yLoc = 0;


    public static void runGame() throws IOException {
        Board.load();
        SimplexNoise.getNoise();
        Painter.initialPainting();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(millis), event -> {
            controls();
            if(isApressed){
                Board.hero.moveLeft();
            }
            if(isDpressed){
                Board.hero.moveRight();
            }
            if(isLFTpressed && !wasLFTpressed){
                Board.hero.grapple(mouseX,mouseY);
                wasLFTpressed = true;
            }
            if(isRGHTpressed){
                Board.hero.attack(mouseX,mouseY);
                wasRGHTpressed = true;
            }

            for(Missile m : Board.missiles) m.execute();
            for(Effect e : Board.effects) e.execute();
            for(Buff b : Board.buffs) b.tick();
            boolean didWin = true;
            for(int i = 0; i < Board.creatures.size(); i++){
                creatures.get(i).execute();
                if(creatures.get(i).ENTITY_ID > 2 && creatures.get(i).ENTITY_ID < 100) didWin = false;
                if(Board.creatures.get(i).REMOVE){
                    root.getChildren().remove(creatures.get(i).getTexture());
                    Board.creatures.remove(i);
                    i--;
                }
            }
            if(didWin) {
                try {
                    winScreen();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for(int i = 0; i < Board.missiles.size(); i++){
                if(Board.missiles.get(i).REMOVE){
                    Board.missiles.remove(i);
                    i--;
                }
            }
            for(int i = 0; i < Board.effects.size(); i++){
                if(Board.effects.get(i).remove){
                    Board.effects.remove(i);
                    i--;
                }
            }
            for(int i = 0; i < Board.buffs.size(); i++){
                if(Board.buffs.get(i).REMOVE){
                    Board.buffs.remove(i);
                    i--;
                }
            }
            try {
                Painter.paintGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        timeline.play();
    }

    static ImageView blur;
    static ImageView paused_button_1;
    static ImageView paused_button_2;
    public static void pauseScreen() throws IOException {
        timeline.pause();
        blur = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/pause_screen_blur.jpg"))));
        blur.setOpacity(0.25);
        blur.setFitWidth(WIDTH);
        blur.setFitHeight(HEIGHT);
        root.getChildren().add(blur);

        Image b1 = new Image(Files.newInputStream(Paths.get("src/textures/resume_button.png")));
        double b1_width = b1.getWidth();
        double b1_height = b1.getHeight();
        double b1_xPos = WIDTH/2.0 - 1 - b1_width/2.0;
        double b1_yPos = HEIGHT/2.0 - 1 - b1_height - MARGIN/2.0;
        paused_button_1 = new ImageView();
        paused_button_1.setImage(b1);
        paused_button_1.setX(b1_xPos);
        paused_button_1.setY(b1_yPos);
        root.getChildren().add(paused_button_1);

        Image b2 = new Image(Files.newInputStream(Paths.get("src/textures/quit_button.png")));
        double b2_width = b2.getWidth();
        double b2_height = b2.getHeight();
        double b2_xPos = WIDTH/2.0 - 1 - b2_width/2.0;
        double b2_yPos = HEIGHT/2.0 - 1  + MARGIN/2.0;
        paused_button_2 = new ImageView();
        paused_button_2.setImage(b2);
        paused_button_2.setX(b2_xPos);
        paused_button_2.setY(b2_yPos);
        root.getChildren().add(paused_button_2);

        scene.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            if(x >= b1_xPos && x < b1_xPos + b1_width && y >= b1_yPos && y < b1_yPos + b1_height) {
                root.getChildren().remove(blur);
                root.getChildren().remove(paused_button_1);
                root.getChildren().remove(paused_button_2);
                blur = null;
                paused_button_1 = null;
                paused_button_2 = null;
                timeline.play();
            }
            if(x >= b2_xPos && x < b2_xPos + b2_width && y >= b2_yPos && y < b2_yPos + b2_height) {
                xLoc = 0;
                yLoc = 0;
                Board.creatures.clear();
                Board.missiles.clear();
                timeline.stop();
                timeline.getKeyFrames().clear();
                try {
                    MainMenu.mainMenu();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE){
                root.getChildren().remove(blur);
                root.getChildren().remove(paused_button_1);
                root.getChildren().remove(paused_button_2);
                blur = null;
                paused_button_1 = null;
                paused_button_2 = null;
                timeline.play();
            }
        });
        scene.setOnMouseDragged(mouseEvent -> {});
        scene.setOnMousePressed(mouseEvent -> {});
        scene.setOnMouseReleased(mouseEvent -> {});
    }


    public static ImageView button_1;
    public static ImageView image_1;
    public static void deathScreen() throws IOException {
        timeline.pause();
        blur = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/pause_screen_blur.jpg"))));
        blur.setOpacity(0.25);
        blur.setFitWidth(WIDTH);
        blur.setFitHeight(HEIGHT);
        root.getChildren().add(blur);
        Image b1 = new Image(Files.newInputStream(Paths.get("src/textures/quit_button.png")));
        double b1_width = b1.getWidth();
        double b1_height = b1.getHeight();
        double b1_xPos = WIDTH/2.0 - b1_width/2.0;
        double b1_yPos = HEIGHT/2.0 - 1  + MARGIN/2.0;
        button_1 = new ImageView();
        button_1.setImage(b1);
        button_1.setX(b1_xPos);
        button_1.setY(b1_yPos);


        Image i1 = new Image(Files.newInputStream(Paths.get("src/textures/you_died.png")));
        double i1_width = i1.getWidth();
        double i1_height = i1.getHeight();
        double i1_xPos = WIDTH/2.0  - i1_width/2.0;
        double i1_yPos =  HEIGHT/2.0 - 1 - b1_height - MARGIN/2.0;
        image_1 = new ImageView();
        image_1.setImage(i1);
        image_1.setX(i1_xPos);
        image_1.setY(i1_yPos);
        root.getChildren().add(button_1);
        root.getChildren().add(image_1);
        scene.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            if(x >= b1_xPos && x < b1_xPos + b1_width && y >= b1_yPos && y < b1_yPos + b1_height) {
                xLoc = 0;
                yLoc = 0;
                Board.creatures.clear();
                Board.missiles.clear();
                timeline.stop();
                timeline.getKeyFrames().clear();
                try {
                    MainMenu.mainMenu();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scene.setOnKeyPressed(e -> { });
        scene.setOnMouseDragged(mouseEvent -> {});
        scene.setOnMousePressed(mouseEvent -> {});
        scene.setOnMouseReleased(mouseEvent -> {});
    }

    public static void winScreen() throws IOException {
        timeline.pause();
        blur = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/pause_screen_blur.jpg"))));
        blur.setOpacity(0.25);
        blur.setFitWidth(WIDTH);
        blur.setFitHeight(HEIGHT);
        root.getChildren().add(blur);
        Image b1 = new Image(Files.newInputStream(Paths.get("src/textures/quit_button.png")));
        double b1_width = b1.getWidth();
        double b1_height = b1.getHeight();
        double b1_xPos = WIDTH/2.0 - b1_width/2.0;
        double b1_yPos = HEIGHT/2.0 - 1  + MARGIN/2.0;
        button_1 = new ImageView();
        button_1.setImage(b1);
        button_1.setX(b1_xPos);
        button_1.setY(b1_yPos);


        Image i1 = new Image(Files.newInputStream(Paths.get("src/textures/you_won.png")));
        double i1_width = i1.getWidth();
        double i1_height = i1.getHeight();
        double i1_xPos = WIDTH/2.0  - i1_width/2.0;
        double i1_yPos =  HEIGHT/2.0 - 1 - b1_height - MARGIN/2.0;
        image_1 = new ImageView();
        image_1.setImage(i1);
        image_1.setX(i1_xPos);
        image_1.setY(i1_yPos);
        root.getChildren().add(button_1);
        root.getChildren().add(image_1);
        scene.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            if(x >= b1_xPos && x < b1_xPos + b1_width && y >= b1_yPos && y < b1_yPos + b1_height) {
                xLoc = 0;
                yLoc = 0;
                Board.creatures.clear();
                Board.missiles.clear();
                timeline.stop();
                timeline.getKeyFrames().clear();
                try {
                    MainMenu.mainMenu();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scene.setOnKeyPressed(e -> { });
        scene.setOnMouseDragged(mouseEvent -> {});
        scene.setOnMousePressed(mouseEvent -> {});
        scene.setOnMouseReleased(mouseEvent -> {});
    }

    public static boolean isApressed = false;
    public static boolean isDpressed = false;
    public static boolean isLFTpressed = false;
    public static boolean wasLFTpressed = false;
    public static boolean isRGHTpressed = false;
    public static boolean wasRGHTpressed = false;

    public static void controls(){
        scene.setOnMouseMoved(mouseEvent -> {
            mouseX = mouseEvent.getSceneX();
            mouseY = mouseEvent.getSceneY();
        });
        scene.setOnMouseClicked(mouseEvent -> {
            mouseX = mouseEvent.getSceneX();
            mouseY = mouseEvent.getSceneY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            mouseX = mouseEvent.getSceneX();
            mouseY = mouseEvent.getSceneY();
        });
        scene.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode()==KeyCode.A){
                isApressed=false;
            }
            if(keyEvent.getCode()==KeyCode.D){
                isDpressed=false;
            }
        });
        scene.setOnMouseReleased(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                isLFTpressed = false;
                wasLFTpressed = false;
                if(Board.hero.grappleState != 0)
                    Board.hero.grapple.terminate();
            }
            if(mouseEvent.getButton() == MouseButton.SECONDARY){
                isRGHTpressed = false;
                wasRGHTpressed = false;

            }
        });
        scene.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode()==KeyCode.A){
                isApressed=true;
            }
            if(keyEvent.getCode() == KeyCode.D){
                isDpressed = true;
            }
            if(keyEvent.getCode() == KeyCode.W){
                Board.hero.doubleJump();
                Board.hero.jump();
            }
            if(keyEvent.getCode() == KeyCode.S){
                Board.hero.diveDown();
            }
            if(keyEvent.getCode() == KeyCode.DIGIT1){
                hero.useAbility1(mouseX,mouseY);
            }
            if(keyEvent.getCode() == KeyCode.DIGIT2){
                hero.useAbility2(mouseX,mouseY);
            }
            if(keyEvent.getCode() == KeyCode.DIGIT3){
                hero.useAbility3(mouseX,mouseY);
            }
            if(keyEvent.getCode() == KeyCode.ESCAPE){
                try {
                    pauseScreen();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        scene.setOnMousePressed(mouseEvent -> {
            mouseX = mouseEvent.getSceneX();
            mouseY = mouseEvent.getSceneY();
            if(mouseEvent.getButton() == MouseButton.SECONDARY){
                isRGHTpressed = true;
            }
            if(mouseEvent.getButton()==MouseButton.PRIMARY){
                isLFTpressed = true;
            }
        });
    }
}
