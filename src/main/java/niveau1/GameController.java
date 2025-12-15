//tous ce qui est dessin du serpent mouvement les regles qui elimine le serpent pause et envoie du score final
package niveau1;

import controller.AdminController;
import database.UserDAO_Irfane;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.UserDAO;
import model.User;
import utils.SceneUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private int idUser;
    private String prenom;

    // Méthode pour recevoir les infos du joueur
    public void setUtilisateur(int idUser, String prenom) {
        this.idUser = idUser;
        this.prenom = prenom;
        System.out.println("Niveau 1 - Joueur : " + prenom + " (ID: " + idUser + ")");
        // Si les éléments FXML sont déjà injectés
        if (lblJoueur != null) {
            lblJoueur.setText("Joueur: " + prenom);
        }
    }

    @FXML private VBox rootBox;
    @FXML private Canvas gameCanvas;
    @FXML private Label lblScore;
    @FXML private Label lblJoueur;
    @FXML private Button btnPause; // Nouveau lien avec le bouton FXML

    private GraphicsContext gc;
    private User joueur;
    private AdminController adminController;
    private UserDAO_Irfane dao = new UserDAO_Irfane();

    // --- NOUVEAUX PARAMÈTRES (600x600) ---
    private static final int TILE = 25;
    private static final int WIDTH = 24;  // 600 px / 25 = 24 cases
    private static final int HEIGHT = 24; // 600 px / 25 = 24 cases

    // État jeu
    private List<Point> snake = new ArrayList<>();
    private Point food;
    private int direction = 1;
    private boolean running = false;
    private boolean isPaused = false; // État de pause
    private int score = 0;
    private AnimationTimer timer;

    public void initData(User joueur, AdminController adminController) {
        this.joueur = joueur;
        this.adminController = adminController;

        lblJoueur.setText("Joueur: " + joueur.getPrenom());
        gc = gameCanvas.getGraphicsContext2D();

        // --- CORRECTION CLAVIER ---
        // On attache l'écouteur de touches directement à la fenêtre (Scene)
        // dès qu'elle est disponible. C'est plus fiable.
        rootBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    // Touche Espace = Raccourci pour Pause
                    if (e.getCode() == KeyCode.SPACE) {
                        handlePause();
                    } else {
                        handleInput(e.getCode());
                    }
                });
                // On demande le focus immédiatement
                rootBox.requestFocus();
            }
        });

        // On rend la boite focusable
        rootBox.setFocusTraversable(true);

        startGame();
    }
   @FXML
   public void initialize() {
       gc = gameCanvas.getGraphicsContext2D();

       rootBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
           if (newScene != null) {
               newScene.setOnKeyPressed(e -> {
                   if (e.getCode() == KeyCode.SPACE) {
                       handlePause();
                   } else {
                       handleInput(e.getCode());
                   }
               });
               rootBox.requestFocus();
           }
       });

       rootBox.setFocusTraversable(true);

       startGame();
   }


    private void startGame() {
        snake.clear();
        // Départ centré
        snake.add(new Point(10, 10));
        spawnFood();
        score = 0;
        direction = 1;
        running = true;
        isPaused = false;

        // Réinitialiser le bouton Pause
        if(btnPause != null) btnPause.setText("PAUSE");

        updateScoreLabel();

        timer = new AnimationTimer() {
            long lastTick = 0;
            public void handle(long now) {
                // Si le jeu tourne ET n'est pas en pause
                if (running && !isPaused && now - lastTick > 150_000_000) {
                    lastTick = now;
                    tick();
                    draw();
                }
            }
        };
        timer.start();
    }

    // --- FONCTION PAUSE (Appelée par le bouton ou Espace) ---
    @FXML
    private void handlePause() {
        if (!running) return; // Si perdu, ne rien faire

        if (isPaused) {
            // Reprendre le jeu
            isPaused = false;
            btnPause.setText("PAUSE");
            btnPause.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            // Mettre en pause
            isPaused = true;
            btnPause.setText("REPRENDRE");
            btnPause.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

            // Dessiner "PAUSE" sur l'écran
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(40));
            gc.fillText("PAUSE", 240, 300);
        }

        //  Redonner le focus à la boite principale pour que le clavier remarche
        // Sinon le focus reste sur le bouton et les flèches ne marchent plus.
        rootBox.requestFocus();
    }

    private void handleInput(KeyCode code) {
        if (isPaused) return; // Bloquer les mouvements si pause

        if (code == KeyCode.UP && direction != 2) direction = 0;
        else if (code == KeyCode.RIGHT && direction != 3) direction = 1;
        else if (code == KeyCode.DOWN && direction != 0) direction = 2;
        else if (code == KeyCode.LEFT && direction != 1) direction = 3;
    }

    private void tick() {
        Point head = snake.get(0);
        int newX = head.x;
        int newY = head.y;

        if (direction == 0) newY--;
        if (direction == 1) newX++;
        if (direction == 2) newY++;
        if (direction == 3) newX--;

        // Collision Murs ou Soi-même
        if (newX < 0 || newX >= WIDTH || newY < 0 || newY >= HEIGHT || isSnakeOn(newX, newY)) {
            gameOver("GAME OVER !");
            return;
        }

        snake.add(0, new Point(newX, newY));

        if (newX == food.x && newY == food.y) {
            score += 2;
            updateScoreLabel();
            spawnFood();

            if (score == 50) {
                System.out.println("Niveau 1 complété !");
            }
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void gameOver(String raison) {
        running = false;
        timer.stop();
        dao.updateScore(this.idUser, score);

        if (adminController != null) adminController.refreshTable();

        SceneUtils.changerSceneAvecScore(
                null,
                lblScore, // ou un autre noeud de la scène
                score,
                this.idUser, // Utilise l'ID reçu
                1,
                this.prenom  // Utilise le prénom reçu
        );
    }

    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(WIDTH);
            y = rand.nextInt(HEIGHT);
        } while (isSnakeOn(x, y));
        food = new Point(x, y);
    }

    private boolean isSnakeOn(int x, int y) {
        for (Point p : snake) {
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }

    private void updateScoreLabel() {
        lblScore.setText("Score: " + score);
    }

    private void draw() {
        // Ne pas redessiner si en pause (pour garder le texte "PAUSE")
        if (isPaused) return;

        // Fond
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        // Nourriture
        gc.setFill(Color.RED);
        gc.fillOval(food.x * TILE, food.y * TILE, TILE, TILE);
        // Serpent
        if (!snake.isEmpty()) {
            // TÊTE DU SERPENT
            Point head = snake.get(0);
            gc.setFill(Color.DARKGREEN);
            gc.fillRect(head.x * TILE, head.y * TILE, TILE, TILE);

            // YEUX (adaptés pour TILE=25)
            gc.setFill(Color.WHITE);
            gc.fillOval(head.x * TILE + 5, head.y * TILE + 5, 4, 4);
            gc.fillOval(head.x * TILE + 15, head.y * TILE + 5, 4, 4);

            // CORPS DU SERPENT
            gc.setFill(Color.LIMEGREEN);
            for (int i = 1; i < snake.size(); i++) {
                Point p = snake.get(i);
                gc.fillRect(p.x * TILE, p.y * TILE, TILE, TILE);
            }
        }
    }
}
