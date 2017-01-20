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
/**
 * Klasa do której będziemy zapisywać wyniki działania wątków
 * ma na celu wyświetlenie w Server kto wyszedł z czatu i ile na nim siedział
 */
public class User {
    
    private String name;
    private long time;

    public User(String name, long time) {
        this.name = name;
        this.time = time;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User " + name + " spedzil na czacie " + time + " sekund";
    }
}
