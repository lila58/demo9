package niveau3;


import database.requestDB;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import database.UserDAO;
import utils.SceneUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class labyrintheController {
    private int idUser;
    private String prenom;

    // Méthode pour recevoir les infos du joueur
    public void setUtilisateur(int idUser, String prenom) {
        this.idUser = idUser;
        this.prenom = prenom;
        System.out.println("Niveau 3 - Joueur : " + prenom + " (ID: " + idUser + ")");
    }
    @FXML
    public AnchorPane rootPane;
    @FXML
    public Button pause = new Button("Pause");
    @FXML
    public Label scoreLabel;
    public int score=0 ;
    private final int mur = 1;
    private static final int MAX_SNAKE_LENGTH = 50;  // Serpent max 50 segments
    private static final double INITIAL_SPEED = 200;
    private static final double FINAL_SPEED = 120;
    private boolean maxLengthReached = false;

    private final int tileSize = 25;
    private static class tile{
        int x;
        int y;

        public  tile(int x,int y){
            this.x=x;
            this.y=y;
        }
    }


    private static class Rectangle {
        int x, y, width, height;
        public Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    // Liste de zones sûres prédéfinies
    private final Rectangle[] safeZones = {
            new Rectangle(5, 5, 14, 3),    // Bande horizontale haute
            new Rectangle(10, 8, 4, 8),    // Centre (le plus sûr)
            new Rectangle(15, 5, 3, 14),   // Bande horizontale basse
    };



    private final ArrayList<tile> snake;
    Random random;
    tile head;
    private int velocityX;
    private int velocityY;
    private final tile food;
    private final double  gameSpeed = 300;
    
    // Flag pour empêcher le retournement suicide (double changement de direction en une frame)
    private boolean directionChanged = false;
    private Timeline timeline;

    private final int[][] maze = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, // 0
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 1 Large couloir haut
            {1,0,1,1,1,0,1,1,0,1,1,1,1,1,1,0,1,1,0,1,1,1,0,1}, // 2  Obstacles espacés
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 3 Couloir circulation
            {1,0,1,0,1,1,0,1,1,1,0,0,0,0,1,1,1,0,1,1,0,1,0,1}, // 4
            {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1}, // 5
            {1,0,1,1,0,1,1,1,0,1,1,0,0,1,1,0,1,1,1,0,1,1,0,1}, // 6
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 7 Couloir large
            {1,1,1,0,1,1,0,1,0,1,0,0,0,0,1,0,1,0,1,1,0,1,1,1}, // 8
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 9
            {1,0,1,1,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,1,0,1}, // 10
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 11 CENTRE - Zone refuge
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 12 CENTRE - Zone refuge
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 13 CENTRE - Zone refuge
            {1,0,1,1,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,1,0,1}, // 14
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 15
            {1,1,1,0,1,1,0,1,0,1,0,0,0,0,1,0,1,0,1,1,0,1,1,1}, // 16
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 17 Couloir large
            {1,0,1,1,0,1,1,1,0,1,1,0,0,1,1,0,1,1,1,0,1,1,0,1}, // 18
            {1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1}, // 19
            {1,0,1,0,1,1,0,1,1,1,0,0,0,0,1,1,1,0,1,1,0,1,0,1}, // 20
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 21 Couloir circulation
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // 22 Obstacles espacés
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}  // 23
    };

    @FXML
    public Canvas labyrinthe;

    private GraphicsContext gc;

    public labyrintheController(){
        int width = 600;
        int height = 600;
        head = new tile(12, 12);
        snake = new ArrayList<>();
        food = new tile(10, 10);
        random = new Random();
        callFood();
    }

    public void draw(){

        gc.clearRect(0, 0, labyrinthe.getWidth(), labyrinthe.getHeight());


        for (int i = 0; i < maze.length; i++) {           // i = ligne (Y)
            for(int j = 0; j < maze[i].length; j++) {     // j = colonne (X)


                if (maze[i][j] == mur) {
                    gc.setFill(Color.DARKGRAY);
                    gc.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);


                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(1);
                    gc.strokeRect(j * tileSize, i * tileSize, tileSize, tileSize);
                }
            }
        }

        gc.setFill(Color.RED);
        gc.fillOval(food.x* tileSize, food.y * tileSize, tileSize, tileSize);
        //tete
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(head.x * tileSize, head.y * tileSize, tileSize, tileSize);

        //YEUX
        gc.setFill(Color.WHITE);
        gc.fillOval(head.x * tileSize + 5, head.y * tileSize + 5, 4, 4);
        gc.fillOval(head.x * tileSize + 15, head.y * tileSize + 5, 4, 4);


        gc.setFill(Color.LIMEGREEN);
        for (tile snakePart : snake) {
            gc.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

    }

    public void initialize(){
        gc = labyrinthe.getGraphicsContext2D();


        labyrinthe.setFocusTraversable(true);
        labyrinthe.requestFocus();


        // CORRECTION : Attacher les contrôles quand la scène est prête
        rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                setupKeyControls(newScene);
            }
        });

        draw();
        timeline = new Timeline(new KeyFrame(Duration.millis(gameSpeed), e -> {
            try {
                run();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        pause.setOnAction(e -> {
            if(timeline.getStatus()==Animation.Status.RUNNING){
                timeline.pause();
            }else{
                timeline.play();
            }
        });
    }
    private void run() throws SQLException {
        updateMove();
        directionChanged = false; // Réinitialiser le flag après le mouvement
        draw();

    }

    public boolean isWall(int x, int y) {
        if (x < 0 || x >= maze[0].length || y < 0 || y >= maze.length) {
            return true; // Hors limites = considéré comme mur
        }
        return maze[y][x] == mur;
    }

    public boolean isEmpty(int x, int y) {
        if (x < 0 || x >= maze[0].length || y < 0 || y >= maze.length) {
            return false;
        }
        int vide = 0;
        return maze[y][x] == vide;
    }


    private boolean isOccupiedBySnake(tile t) {

        if (head.x == t.x && head.y == t.y) return true;

        for (tile part : snake) {
            if (part.x == t.x && part.y == t.y) return true;
        }
        return false;
    }


    public void callFood(){
        tile newFood;

        
        do {

            newFood = new tile(
                random.nextInt(maze[0].length),  // Colonne aléatoire (0-23)
                random.nextInt(maze.length)      // Ligne aléatoire (0-23)
            );

            
        } while (!isEmpty(newFood.x, newFood.y) || isOccupiedBySnake(newFood));
        
        food.x = newFood.x;
        food.y = newFood.y;
    }

    private boolean collision(tile tile1, tile tile2){
        return  tile1.x == tile2.x && tile1.y==tile2.y;
    }
    private boolean collisionWall(){
        return isWall(head.x + velocityX, head.y + velocityY);
    }
    private boolean checkSelfCollision() {
        for (tile t : snake) {
            if (collision(head, t)) {
                return true;
            }
        }
        return false;
    }

    public void updateMove() throws SQLException {
        

        if(collision(head, food)){
            callFood();
            

            if (snake.size() < MAX_SNAKE_LENGTH) {
                snake.add(new tile(head.x, head.y));
                updateSpeed();
            } else {

                if (!maxLengthReached) {
                    maxLengthReached = true;
                    System.out.println("🎉 Longueur MAX atteinte ! Mode High Score activé !");
                    showMaxLengthMessage();
                }
            }
            
            score += 2;
            scoreLabel.setText("Score : " + score);
        }

        if (collisionWall()) {
            timeline.stop();
            gameOver();
            return;
        }

        for(int i = snake.size() - 1; i >= 0; i--){
            tile SnakePart = snake.get(i);
            
            if(i == 0){

                SnakePart.x = head.x;
                SnakePart.y = head.y;
            } else {

                tile prevSnakePart = snake.get(i - 1);
                SnakePart.x = prevSnakePart.x;
                SnakePart.y = prevSnakePart.y;
            }
        }
        head.x += velocityX;
        head.y += velocityY;

        if (checkSelfCollision()) {
            timeline.stop();
            gameOver();
            return;
        }
    }

    public void setupKeyControls(Scene scene){
        scene.setOnKeyPressed(event -> {
            handleKeyPress(event);
            event.consume();
        });

        labyrinthe.setFocusTraversable(true);
        labyrinthe.setOnMouseClicked(e -> labyrinthe.requestFocus());
    }
    private void handleKeyPress(KeyEvent event) {
        // Si une direction a déjà été prise dans ce tour, on ignore les autres touches
        if (directionChanged) return;

        KeyCode keyCode = event.getCode();
        if(keyCode == KeyCode.LEFT && velocityX == 0) { // Empêche le retour arrière
            velocityX = -1;
            velocityY = 0;
            directionChanged = true;
        } else if(keyCode == KeyCode.RIGHT && velocityX == 0) {
            velocityX = 1;
            velocityY = 0;
            directionChanged = true;
        } else if(keyCode == KeyCode.UP && velocityY == 0) {
            velocityX = 0;
            velocityY = -1;
            directionChanged = true;
        } else if(keyCode == KeyCode.DOWN && velocityY == 0) {
            velocityX = 0;
            velocityY = 1;
            directionChanged = true;
        }

    }

   /* private void gameOver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interface/modalGameOver.fxml"));
            Parent root = loader.load();

            gameOverController controller = loader.getController();


            controller.setScore(score);


            controller.setNiveauARelancer("labyrinthe.fxml");

            Stage currentGameStage = (Stage) rootPane.getScene().getWindow();
            controller.setMainGameStage(currentGameStage);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.initOwner(currentGameStage);
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
   private void gameOver() throws SQLException {
       requestDB db = new requestDB();
       db.saveScore(this.idUser, 3, score);

       SceneUtils.changerSceneAvecScore(
               null,
               rootPane,
               score,
               this.idUser, // Utilise l'ID reçu
               3,
               this.prenom // Utilise le prénom reçu
       );
   }

   private void updateSpeed() {
        int length = snake.size();

        double progression = (double) length / MAX_SNAKE_LENGTH;
        double newSpeed = INITIAL_SPEED - (INITIAL_SPEED - FINAL_SPEED) * progression;

        timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.millis(newSpeed), e -> {
            try {
                run();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void showMaxLengthMessage() {

        Label maxLabel = new Label("🎉 LONGUEUR MAX ! Mode High Score ! 🎉");
        maxLabel.setStyle(
                "-fx-font-size: 20px; " +
                        "-fx-text-fill: gold; " +
                        "-fx-background-color: rgba(0,0,0,0.7); " +
                        "-fx-padding: 10px; " +
                        "-fx-background-radius: 10px;"
        );


        maxLabel.setLayoutX(150);
        maxLabel.setLayoutY(280);

        rootPane.getChildren().add(maxLabel);

        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> rootPane.getChildren().remove(maxLabel))
        );
        fadeOut.play();
    }



}
