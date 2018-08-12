/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clienttcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
/**
 *
 * @author roygeagea
 */
public class Printer implements Runnable {
    
    BufferedReader stdin;
    PrintWriter envoyer;
    ClientTCP client;
    String couleur;
            
    public Printer(BufferedReader stdin, PrintWriter envoyer, ClientTCP client, String couleur) throws IOException {
        this.stdin = stdin;
        this.envoyer = envoyer;
        this.client = client;
        this.couleur = couleur;
    }

    @Override
    public void run() {
        envoyer.printf("%s\n", this.couleur);
        String line;
        try {
            while (!(line = stdin.readLine()).equals("exit")) {
                envoyer.printf("%s\n", line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Printer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
