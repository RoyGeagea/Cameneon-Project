/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roygeagea
 */
public class ClientsHandler implements Runnable {
    
    ServerSocket l;
    GestionListeClients listeClients;
    
    public ClientsHandler(ServerSocket l, GestionListeClients listeClients) {
        this.l = l;
        this.listeClients = listeClients;
    }
    
    @Override
    public void run() {
        int i = 0;
        while (true) {
            i = i + 1;
            Socket serviceSocket;
            try {
                serviceSocket = l.accept();
                new Thread(new EchoService(serviceSocket, listeClients, i)).start();
            } catch (IOException ex) {
                Logger.getLogger(ClientsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
