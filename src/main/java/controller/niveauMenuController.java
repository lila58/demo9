package controller;

import database.InfosJoueurDB;
import database.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.joueur;
import model.score;
import niveau3.labyrintheController;
import utils.SceneManager;
import utils.SceneUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class niveauMenuController {


    @FXML
    public Label userName = new Label();
    @FXML
    public Button compte;

    @FXML
    private Button niveau1;

    @FXML
    private Button niveau2;

    @FXML
    private Button niveau3;

    @FXML
    private Button btnRetour;

    private int currentUserId;

    private String prenom;
    private joueur monJoueur;
    public void initData(int userId) throws SQLException {
        this.currentUserId = userId;


        InfosJoueurDB joueurDB = new InfosJoueurDB(currentUserId);
        monJoueur = joueurDB.getJoueur();
        this.prenom = monJoueur.getPseudo();
        if(monJoueur != null) {
            userName.setText(monJoueur.getPseudo());
            verifierDeblocage(monJoueur);
        } else {
            System.err.println("Erreur : joueur non trouvé pour l'ID " + userId);
        }
    }

        public void verifierDeblocage(joueur monjoueur){
            int deblocN2=50;

            int deblocN3=40;

            ArrayList<score> scores = monjoueur.getScores();
            for (score s : scores) {
                if (s.getNiveau() == 1) {
                    niveau2.setDisable(s.getMeilleur_score() < deblocN2);
                } else if (s.getNiveau() == 2) {
                    niveau3.setDisable(s.getMeilleur_score() < deblocN3);
                }
            }

        }



    @FXML
    public void initialize() {
        //explication
        compte.setOnAction(event -> {
            try {
                ouvrirInterfaceJoueur(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        niveau3.setOnAction(this::lancerNiveau3);
        niveau2.setOnAction(this::lancerNiveau2);
        niveau1.setOnAction(this::lancerNiveau1);
    }

    @FXML
    private void ouvrirInterfaceJoueur(ActionEvent event) throws SQLException {
        System.out.println("Ouverture interface joueur pour ID: " + currentUserId);
        if(currentUserId == 0) {
            System.err.println("ERREUR: currentUserId non initialisé!");
            return;
        }
        FXMLLoader loader = SceneManager.ouvrirNouvelleFenetre(
                "/interface/InterfaceJoueur.fxml",
                "Interface Joueur - " + monJoueur.getPseudo(),
                event
        );
        if (loader != null) {
            InterfaceJoueurController controller = loader.getController();
            try {
                controller.initData(currentUserId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @FXML
    void lancerNiveau3(ActionEvent event) {

        SceneUtils.changerSceneAvecUtilisateur(
                "/niveau3/labyrinthe.fxml",
                (Node) event.getSource(),
                currentUserId,
                prenom
        );
    }

    @FXML
    void lancerNiveau2(ActionEvent event) {
        SceneUtils.changerSceneAvecUtilisateur(
                "/niveau2/Game.fxml",
                (Node) event.getSource(),
                currentUserId,
                prenom
        );
    }

    @FXML
    void lancerNiveau1(ActionEvent event) {
        SceneUtils.changerSceneAvecUtilisateur(
                "/niveau1/game.fxml",
                (Node) event.getSource(),
                currentUserId,
                prenom
        );
    }



}
