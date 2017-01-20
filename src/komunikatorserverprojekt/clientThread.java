/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikatorserverprojekt;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.*;
/**
 *
 * @author Bucior
 */
class clientThread implements Callable<User> {

    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final ArrayList<clientThread> threads;
    private Semaphore semafor;

    public clientThread(Socket clientSocket, ArrayList<clientThread> threads, Semaphore semafor) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.semafor = semafor;
    }

    @Override
    public User call() {
        User outcome = new User();//Wynik działania wątku dla Servera
        try {
            //Stwórz input i output streams dla klienta
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            
            //Sprawdź ile jest wolnego miejsca w semaforze/na czacie
            os.println("Wolne miejsca: " + semafor.availablePermits());
            if(semafor.availablePermits()<1){
                os.println("Czekaj...");
            }
            
            semafor.acquire();//<====BLOKADA SEMAFORA
            //Jeżeli wątek przeszedł blokadę 
            long start = System.currentTimeMillis();//Zacznij liczyć czas danego uzytkownika na czacie
            //Ustawienie imienia użytkownika
            os.println("Wpisz swoje imie.");
            String name = is.readLine().trim();
            
            //Wiadomość powitalna
            os.println("Witaj " + name + " \nAby wyjsc wpisz /quit w nowej linii");
            
            //Wyślij do wszystkich wątków informację że nowa osoba dołączyła do czatu
            for(clientThread t : threads){
                if(t != this){
                    t.os.println("*** Uzytkownik " + name + " dolaczyl do czatu !!! ***");
                }
            }
            //Sekcja krytyczna - START
            while (true) {
                String line = is.readLine();//Wiadomość od uzytkownika
                //Jeżeli podano napis /quit to przerwij działanie wątku
                if (line.startsWith("/quit")) {
                    long elapsedTime = System.currentTimeMillis() - start;
                    outcome = new User(name, elapsedTime/1000);
                    semafor.release();
                    break;
                }
                //Wyślij wiadomość do innych użytkowników na czacie
                for(clientThread t : threads){
                    t.os.println("<" + name + "> : " + line);
                }
            }
            //Sekcja krytyczna - STOP
            //Daj znać innym użytkownikom że ten wątek opuścił czat
            for (clientThread t : threads) {
                if (t != this) {
                    t.os.println("*** Uzytkownik " + name
                            + " opuszcza czat !!! ***");
                }
            }
            //Wiadomość pożegnalna
            os.println("*** Pa pa " + name + " ***");


            //Zamykanie wszystkich streamów i socketów
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        } catch (InterruptedException ex) {
            Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return outcome;
    }
}
