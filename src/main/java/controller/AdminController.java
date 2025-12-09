//fichier pour remplir le tableau avec les donnees qui viennent de userDao irfanee
package controller;

import database.UserDAO;
import database.UserDAO_Irfane;
import model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import niveau1.GameController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPrenom;
    @FXML private TableColumn<User, String> colMail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Integer> colScore;
    @FXML private TableColumn<User, Integer> colDernierScore; // Nouvelle colonne

    @FXML private TextField txtNom, txtPrenom, txtMail, txtRole;
    @FXML private PasswordField txtMdp;

    private final UserDAO_Irfane dao = new UserDAO_Irfane();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes
        colNom.setCellValueFactory(cell -> cell.getValue().nomProperty());
        colPrenom.setCellValueFactory(cell -> cell.getValue().prenomProperty());
        colMail.setCellValueFactory(cell -> cell.getValue().mailProperty());
        colRole.setCellValueFactory(cell -> cell.getValue().roleProperty());
        colScore.setCellValueFactory(cell -> cell.getValue().meilleurScoreNiveau1Property().asObject());
        colDernierScore.setCellValueFactory(cell -> cell.getValue().dernierScoreNiveau1Property().asObject());

        // LISTENER : Quand on clique sur une ligne, on remplit les champs
        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                remplirChamps(newVal);
            }
        });

        refreshTable();
    }

    private void remplirChamps(User u) {
        txtNom.setText(u.getNom());
        txtPrenom.setText(u.getPrenom());
        txtMail.setText(u.getMail());
        txtRole.setText(u.getRole());
        txtMdp.setText(u.getMdp());
    }

    @FXML
    private void handleModifier() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Sélectionnez un joueur à modifier");
            return;
        }

        // On met à jour l'objet avec ce qui est écrit dans les champs
        selected.setNom(txtNom.getText());
        selected.setPrenom(txtPrenom.getText());
        selected.setMail(txtMail.getText());
        selected.setRole(txtRole.getText());
        selected.setMdp(txtMdp.getText());

        // On envoie la modif à la base de données
        dao.updateUser(selected);

        refreshTable();
        clearFields();
    }

    @FXML
    private void handleAjouter() {
        if(txtNom.getText().isEmpty()) return;
        User u = new User(txtNom.getText(), txtPrenom.getText(), txtMail.getText(), txtMdp.getText(), txtRole.getText());
        dao.insertUser(u);
        refreshTable();
        clearFields();
    }

    @FXML
    private void handleSupprimer() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.deleteUser(selected.getId());
            refreshTable();
            clearFields();
        }
    }

    @FXML
    private void handleLancerJeu() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Sélectionnez un joueur !");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/snake/game.fxml"));
            Parent root = loader.load();
            GameController gameController = loader.getController();
            gameController.initData(selected, this);

            Stage stage = new Stage();
            stage.setTitle("Snake - Niveau 1");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void refreshTable() {
        userList.setAll(dao.findAllUsersWithScore());
        tableUsers.setItems(userList);
    }

    private void clearFields() {
        txtNom.clear(); txtPrenom.clear(); txtMail.clear(); txtMdp.clear(); txtRole.clear();
        tableUsers.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}
