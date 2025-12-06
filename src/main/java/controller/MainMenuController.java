package controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import utils.SceneUtils;

public class MainMenuController {

    @FXML
    private VBox racine;

    @FXML
    private void allerInscription() {
        SceneUtils.changerScene("/interface/Register.fxml", racine);
    }

    @FXML
    private void goToLogin() {
        SceneUtils.changerScene("/interface/Login.fxml", racine);
    }

}
