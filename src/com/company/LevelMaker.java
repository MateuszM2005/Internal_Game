package com.company;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

import static com.company.Painter.*;
import static com.company.Board.*;
import static com.company.MainMenu.*;



public class LevelMaker {
    public static int locX = 0;
    public static int locY = 0;
    public static final int cameraSpeed = 20;
    public static final int time = 20;
    static boolean isAPressed;
    static boolean isSPressed;
    static boolean isDPressed;
    static boolean isWPressed;

    static boolean isWidthDownPressed;
    static boolean isWidthUpPressed;

    static boolean isHeightDownPressed;
    static boolean isHeightUpPressed;

    static double cursorX;
    static double cursorY;
    final static int MIN_MAP_WIDTH = 1024;
    final static int MIN_MAP_HEIGHT = 256;
    final static int MAX_MAP_WIDTH = 8192;
    final static int MAX_MAP_HEIGHT = 4069;
    public static int currentMapWidth;
    public static int currentMapHeight;

    public static short currentBlock = 0;
    public static int currentEntity = 1;

    public static void start() throws IOException {
        hero = null;
        currentLevel = 4;
        Board.load();
        currentMapWidth = pixelLength/blockSize;
        currentMapHeight = pixelHeight/blockSize;
        SimplexNoise.getNoise();
        Painter.initialLevelMaker();
        runMapMaker();
        paintLevelMaker();
    }

    public static int timer;
    public static boolean isEditing;
    public static Select currentSelect;

    public static boolean isDragging;
    public static Creature dragObject;
    public static double dragX;
    public static double dragY;


    public static class Select{
        int startX, endX, startY,endY;
        short value;
        public Select(double startX, double startY, short value){
            this.startX =  Math.min((int) ((startX + locX)/blockSize),currentMapWidth-1);
            this.startY =  Math.min((int) ((startY+locY)/blockSize),currentMapHeight-1);
            endX = this.startX;
            endY = this.startY;
            this.value = value;

        }
        public void moveEnd(double endX, double endY){
            this.endX = Math.min((int) ((endX+locX)/blockSize),currentMapWidth-1);
            this.endY = Math.min((int) ((endY+locY)/blockSize), currentMapHeight-1);
        }
        public void fillRect(){
            int x1 = Math.max(0,Math.min(startX,endX));
            int x2 = Math.min(Math.max(startX,endX),currentMapWidth-1);

            int y1 = Math.max(0,Math.min(startY,endY));
            int y2 = Math.min(Math.max(startY,endY),currentMapHeight-1);
            new Operation(x1,y1,x2,y2,true);
            for(int y = y1; y <= y2; y++){
                for(int x = x1; x <= x2; x++){
                    loadedMap[y][x] = value;
                }
            }
        }
    }
    public static Stack<Operation> history = new Stack<>();
    public static Stack<Operation> undoHistory = new Stack<>();
    public static class Operation{
        int xStart, yStart,endX,endY;
        short[][] data;
        public Operation(int xStart, int yStart, int endX, int endY, boolean original){
            this.xStart = xStart;
            this.endX = endX;
            this.yStart = yStart;
            this.endY = endY;
            data = new short[endY - yStart+1][endX - xStart + 1];
            for(int y = yStart; y <= endY; y++){
                for(int x = xStart; x <= endX; x++){
                    data[y-yStart][x-xStart] = loadedMap[y][x];
                }
            }
            if(original) history.add(this); else undoHistory.add(this);
        }
        public void undo(){
            new Operation(xStart,yStart,endX,endY,false);
            for(int y = yStart; y <= endY; y++){
                for(int x = xStart; x <= endX; x++){
                    loadedMap[y][x] = data[y-yStart][x-xStart];
                }
            }
        }
        public void redo(){
            new Operation(xStart,yStart,endX,endY,true);
            for(int y = yStart; y <= endY; y++){
                for(int x = xStart; x <= endX; x++){
                    loadedMap[y][x] = data[y-yStart][x-xStart];
                }
            }
        }
    }
    public static void undo(){
        if(!history.empty()){
            Operation operation = history.pop();
            operation.undo();
            checkEntityTerrainCollision();
        }
        try {
            paintLevelMaker();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void redo(){
        if(!undoHistory.empty()){
            Operation operation = undoHistory.pop();
            operation.redo();
            checkEntityTerrainCollision();
        }
        try {
            paintLevelMaker();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resizeBoard(int x, int y){
        short[][] temp = new short[y][x];
        x = Math.min(x,pixelLength/blockSize);
        y = Math.min(y,pixelHeight/blockSize);
        for(int y1 = 0; y1 < y; y1++){
            System.arraycopy(loadedMap[y1], 0, temp[y1], 0, x);
        }
        loadedMap = temp;
        pixelHeight = temp.length*blockSize;
        pixelLength = temp[0].length*blockSize;
        locX = Math.max(Math.min(pixelLength-WIDTH+TB_WIDTH-1,locX),0);
        locY = Math.max(Math.min(pixelHeight-HEIGHT-1,locY),0);
    }
    public static void checkEntityTerrainCollision(){
        for(int i = 0; i < Board.creatures.size(); i++){
            boolean doBreak = false;
            for(int x = (int) ((creatures.get(i).xPos + locX)/blockSize); x <= (int) ((creatures.get(i).xPos + locX + creatures.get(i).hitboxWidth)/blockSize); x++){
                for(int y = (int) ((creatures.get(i).yPos + locY)/blockSize); y <= (int) ((creatures.get(i).yPos + locY + creatures.get(i).hitboxWidth)/blockSize); y++){
                    if(isSolid(x,y)){
                        if(creatures.get(i).ENTITY_ID == 1) hero = null;
                        root.getChildren().remove(creatures.get(i).getTexture());
                        Board.creatures.remove(i);
                        i--;
                        doBreak = true;
                        break;
                    }
                }
                if(doBreak) break;
            }
        }
    }

    public static void configureKeyBinds(){
        scene.setOnMouseClicked(event -> {

        });
        scene.setOnMousePressed(event -> {
            cursorX = event.getSceneX();
            cursorY = event.getSceneY();
            if(cursorX > WIDTH-TB_WIDTH) {
                cursorX = event.getSceneX();
                cursorY = event.getSceneY();
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y1 && cursorY < TB_Y1 + TB_ICON_SIZE ){
                    currentEntity = Math.min(currentEntity+1,Creatures.AMOUNT_OF_ENTITIES);
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y3 && cursorY < TB_Y3 + TB_ICON_SIZE ){
                    currentEntity = Math.max(currentEntity - 1, 1);
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y4 && cursorY < TB_Y4 + TB_ICON_SIZE ){
                    currentBlock = (short) Math.min(currentBlock + 1,Block.AMMOUNT_OF_BLOCKS-1);
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y6 && cursorY < TB_Y6 + TB_ICON_SIZE ){
                    currentBlock = (short) Math.max(currentBlock - 1, 0);
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y7 && cursorY < TB_Y7 + TB_ICON_SIZE ){
                    isWidthUpPressed = true;
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y9 && cursorY < TB_Y9 + TB_ICON_SIZE ){
                    isWidthDownPressed = true;
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y10 && cursorY < TB_Y10 + TB_ICON_SIZE ){
                    isHeightUpPressed = true;
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y12 && cursorY < TB_Y12 + TB_ICON_SIZE ){
                    isHeightDownPressed = true;
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y12 && cursorY < TB_Y12 + TB_ICON_SIZE ){
                    isHeightDownPressed = true;
                }
                if(cursorX >= TB_ICON_X && cursorX < TB_ICON_X + TB_ICON_SIZE && cursorY >= TB_Y2 && cursorY < TB_Y2 + TB_ICON_SIZE ){
                    if(currentEntity != 1 || hero == null){
                        isDragging = true;
                        dragObject = Creatures.getByID(currentEntity,cursorX,cursorY);
                        dragX = 0;
                        dragY = 0;
                    }

                }

            }else {
                for(Creature c : creatures){
                    if(cursorX >= c.xPos && cursorX < c.xPos + c.hitboxWidth && cursorY >= c.yPos && cursorY < c.yPos + c.hitboxHeight){
                        dragObject = c;
                        dragX = c.xPos - cursorX;
                        dragY = c.yPos - cursorY;
                        isDragging = true;

                        try {
                            paintLevelMaker();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }
                currentSelect = new Select(cursorX,cursorY, currentBlock);
                isEditing = true;
            }

            try {
                paintLevelMaker();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        scene.setOnMouseDragged(event -> {
            cursorX = event.getSceneX();
            cursorY = event.getSceneY();
            if(isDragging){
                dragObject.xPos = cursorX + dragX;
                dragObject.yPos = cursorY + dragY;
            }else
            if(isEditing){
                currentSelect.moveEnd(cursorX,cursorY);

            }
            try {
                paintLevelMaker();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });
        scene.setOnMouseReleased(event -> {
            cursorX = event.getSceneX();
            cursorY = event.getSceneY();
            isWidthDownPressed = false;
            isWidthUpPressed = false;
            isHeightDownPressed = false;
            isHeightUpPressed = false;
            timer = 0;
            if(isDragging){
                dragObject.xPos = cursorX + dragX;
                dragObject.yPos = cursorY + dragY;
                if(dragObject.xPos + dragObject.hitboxWidth >= WIDTH-TB_WIDTH){
                    if(dragObject.ENTITY_ID == 1) hero = null;
                    root.getChildren().remove(dragObject.getTexture());
                    Board.creatures.remove(dragObject);
                }
                isDragging = false;
                dragObject = null;
            } else
            if(isEditing){
                currentSelect.fillRect();
                isEditing = false;
            }
            checkEntityTerrainCollision();
            try {
                paintLevelMaker();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        scene.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.A){
                isAPressed = true;
            }
            if(event.getCode() == KeyCode.D){
                isDPressed = true;
            }
            if(event.getCode() == KeyCode.W){
                isWPressed = true;
            }
            if(event.getCode() == KeyCode.S){
                isSPressed = true;
            }
            if(event.getCode() == KeyCode.ENTER){
                save(locX,locY);
            }
            if(event.getCode() == KeyCode.ESCAPE){
                try {
                    pauseScreen();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (event.getCode() == KeyCode.Z && event.isShortcutDown()) {
                undo();
            }
            if (event.getCode() == KeyCode.Y && event.isShortcutDown()) {
                redo();
            }
        });
        scene.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.A){
                isAPressed = false;
            }
            if(event.getCode() == KeyCode.D){
                isDPressed = false;
            }
            if(event.getCode() == KeyCode.W){
                isWPressed = false;
            }
            if(event.getCode() == KeyCode.S){
                isSPressed = false;
            }
            if(event.getCode() == KeyCode.DIGIT0){
                currentBlock = 0;
                try {
                    paintToolbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getCode() == KeyCode.DIGIT1){
                currentBlock = 1;
                try {
                    paintToolbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getCode() == KeyCode.DIGIT2){
                currentBlock = 2;
                try {
                    paintToolbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getCode() == KeyCode.DIGIT3){
                currentBlock = 3;
                try {
                    paintToolbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getCode() == KeyCode.DIGIT4){
                currentBlock = 4;
                try {
                    paintToolbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getCode() == KeyCode.DIGIT5){
                currentBlock = 5;
                try {
                    paintToolbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(event.getCode() == KeyCode.DIGIT6){
                currentBlock = 6;
                try {
                    paintToolbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void runMapMaker() throws IOException {
        configureKeyBinds();

        timeline.getKeyFrames().clear();
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(time), e -> {
            boolean doPaint = false;
            if(isAPressed){
                double delta = locX;
                locX = Math.max(0,locX-cameraSpeed);
                delta -= locX;
                for(Creature c : creatures){
                    c.xPos += delta;
                }
                if(isDragging) {
                    dragObject.xPos = cursorX + dragX;
                    dragObject.yPos = cursorY + dragY;
                }
                doPaint = true;
            }
            if(isDPressed){
                double delta = locX;
                locX = Math.max(0,Math.min(pixelLength-WIDTH+TB_WIDTH-1,locX+cameraSpeed));
                delta -= locX;
                for(Creature c : creatures){
                    c.xPos += delta;
                }
                if(isDragging) {
                    dragObject.xPos = cursorX + dragX;
                    dragObject.yPos = cursorY + dragY;
                }
                doPaint = true;
            }
            if(isWPressed){
                double delta = locY;
                locY = Math.max(0,locY-cameraSpeed);
                delta -= locY;
                for(Creature c : creatures){
                    c.yPos += delta;
                }
                if(isDragging) {
                    dragObject.xPos = cursorX + dragX;
                    dragObject.yPos = cursorY + dragY;
                }
                    doPaint = true;
            }
            if(isSPressed){
                double delta = locY;
                locY = Math.max(Math.min(pixelHeight-HEIGHT-1,locY+cameraSpeed),0);
                delta -= locY;
                for(Creature c : creatures){
                    c.yPos += delta;
                }
                if(isDragging) {
                    dragObject.xPos = cursorX + dragX;
                    dragObject.yPos = cursorY + dragY;
                }
                doPaint = true;
            }
            if(isWidthUpPressed){
                if(timer > 1000) currentMapWidth = Math.min(currentMapWidth + 10,MAX_MAP_WIDTH);
                else currentMapWidth = Math.min(currentMapWidth + 1,MAX_MAP_WIDTH);
                resizeBoard(currentMapWidth,currentMapHeight);
                doPaint = true;
                timer += 20;
            }
            if(isWidthDownPressed){
                if(timer > 1000) currentMapWidth = Math.max(currentMapWidth - 10, MIN_MAP_WIDTH);
                else currentMapWidth = Math.max(currentMapWidth - 1, MIN_MAP_WIDTH);
                resizeBoard(currentMapWidth,currentMapHeight);
                doPaint = true;
                timer += 20;
            }
            if(isHeightUpPressed){
                if(timer > 1000) currentMapHeight = Math.min(currentMapHeight + 10,MAX_MAP_HEIGHT);
                else currentMapHeight = Math.min(currentMapHeight + 1,MAX_MAP_HEIGHT);
                resizeBoard(currentMapWidth,currentMapHeight);
                doPaint = true;
                timer += 20;
            }
            if(isHeightDownPressed){
                if(timer > 1000) currentMapHeight = Math.max(currentMapHeight - 10, MIN_MAP_HEIGHT);
                else currentMapHeight = Math.max(currentMapHeight - 1, MIN_MAP_HEIGHT);
                resizeBoard(currentMapWidth,currentMapHeight);
                doPaint = true;
                timer += 20;

            }
            if(doPaint){
                try {
                    paintLevelMaker();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }));
        timeline.play();
    }

    static ImageView blur;
    static ImageView paused_button_1;
    static ImageView paused_button_2;
    static ImageView paused_button_3;
    public static void pauseScreen() throws IOException {
        timeline.pause();

        blur = new ImageView(new Image(Files.newInputStream(Paths.get("src/textures/pause_screen_blur.jpg"))));
        blur.setOpacity(0.25);
        blur.setFitWidth(WIDTH);
        blur.setFitHeight(HEIGHT);
        root.getChildren().add(blur);

        Image b1 = new Image(Files.newInputStream(Paths.get("src/textures/quit_button.png")));
        double b1_width = b1.getWidth();
        double b1_height = b1.getHeight();
        double b1_xPos = WIDTH/2.0 - 1 - b1_width/2.0;
        double b1_yPos = HEIGHT/2.0 - 1 - 1.5*b1_height  - MARGIN;
        paused_button_1 = new ImageView();
        paused_button_1.setImage(b1);
        paused_button_1.setX(b1_xPos);
        paused_button_1.setY(b1_yPos);
        root.getChildren().add(paused_button_1);

        Image b2 = new Image(Files.newInputStream(Paths.get("src/textures/save_button.png")));
        double b2_width = b2.getWidth();
        double b2_height = b2.getHeight();
        double b2_xPos = WIDTH/2.0 - 1 - b2_width/2.0;
        double b2_yPos = HEIGHT/2.0 - 1  - b2_height/2.0;
        paused_button_2 = new ImageView();
        paused_button_2.setImage(b2);
        paused_button_2.setX(b2_xPos);
        paused_button_2.setY(b2_yPos);
        root.getChildren().add(paused_button_2);

        Image b3 = new Image(Files.newInputStream(Paths.get("src/textures/resume_button.png")));
        double b3_width = b3.getWidth();
        double b3_height = b3.getHeight();
        double b3_xPos = WIDTH/2.0 - 1 - b3_width/2.0;
        double b3_yPos = HEIGHT/2.0 - 1  + MARGIN + b3_height/2;
        paused_button_3 = new ImageView();
        paused_button_3.setImage(b3);
        paused_button_3.setX(b3_xPos);
        paused_button_3.setY(b3_yPos);
        root.getChildren().add(paused_button_3);
        scene.setOnMousePressed(e -> {

        });
        scene.setOnMouseDragged(e -> {

        });
        scene.setOnMouseReleased(e -> {

        });

        scene.setOnMouseClicked(mouseEvent -> {
            double x = mouseEvent.getSceneX();
            double y = mouseEvent.getSceneY();
            if(x >= b1_xPos && x < b1_xPos + b1_width && y >= b1_yPos && y < b1_yPos + b1_height) {
                locX = 0;
                locY = 0;
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
            if(x >= b2_xPos && x < b2_xPos + b2_width && y >= b2_yPos && y < b2_yPos + b2_height){
                save(locX,locY);
                locX = 0;
                locY = 0;
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
            if(x >= b3_xPos && x < b3_xPos + b3_width && y >= b3_yPos && y < b3_yPos + b3_height){
                root.getChildren().remove(blur);
                root.getChildren().remove(paused_button_1);
                root.getChildren().remove(paused_button_2);
                root.getChildren().remove(paused_button_3);
                blur = null;
                paused_button_1 = null;
                paused_button_2 = null;
                paused_button_3 = null;
                configureKeyBinds();
                timeline.play();
            }
        });
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE){
                root.getChildren().remove(blur);
                root.getChildren().remove(paused_button_1);
                root.getChildren().remove(paused_button_2);
                root.getChildren().remove(paused_button_3);
                blur = null;
                paused_button_1 = null;
                paused_button_2 = null;
                paused_button_3 = null;
                configureKeyBinds();
                timeline.play();
            }
        });
    }
}
