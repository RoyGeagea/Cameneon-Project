/*
MIT License

Copyright (c) 2018, Roy Geagea

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
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
 * @author roygeagea
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
