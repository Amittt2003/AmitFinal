package com.example.amitfinal.Models;

import java.util.ArrayList;

public class UserScores {

    //הגדרה של ArrayList מסוג UserScore ששומר בתוכו אובייקטים מסוג (UserScore(String name, int scoreClassic, int scoreChaos

    //ה ArrayList - bestScoreClassicArray שומר בתוכו את הניקוד של השחקנים במוד Classic
    private final ArrayList<UserScore> bestScoreClassicArray = new ArrayList<>();

    //ה ArrayList - bestScoreChaosArray שומר בתוכו את הניקוד של השחקנים במוד Chaos
    private final ArrayList<UserScore> bestScoreChaosArray = new ArrayList<>();


    public UserScores(){}


    public String getNameByIndexClassic(int index){
        return this.bestScoreClassicArray.get(index).getName();
    }

    public String getNameByIndexChaos(int index){
        return this.bestScoreChaosArray.get(index).getName();
    }


    public int getScoreByIndexClassic(int index){
        return this.bestScoreClassicArray.get(index).getScoreClassic();
    }

    public int getScoreByIndexChaos(int index){
        return this.bestScoreChaosArray.get(index).getScoreChaos();
    }

    public int getLevelByIndexClassic(int index){
        return this.bestScoreClassicArray.get(index).getLevelClassic();
    }

    public int getLevelByIndexChaos(int index){
        return this.bestScoreChaosArray.get(index).getLevelChaos();
    }



    public String getTopNameClassic(){
        return this.bestScoreClassicArray.get(0).getName();
    }

    public String getTopNameChaos(){
        return this.bestScoreChaosArray.get(0).getName();
    }


    public int getTopScoreClassic(){
        return this.bestScoreClassicArray.get(0).getScoreClassic();
    }

    public int getTopScoreChaos(){
        return this.bestScoreChaosArray.get(0).getScoreChaos();
    }

    public int getTopLevelClassic(){
        return this.bestScoreClassicArray.get(0).getLevelClassic();
    }


    public int getTopLevelChaos(){
        return this.bestScoreChaosArray.get(0).getLevelChaos();
    }



    public int getSizeClassicArray(){
        return this.bestScoreClassicArray.size();
    }

    public int getSizeChaosArray(){
        return this.bestScoreChaosArray.size();
    }



    public void add(UserScore userScore){
        this.bestScoreClassicArray.add(userScore);
        this.bestScoreChaosArray.add(userScore);
    }

    public void clear(){
        this.bestScoreClassicArray.clear();
        this.bestScoreChaosArray.clear();
    }

    public void sort(){
        //ממיין את המערך bestScoreClassicArray מהערך (score) הגדול לקטן
        //כלומר, מהשיא הגבוה ביותר לקטן ביותר
        for (int i = 0; i <  this.bestScoreClassicArray.size() - 1; i++) {
            for (int j = i + 1; j < this.bestScoreClassicArray.size(); j++) {
                if (this.bestScoreClassicArray.get(i).getScoreClassic() < this.bestScoreClassicArray.get(j).getScoreClassic()) {
                    UserScore x = this.bestScoreClassicArray.get(i);
                    this.bestScoreClassicArray.set(i, this.bestScoreClassicArray.get(j));
                    this.bestScoreClassicArray.set(j, x);
                }
            }
        }

        //ממיין את המערך bestScoreChaosArray מהערך (score) הגדול לקטן
        //כלומר, מהשיא הגבוה ביותר לקטן ביותר
        for (int i = 0; i <  this.bestScoreChaosArray.size() - 1; i++) {
            for (int j = i + 1; j < this.bestScoreChaosArray.size(); j++) {
                if (this.bestScoreChaosArray.get(i).getScoreChaos() < this.bestScoreChaosArray.get(j).getScoreChaos()) {
                    UserScore x = this.bestScoreChaosArray.get(i);
                    this.bestScoreChaosArray.set(i, this.bestScoreChaosArray.get(j));
                    this.bestScoreChaosArray.set(j, x);
                }
            }
        }
    }
}
