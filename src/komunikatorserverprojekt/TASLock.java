/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikatorserverprojekt;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Projekt PW - Temat 2: Komunikator sieciowy
 * Wojciech Bałchanowski i Kacper Dutkiewicz
 * IJO1
 * Poniedziałek 9:45
 */
public class TASLock {
    public TASLock(){
        atomicBoolean = new AtomicBoolean(false);
    }
    AtomicBoolean atomicBoolean;
    public void lock(){
        while(atomicBoolean.getAndSet(true)){
            
        }
    }
    public void unlock(){
        atomicBoolean.set(false);
    }
    
}
