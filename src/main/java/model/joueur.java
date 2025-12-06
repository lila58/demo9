package model;

import java.util.ArrayList;

public class joueur {
    int id;
     String pseudo;
    ArrayList<score> scores;

    public joueur(int id, String pseudo, ArrayList<score> scores) {
        this.id = id;
        this.pseudo = pseudo;
        this.scores = scores;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public ArrayList<score> getScores() {
        return scores;
    }

    public void setScores(ArrayList<score> scores) {
        this.scores = scores;
    }
}
