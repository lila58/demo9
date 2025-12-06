package model;

public class score {
    int id;
    int niveau;
    int id_joueur;
    int dernier_score;
    int meilleur_score;

    public score(int id_joueur, int niveau, int dernier_score, int meilleur_score) {
        this.id_joueur = id_joueur;
        this.niveau = niveau;
        this.dernier_score = dernier_score;
        this.meilleur_score = meilleur_score;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    public int getId_joueur() {
        return id_joueur;
    }

    public void setId_joueur(int id_joueur) {
        this.id_joueur = id_joueur;
    }

    public int getDernier_score() {
        return dernier_score;
    }

    public void setDernier_score(int dernier_score) {
        this.dernier_score = dernier_score;
    }

    public int getMeilleur_score() {
        return meilleur_score;
    }

    public void setMeilleur_score(int meilleur_score) {
        this.meilleur_score = meilleur_score;
    }
}
