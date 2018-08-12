/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Pascal Fares
 */
public class ServerTCP {
    
    /**
     * @param args the command line arguments
     */
    
    static Random randomGenerator;
    
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket l = new ServerSocket(2001);
        randomGenerator = new Random();
        GestionListeClients listeClients = new GestionListeClients();
        System.out.println(l.getLocalSocketAddress());
        Thread a = new Thread(new ClientsHandler(l, listeClients));
        a.start();
        a.join();
    }
    
    static public synchronized Cameneon getRandomCameneon(Cameneon forThis, GestionListeClients listeClients) {
        if (listeClients.listeClients.size() > 1) {
            int index = randomGenerator.nextInt(listeClients.listeClients.size());
            Cameneon toReturn = listeClients.listeClients.get(index);
            if (toReturn != forThis && toReturn.getEtat() == Cameneon.Etat.Available) {
                return toReturn;
            }
            else {
                return getRandomCameneon(forThis, listeClients);
            }
        }
        else {
            return null;
        }
    }
}
