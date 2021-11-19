package com.example.amitfinal.Models;

// משמש להכנסת וקבלת נתונים מFirebase
public class User {

    private final String name;
    private final String email;
    private int bestScoreClassic;
    private int bestScoreChaos;
    private int bestLevelClassic;
    private int bestLevelChaos;

    public User(String name, String email, int bestScoreClassic, int bestScoreChaos, int bestLevelClassic, int bestLevelChaos){
        this.name = name;
        this.email = email;
        this.bestScoreClassic = bestScoreClassic;
        this.bestScoreChaos = bestScoreChaos;
        this.bestLevelClassic = bestLevelClassic;
        this.bestLevelChaos = bestLevelChaos;
    }

    public User(){
        this.name = "none";
        this.email = "none";
        this.bestScoreClassic = 0;
        this.bestScoreChaos = 0;
        this.bestLevelClassic = 1;
        this.bestLevelChaos = 1;
    }

    public void setBestScoreClassic(int score) {
        this.bestScoreClassic = score;
    }

    public void setBestScoreChaos(int score) {
        this.bestScoreChaos = score;
    }

    public void setBestLevelClassic(int bestLevelClassic) {
        this.bestLevelClassic = bestLevelClassic;
    }

    public void setBestLevelChaos(int bestLevelChaos) {
        this.bestLevelChaos = bestLevelChaos;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public int getBestScoreClassic() {
        return this.bestScoreClassic;
    }

    public int getBestScoreChaos() {
        return this.bestScoreChaos;
    }


    public int getBestLevelClassic() {
        return this.bestLevelClassic;
    }

    public int getBestLevelChaos() {
        return this.bestLevelChaos;
    }

}
