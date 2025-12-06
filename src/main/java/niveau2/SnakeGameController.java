package niveau2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import database.UserDAO;
import utils.SceneUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGameController {

    @FXML private Canvas canvas;
    @FXML private Label scoreLabel;
    @FXML private Label prenomLabel;

    private final int TAILLE = 20;
    private final int LARGEUR = 30;
    private final int HAUTEUR = 30;

    private List<int[]> serpent = new ArrayList<>();
    private int[] pomme;

    //Direction par défaut
    private int dx = 1;
    private int dy = 0;

    private int score = 0;
    private Timeline timeline;

    private int idUser;
    private String prenom;

    //Niveau fixe
    private final int NIVEAU = 2;

    private final UserDAO userDAO = new UserDAO();

    //Obstacles
    private final List<int[]> obstacles = List.of(
            new int[]{10, 5}, new int[]{11, 5}, new int[]{12, 5},
            new int[]{10, 15}, new int[]{11, 15}, new int[]{12, 15}
    );

    //Reçu depuis SceneUtils
    public void setUtilisateur(int idUser, String prenom){
        this.idUser = idUser;
        this.prenom = prenom;

        //Le label peut être null au moment de l’injection
        Platform.runLater(() -> prenomLabel.setText(prenom));

        System.out.println("Joueur connecté : " + prenom);
    }

    @FXML
    public void initialize(){

        serpent.clear();
        serpent.add(new int[]{2, 10}); // position de départ sûre

        genererPomme();
        score = 0;
        scoreLabel.setText("0");

        //clavier
        Platform.runLater(() -> {
            canvas.setFocusTraversable(true);
            canvas.requestFocus();
        });

        canvas.setOnKeyPressed(this::gererTouches);

        timeline = new Timeline(new KeyFrame(Duration.millis(140), e -> miseAJour()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //Gestion des touches
    private void gererTouches(KeyEvent event){
        switch (event.getCode()){
            case UP ->{
                if(dy != 1){ dx = 0; dy = -1;}
            }
            case DOWN ->{
                if(dy != -1){ dx = 0; dy = 1;}
            }
            case LEFT ->{
                if(dx != 1){ dx = -1; dy = 0;}
            }
            case RIGHT ->{
                if(dx != -1){ dx = 1; dy = 0;}
            }
        }
    }

    private void miseAJour(){
        int[] tete = serpent.get(0);
        int nx = tete[0] + dx;
        int ny = tete[1] + dy;

        //Murs
        if(nx < 0 || ny < 0 || nx >= LARGEUR || ny >= HAUTEUR){
            finDePartie();
            return;
        }

        //Collision avec soi-même
        for(int[] p : serpent){
            if (p[0] == nx && p[1] == ny){
                finDePartie();
                return;
            }
        }

        //Collision obstacles
        for(int[] o : obstacles){
            if(o[0] == nx && o[1] == ny){
                finDePartie();
                return;
            }
        }

        serpent.add(0, new int[]{nx, ny});

        //Mange une pomme = +2 points
        if(nx == pomme[0] && ny == pomme[1]){
            score += 2;
            scoreLabel.setText(String.valueOf(score));
            genererPomme();
        }else{
            serpent.remove(serpent.size() - 1);
        }

        dessiner();
    }

    private void dessiner(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);   //Fond blanc
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //TÊTE DU SERPENT
        int[] tete = serpent.get(0);
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(tete[0] * TAILLE, tete[1] * TAILLE, TAILLE, TAILLE);

        //CORPS DU SERPENT
        gc.setFill(Color.LIMEGREEN);
        for(int i = 1; i < serpent.size(); i++){
            int[] p = serpent.get(i);
            gc.fillRect(p[0] * TAILLE, p[1] * TAILLE, TAILLE, TAILLE);
        }
        //YEUX
        gc.setFill(Color.WHITE);
        gc.fillOval(tete[0] * TAILLE + 4, tete[1] * TAILLE + 4, 4, 4);
        gc.fillOval(tete[0] * TAILLE + 12, tete[1] * TAILLE + 4, 4, 4);


        //Pomme
        gc.setFill(Color.RED);
        gc.fillOval(pomme[0] * TAILLE, pomme[1] * TAILLE, TAILLE, TAILLE);

        //Obstacles
        gc.setFill(Color.GRAY);
        for (int[] o : obstacles) {
            gc.fillRect(o[0] * TAILLE, o[1] * TAILLE, TAILLE, TAILLE);
        }
    }

    private void genererPomme(){
        Random r = new Random();
        pomme = new int[]{r.nextInt(LARGEUR), r.nextInt(HAUTEUR)};
    }

    //FIN DE PARTIE
    private void finDePartie(){
        timeline.stop();

        //Sauvegarde du score
        userDAO.sauvegarderScore(idUser, NIVEAU, score);

        //Affichage GameOver
        SceneUtils.changerSceneAvecScore(
                null,      // null car SceneUtils charge désormais modalGameOver.fxml en dur
                canvas,    // Node source pour récupérer la Window
                score,     // dernierScore
                idUser,
                NIVEAU,    // 2
                prenom     // nécessaire pour l'affichage
        );
    }
}
