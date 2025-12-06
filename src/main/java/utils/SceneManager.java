package utils;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class SceneManager {

    // 1. Changer la scène sur la fenêtre ACTUELLE (ex: Menu -> Jeu)
    public static FXMLLoader changerScene(Event event, String fxmlPath, String titre) {
        return changerScene(
                (Stage) ((Node) event.getSource()).getScene().getWindow(),
                fxmlPath,
                titre
        );
    }

    // Surcharge pour changer la scène sur un Stage spécifique
    public static FXMLLoader changerScene(Stage stage, String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();

            return loader;
        } catch (IOException e) {
            System.err.println("ERREUR SceneManager: Impossible de charger la vue " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    // 2. Ouvrir une NOUVELLE fenêtre (ex: Popup indépendant)
    // eventToClose: événement qui a déclenché l'action (permet de fermer l'ancienne fenêtre si besoin)
    public static FXMLLoader ouvrirNouvelleFenetre(String fxmlPath, String titre, Event eventToClose) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();

            // Si on veut fermer l'ancienne fenêtre
            if (eventToClose != null) {
                ((Node) eventToClose.getSource()).getScene().getWindow().hide();
            }

            return loader;
        } catch (IOException e) {
            System.err.println("ERREUR SceneManager: Impossible d'ouvrir la nouvelle fenêtre " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    // 3. Ouvrir une fenêtre MODALE (bloquante, ex: Game Over)
    public static FXMLLoader ouvrirModal(String fxmlPath, String titre, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));

            if (owner != null) {
                stage.initOwner(owner);
                stage.initModality(Modality.WINDOW_MODAL);
            }

            stage.show();
            return loader;
        } catch (IOException e) {
            System.err.println("ERREUR SceneManager: Impossible d'ouvrir le modal " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
    
    // 4. Charger une scène sur un stage existant (pour le démarrage de l'application)
    public static FXMLLoader chargerSceneSurStage(Stage stage, String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            stage.setTitle(titre);
            stage.setScene(new Scene(root, 800, 800)); // Taille par défaut
            stage.show();

            return loader;
        } catch (IOException e) {
             System.err.println("ERREUR SceneManager: Impossible de charger la scène de démarrage " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
}