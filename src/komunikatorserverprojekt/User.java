/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikatorserverprojekt;

/**
 *
 * @author Bucior
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