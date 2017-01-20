/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikatorserverprojekt;

/**
 * Projekt PW - Temat 2: Komunikator sieciowy
 * Wojciech Bałchanowski i Kacper Dutkiewicz
 * IJO1
 * Poniedziałek 9:45
 */
public class Rating {

    public Rating() {
        negativeRating = 0;
        positiveRating = 0;
    }
    private int positiveRating;
    private int negativeRating;

    public void addPositive() {
        positiveRating++;
    }

    public void addNegative() {
        negativeRating++;
    }

    public int getPositiveRating() {
        return positiveRating;
    }

    public void setPositiveRating(int positiveRating) {
        this.positiveRating = positiveRating;
    }

    public int getNegativeRating() {
        return negativeRating;
    }

    public void setNegativeRating(int negativeRating) {
        this.negativeRating = negativeRating;
    }

    
}
