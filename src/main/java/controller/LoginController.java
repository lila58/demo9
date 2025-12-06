package controller;

import database.UserDAO;
import utils.SceneUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController{

    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void seConnecter(){

        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();

        if (email.isEmpty() || motDePasse.isEmpty()){
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        int idUser = userDAO.verifierConnexion(email, motDePasse);

        if(idUser != -1){

            //Récupération du prénom pour l'afficher dans le jeu
            String prenom = userDAO.getPrenomById(idUser);

            //Redirection vers le jeu avec prénom
            SceneUtils.changerSceneAvecUtilisateur(
                    "/interface/menuNiveau.fxml",
                    emailField,
                    idUser,
                    prenom
            );

        }else{
            messageLabel.setText("Email ou mot de passe incorrect");
        }
    }

    @FXML
    private void allerInscription() {
        SceneUtils.changerScene("/interface/Register.fxml", emailField);
    }
}
