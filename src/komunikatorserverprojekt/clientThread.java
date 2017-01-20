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
    private TASLock lock;
    private Rating rating;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public clientThread(Socket clientSocket, ArrayList<clientThread> threads, Semaphore semafor, TASLock lock, Rating rating) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.semafor = semafor;
        this.lock = lock;
        this.rating = rating;
    }

    @Override
    public User call() {
        User outcome = new User();//Wynik działania wątku dla Servera
        try {
            //Stwórz input i output streams dla klienta
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());

            //Sprawdź ile jest wolnego miejsca w semaforze/na czacie
            os.println(ANSI_BLUE + "Wolne miejsca: " + ANSI_RESET + semafor.availablePermits());
            if (semafor.availablePermits() < 1) {
                os.println(ANSI_BLUE + "Czekaj..." + ANSI_RESET);
            }

            semafor.acquire();//<====BLOKADA SEMAFORA
            //Jeżeli wątek przeszedł blokadę 
            long start = System.currentTimeMillis();//Zacznij liczyć czas danego uzytkownika na czacie
            //Ustawienie imienia użytkownika
            os.println(ANSI_BLUE + "Wpisz swoje imie." + ANSI_RESET);
            String name = is.readLine().trim();

            //Wiadomość powitalna
            os.println(ANSI_BLUE + "Witaj " + ANSI_RESET + name);
            os.println(ANSI_BLUE + "Aby wyjsc wpisz" + ANSI_RESET + " /quit");
            os.println(ANSI_BLUE + "Aby dodac pozytywna ocene czatu wpisz" + ANSI_RESET + " /rating+");
            os.println(ANSI_BLUE + "Aby dodac negatywna ocene czatu wpisz" + ANSI_RESET + " /rating-");
            os.println(ANSI_BLUE + "Aby wyświetlic oceny wpisz" + ANSI_RESET + " /rating");

            //Wyślij do wszystkich wątków informację że nowa osoba dołączyła do czatu
            for (clientThread t : threads) {
                if (t != this) {
                    t.os.println(ANSI_BLUE + "*** Uzytkownik " + ANSI_RESET + name + ANSI_RESET + " dolaczyl do czatu !!! ***" + ANSI_RESET);
                }
            }
            //Sekcja krytyczna - START
            while (true) {
                String line = is.readLine();//Wiadomość od uzytkownika
                //Jeżeli podano napis /quit to przerwij działanie wątku
                if (line.startsWith("/quit")) {
                    long elapsedTime = System.currentTimeMillis() - start;
                    outcome = new User(name, elapsedTime / 1000);
                    semafor.release();
                    break;
                } else if (line.startsWith("/rating+")) {
                    lock.lock();
                    rating.addPositive();
                    lock.unlock();
                } else if (line.startsWith("/rating-")) {
                    lock.lock();
                    rating.addNegative();
                    lock.unlock();
                } else if (line.startsWith("/rating")) {
                    this.os.println(ANSI_GREEN + "Oceny pozytywne: " + ANSI_RESET + rating.getPositiveRating());
                    this.os.println(ANSI_RED + "Oceny negatywne: " + ANSI_RESET + rating.getNegativeRating());
                }
                //Wyślij wiadomość do innych użytkowników na czacie
                for (clientThread t : threads) {
                    t.os.println(ANSI_YELLOW + "<" + name + "> : " + ANSI_RESET + line);
                }
            }
            //Sekcja krytyczna - STOP
            //Daj znać innym użytkownikom że ten wątek opuścił czat
            for (clientThread t : threads) {
                if (t != this) {
                    t.os.println(ANSI_BLUE + "*** Uzytkownik " + ANSI_RESET + name
                            + ANSI_BLUE + " opuszcza czat !!! ***");
                }
            }
            //Wiadomość pożegnalna
            os.println(ANSI_BLUE + "*** Pa pa " + ANSI_RESET + name + ANSI_BLUE + " ***" + ANSI_RESET);

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
