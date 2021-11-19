package com.example.amitfinal.Models;

public class UserScore {

    private final String name;
    private final int scoreClassic;
    private final int scoreChaos;
    private final int levelClassic;
    private final int levelChaos;

    public UserScore(String name, int scoreClassic, int scoreChaos , int levelClassic, int levelChaos){
        this.name = name;
        this.scoreClassic = scoreClassic;
        this.scoreChaos = scoreChaos;
        this.levelClassic = levelClassic;
        this.levelChaos = levelChaos;
    }

    public String getName() {
        return this.name;
    }

    public int getScoreClassic() {
        return this.scoreClassic;
    }

    public int getScoreChaos() {
        return this.scoreChaos;
    }

    public int getLevelClassic() {
        return this.levelClassic;
    }

    public int getLevelChaos() {
        return this.levelChaos;
    }
}
