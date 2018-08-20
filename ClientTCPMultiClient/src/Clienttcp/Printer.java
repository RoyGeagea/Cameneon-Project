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
