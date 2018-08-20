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
package Clienttcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roygeagea
 */
public class ClientTCP implements Runnable {

    Socket l = null;
    ArrayList<String> colorsData = new ArrayList<String>();
    Random randomGenerator;

    private static BufferedReader getInput(InputStream is) throws IOException {
        return new BufferedReader(new InputStreamReader(is));
    }

    private static BufferedReader getInput(Socket p) throws IOException {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    private static PrintWriter getoutput(Socket p) throws IOException {
        //Avec autoflush
        return new PrintWriter(new OutputStreamWriter(p.getOutputStream()), true);
    }

    /**
     * @param args the command line arguments
     */
    public ClientTCP() throws IOException, InterruptedException {
        try {
            randomGenerator = new Random();
            System.out.println("INSTRUCTIONS:");
            System.out.println("- Ecrire 'manger' pour manger");
            System.out.println("- Ecrire 'entrainer' pour s'entrainer");
            System.out.println("- Ecrire 'jouer' pour entrer dans le mail");
            colorsData.add("Rouge");
            colorsData.add("Jaune");
            colorsData.add("Bleu");
            l = new Socket("localhost", 2001);
            int index = randomGenerator.nextInt(colorsData.size());
//            System.out.println(l.getLocalSocketAddress());
            //Input stream de la socket (depuis le serveur)
            BufferedReader ir = getInput(l);
            //Input stream du stdin
            BufferedReader stdin = getInput(System.in);
            //Output de la socket vers le serveur
            PrintWriter envoyer = getoutput(l);

            Thread reader = new Thread(new Reader(ir));
            Thread printer = new Thread(new Printer(stdin, envoyer, this, colorsData.get(index)));
            printer.start();
            reader.start();
            reader.join();
            printer.join();
        } finally {
            if (l != null) {
                l.close();
            }
        }
    }

    @Override
    public void run() {
        try {
            try {
                ClientTCP cl = new ClientTCP();
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientTCP.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
