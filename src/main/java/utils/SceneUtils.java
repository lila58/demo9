package utils;

import controller.gameOverController;
import database.UserDAO;
import niveau1.GameController;
import niveau2.SnakeGameController;
import controller.niveauMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import niveau3.labyrintheController;

import java.io.IOException;

public class SceneUtils {

    private static final String GAME_FXML = "Game.fxml";

    // Retourne le chemin complet du FXML dans /niveau2/
    private static String resolvePath(String fxml) {
        return "/niveau2/" + fxml;
    }

    public static void changerScene(String fxml, Node source){
        try{
            // Correction : on utilise la même logique que changerSceneAvecUtilisateur
            FXMLLoader loader;
            if(fxml.startsWith("/")){
                loader = new FXMLLoader(SceneUtils.class.getResource(fxml));
            } else {
                loader = new FXMLLoader(SceneUtils.class.getResource(resolvePath(fxml)));
            }
            
            Parent root = loader.load();

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setWidth(800);
            stage.setHeight(800);
            stage.centerOnScreen();
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void changerSceneAvecUtilisateur(String fxml, Node source, int idUser, String prenom) {
        try{
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(resolvePath(fxml)));
            // ATTENTION : resolvePath ajoute "/controller/" par défaut.
            // Si vos FXML sont dans des dossiers différents, il faut revoir resolvePath ou passer le chemin complet.

            // Si 'fxml' contient déjà le chemin complet (ex: "/niveau1/game.fxml"), on ne doit pas utiliser resolvePath
            if(fxml.startsWith("/")){
                loader = new FXMLLoader(SceneUtils.class.getResource(fxml));
            } else {
                // Garde la compatibilité avec l'existant si vous passez juste "Game.fxml" pour le niveau 2
                loader = new FXMLLoader(SceneUtils.class.getResource(resolvePath(fxml)));
            }

            Parent root = loader.load();

            Object controller = loader.getController();

            // Injection pour le Niveau 2
            if(controller instanceof SnakeGameController gameController){
                gameController.setUtilisateur(idUser, prenom);
            }
            // Injection pour le Niveau 1
            else if(controller instanceof GameController gameController){
                gameController.setUtilisateur(idUser, prenom);
            }
            // --- AJOUT : Injection pour le Menu des Niveaux ---
            else if(controller instanceof niveauMenuController menuController){
                 try {
                     // Utilisation d'initData ou setUtilisateur selon ce qui existe dans votre menu
                     menuController.initData(idUser);
                 } catch (Exception e) {
                     System.err.println("Erreur lors de l'initialisation du menu : " + e.getMessage());
                     e.printStackTrace();
                 }
            }
                // Injection pour le Niveau 3
                else if(controller instanceof labyrintheController gameController){
                    gameController.setUtilisateur(idUser, prenom);

                    // Configuration des touches pour le labyrinthe
                    // On doit le faire APRÈS avoir attaché la racine à la scène
                    Scene scene = new Scene(root);
                    gameController.setupKeyControls(scene);

                    // On assigne la scène ici pour éviter de la recréer en bas
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setWidth(800);
                    stage.setHeight(800);
                    stage.centerOnScreen();
                    stage.show();
                    return; // On sort car on a déjà fait le setScene/show
                }

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setWidth(800);
            stage.setHeight(800);
            stage.centerOnScreen();
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static void changerSceneAvecScore(String fxml, Node source, int dernierScore, int idUser, int niveau, String prenom){
        try{
            // On charge toujours le modal générique, peu importe le paramètre 'fxml'
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource("/interface/modalGameOver.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();

            if(controller instanceof gameOverController gameOverController){
                gameOverController.setScore(dernierScore);
                // Injection des infos utilisateur dans le modal
                gameOverController.setUtilisateur(idUser, prenom);

                // Sélection du niveau à relancer en fonction de l'ID du niveau
                String niveauARelancer;
                switch (niveau) {
                    case 1:
                        niveauARelancer = "/niveau1/game.fxml";
                        break;
                    case 2:
                        niveauARelancer = "/niveau2/Game.fxml";
                        break;
                    case 3:
                        niveauARelancer = "/niveau3/labyrinthe.fxml";
                        break;
                    default:
                        System.err.println("Niveau inconnu : " + niveau);
                        niveauARelancer = "/niveau2/Game.fxml"; // Valeur par défaut
                        break;
                }

                gameOverController.setNiveauARelancer(niveauARelancer);

                Stage currentGameStage = (Stage) source.getScene().getWindow();
                gameOverController.setMainGameStage(currentGameStage);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            Stage currentGameStage = (Stage) source.getScene().getWindow();
            stage.initOwner(currentGameStage);
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void lancerNouvellePartie(Node source, int idUser, String prenom){
        changerSceneAvecUtilisateur(GAME_FXML, source, idUser, prenom);
    }
}

