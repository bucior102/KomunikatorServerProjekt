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

/**
 *
 * @author Bucior
 */
/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. When a client leaves the chat room this thread informs also
 * all the clients about that and terminates.
 */
class clientThread extends Thread {

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

    public void run() {

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            
            os.println("Wolne miejsca: " + semafor.availablePermits());
            if(semafor.availablePermits()<1){
                os.println("Czekaj...");
            }
            semafor.acquire();
            
            os.println("Wpisz swoje imie.");
            String name = is.readLine().trim();
            os.println("Witaj " + name
                    + " \nAby wyjsc wpisz /quit w nowej linii");
            for(clientThread t : threads){
                if(t != this){
                    t.os.println("*** Uzytkownik " + name + " dolaczyl do czatu !!! ***");
                }
            }
            
            while (true) {
                String line = is.readLine();
                if (line.startsWith("/quit")) {
                    semafor.release();
                    break;
                }
                for(clientThread t : threads){
                    t.os.println("<" + name + "> : " + line);
                }
            }
            
            for (clientThread t : threads) {
                if (t != this) {
                    t.os.println("*** Uzytkownik " + name
                            + " opuszcza czat !!! ***");
                }
            }
            os.println("*** Pa pa " + name + " ***");


            /*
             * Close the output stream, close the input stream, close the socket.
             */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        } catch (InterruptedException ex) {
            Logger.getLogger(clientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
