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
public class Reader implements Runnable {

    BufferedReader ir;
    
    public Reader(BufferedReader ir) throws IOException {
        this.ir = ir;
    }

    @Override
    public void run() {
        try {
            String line;
            while (!(line = ir.readLine()).equals("exit")) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
