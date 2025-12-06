package controller;
import database.requestDB;
import controller.niveauMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import niveau1.GameController;
import niveau2.SnakeGameController;
import niveau3.labyrintheController;
import utils.SceneManager;

import java.io.IOException;
import java.sql.SQLException;

public class gameOverController {

    @FXML
    private Button rejouer;

    @FXML
    private Button menu;

    @FXML
    private Label scoreLabel;


    private String niveauARelancer;


    private Stage mainGameStage;

    private int idUser;
    private String prenom;
    @FXML
    public void initialize() {

        rejouer.setOnAction(event -> {
            try {

                if (niveauARelancer == null || niveauARelancer.isEmpty()) {
                    System.out.println("Erreur : Aucun niveau spécifié à relancer !");
                    return;
                }


                Stage currentPopupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentPopupStage.close();


                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(niveauARelancer));
                Scene scene = new Scene(fxmlLoader.load(), 800, 800);


                Object controller = fxmlLoader.getController();
                // Réinjection des données utilisateur dans le contrôleur du jeu
                if (controller instanceof labyrintheController) {
                    ((labyrintheController) controller).setUtilisateur(idUser, prenom);
                    ((labyrintheController) controller).setupKeyControls(scene);
                } else if (controller instanceof SnakeGameController) {
                    ((SnakeGameController) controller).setUtilisateur(idUser, prenom);
                } else if (controller instanceof GameController) {
                    ((GameController) controller).setUtilisateur(idUser, prenom);
                }


                if (mainGameStage != null) {
                    mainGameStage.setScene(scene);
                    mainGameStage.show();
                } else {

                    Stage gameStage = new Stage();
                    gameStage.setTitle("Jeu");
                    gameStage.setScene(scene);
                    gameStage.show();
                }

                scene.getRoot().requestFocus();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        menu.setOnAction(event -> {

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();


            if (mainGameStage != null) {
                FXMLLoader loader = SceneManager.changerScene(
                        mainGameStage,
                        "/interface/menuNiveau.fxml",
                        "Snake Game - Menu"
                );


                if (loader != null) {
                    niveauMenuController controller = loader.getController();

                    try {
                        controller.initData(this.idUser);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void setScore(int score) {
        scoreLabel.setText("Score : " + score);

    }
    public void setUtilisateur(int idUser, String prenom) {
        this.idUser = idUser;
        this.prenom = prenom;
    }
    public void setNiveauARelancer(String fxmlFile) {
        this.niveauARelancer = fxmlFile;
    }

    public void setMainGameStage(Stage stage) {
        this.mainGameStage = stage;
    }
}