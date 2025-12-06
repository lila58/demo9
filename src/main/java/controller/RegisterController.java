package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import database.UserDAO;
import utils.SceneUtils;

public class RegisterController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void inscrire(){

        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String mail = emailField.getText();
        String mdp = passwordField.getText();

        if(nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || mdp.isEmpty()){
            messageLabel.setText("Tous les champs sont obligatoires");
            return;
        }

        if(userDAO.mailExiste(mail)){
            messageLabel.setText("Email déjà utilisé");
            return;
        }

        int idUser = userDAO.insererUtilisateur(nom, prenom, mail, mdp);

        if(idUser != -1){
            System.out.println("Redirection vers le jeu...");
            SceneUtils.changerSceneAvecUtilisateur("/interface/menuNiveau.fxml", emailField, idUser, prenom);
        }else{
            messageLabel.setText("Erreur inscription");
        }
    }

    @FXML
    private void allerConnexion() {
        SceneUtils.changerScene("/interface/Login.fxml", emailField);
    }
}

