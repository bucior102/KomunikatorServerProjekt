/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package komunikatorserverprojekt;

import java.util.ArrayList;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;
import java.util.*;
/**
 *
 * @author Bucior
 */
public class KomunikatorServerProjekt {

    /**
     * @param args the command line arguments
     */

    public static void main(String args[]) throws InterruptedException, ExecutionException, SocketException {
        Semaphore semafor = new Semaphore(2);//Semafor pilnujący aby w pokoju na czacie nie było więcej niż n użytkownikow
        //Sokety
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        ExecutorService pool = Executors.newCachedThreadPool();//Wykonawca uruchamiający nasze wątki
        ArrayList<clientThread> threads = new ArrayList<clientThread>();//Lista wątków klientów która będzie współdzielona między wątkami
        
        int portNumber = 2222;// Domyślny numer portu
        System.out.println("Serwer dziala...");
        //Otwórz server socket na portNumber (default 2222)
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }
        List<Future<User>> lista = new ArrayList<Future<User>>();//lista do której będziemy zapisywać wyniki z wątków Callable<User>
        ArrayList<Future<User>> listaDoUsuniecia = new ArrayList<Future<User>>();//lista do której będziemy zapisywać wyniki z wątków Callable<User> które mają być usunięte
        
        serverSocket.setSoTimeout(1000);//Ustaw jak długo server ma czekać na połączenie od klienta
        while (true) {
            lista.removeAll(listaDoUsuniecia);//Usuń z listy zawierającej wyniki działania wątków (Future<User>), te elementy które są przeznaczone do usunięcia
            try {
                //Akceptuj połączenie od klienta na sokecie - normalnie tutaj program się zatrzymuje czekając na połączenie
                //Ale dzięki serverSocket.setSoTimeout(1000);, program czeka 1 sekundę, i jeżeli nie otrzyma połączenia, wyskakuje z try-catch
                clientSocket = serverSocket.accept();
                threads.add(new clientThread(clientSocket, threads, semafor));//Stwórz nowy wątek i dodaj go do listy wątków - lista ta zawiera klientów na czcie
                //threads.get(threads.size()-1).start();
                lista.add(pool.submit(threads.get(threads.size() - 1)));//Uruchom wątek i dodaj go do listy pracujących wątków
            } catch (IOException e) {
                //System.out.println(e);
            }
            //Wyświetlanie wyników działania wątków
            for (Future<User> f : lista) {
                if (f.isDone()) {//Jeżeli wątek skończył
                    System.out.println(f.get().toString());//Wyświetl wynik działania watku (Future<User>)
                    listaDoUsuniecia.add(f);//Dodoaj obiekty które skończyły (.isDone()) do listy obiektów do usunięcia
                }
            }

        }
    }

}
