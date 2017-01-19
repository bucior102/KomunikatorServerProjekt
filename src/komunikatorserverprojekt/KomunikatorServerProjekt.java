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
        Semaphore semafor = new Semaphore(2);
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        ExecutorService pool = Executors.newCachedThreadPool();
        ArrayList<clientThread> threads = new ArrayList<clientThread>();
        // The default port number.
        int portNumber = 2222;
        System.out.println("Serwer dziala...");
        /*
         * Open a server socket on the portNumber (default 2222). Note that we can
         * not choose a port less than 1023 if we are not privileged users (root).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }
        List<Future<User>> lista = new ArrayList<Future<User>>();
        ArrayList<Future<User>> listaDoUsuniecia = new ArrayList<Future<User>>();
        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */
        serverSocket.setSoTimeout(1000);
        while (true) {
            lista.removeAll(listaDoUsuniecia);
            try {
                clientSocket = serverSocket.accept();
                threads.add(new clientThread(clientSocket, threads, semafor));
                //threads.get(threads.size()-1).start();
                lista.add(pool.submit(threads.get(threads.size() - 1)));
            } catch (IOException e) {
                //System.out.println(e);
            }
            
            for (Future<User> f : lista) {
                if (f.isDone()) {
                    System.out.println(f.get().toString());
                    listaDoUsuniecia.add(f);
                }
            }

        }
    }

}
