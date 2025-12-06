package controller;

import database.InfosJoueurDB;
import database.joueurs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import model.joueur;
import model.score;

import utils.SceneUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class InterfaceJoueurController {
    public InterfaceJoueurController() {
    }

    public static class LigneScore {
        public String pseudo;
        public int score;
        public LigneScore(String pseudo, int meilleur) {
            this.pseudo = pseudo;
            this.score = meilleur;
        }

        public String getPseudo() { return pseudo; }
        public int getScore() { return score; }
    }


    //d = dernier score
    //m = meilleur score
    @FXML
    public Button menu;
    @FXML
    public Label userName;
    @FXML
    public Pane pane1;
    @FXML
    public Label dscore1;
    @FXML
    public Label mscore1;
    @FXML
    public Pane pane2;
    @FXML
    public Label dscore2;
    @FXML
    public Label mscore2;
    @FXML
    public Pane pane3;
    @FXML
    public Label dscore3;
    @FXML
    public Label mscore3;
    @FXML
    public TableView<LigneScore> classementn1;
    @FXML
    public TableColumn<LigneScore, String> j1;
    @FXML
    public TableColumn<LigneScore, Integer> s1;
    @FXML
    public TableView<LigneScore> classementn2;
    @FXML
    public TableColumn<LigneScore, Integer> s2;
    @FXML
    public TableColumn<LigneScore, String> j2;
    @FXML
    public TableView<LigneScore> classementn3;
    @FXML
    public TableColumn<LigneScore, String> j3;
    @FXML
    public TableColumn<LigneScore, Integer> s3;
    @FXML
    public Button deconnexion;



    public void deconnexion(ActionEvent actionEvent) {
    }
    private joueur monJoueur;
    private int userId;

    public void initData(int userId) throws SQLException {
        this.userId = userId;
        InfosJoueurDB db = new InfosJoueurDB(userId);
        monJoueur = db.getJoueur();

        if (monJoueur != null) {
            userName.setText(monJoueur.getPseudo());
            afficherScoresPerso();
            afficherClassement(1);
            afficherClassement(2);
            afficherClassement(3);
        }
    }
    public void retourMenu(ActionEvent actionEvent) {

        SceneUtils.changerSceneAvecUtilisateur(
                "/interface/menuNiveau.fxml",
                (Node) actionEvent.getSource(),
                userId,
                monJoueur.getPseudo()
        );



    }
//


    public void initialize() {
        j1.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        s1.setCellValueFactory(new PropertyValueFactory<>("score"));
        j2.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        s2.setCellValueFactory(new PropertyValueFactory<>("score"));
        j3.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        s3.setCellValueFactory(new PropertyValueFactory<>("score"));


    }

    private void afficherScoresPerso() {
        ArrayList<score> scores = monJoueur.getScores();
        for(score s : scores) {
            int niveau = s.getNiveau();
            int dernier = s.getDernier_score();
            int meilleur = s.getMeilleur_score();
            if(niveau == 1) {
                dscore1.setText(Integer.toString(dernier));
                mscore1.setText(Integer.toString(meilleur));
            } else if(niveau == 2) {
                dscore2.setText(Integer.toString(dernier));
                mscore2.setText(Integer.toString(meilleur));
            } else if(niveau == 3) {
                dscore3.setText(Integer.toString(dernier));
                mscore3.setText(Integer.toString(meilleur));
            }
        }
    }
    private void afficherClassement(int niveau) throws SQLException {
        joueurs allPlayers = new joueurs();
        ArrayList<joueur> listeJoueurs = allPlayers.getJoueurs(niveau);

        ObservableList<LigneScore> data = FXCollections.observableArrayList();

        for (joueur j : listeJoueurs) {
            int meilleur = j.getScores().stream()
                    .filter(s -> s.getNiveau() == niveau)
                    .findFirst()
                    .map(score::getMeilleur_score)
                    .orElse(0);

            data.add(new LigneScore(j.getPseudo(), meilleur));
        }


        data.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        switch (niveau) {
            case 1 -> classementn1.setItems(data);
            case 2 -> classementn2.setItems(data);
            case 3 -> classementn3.setItems(data);
        }
    }

}