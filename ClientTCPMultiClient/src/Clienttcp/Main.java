/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clienttcp;

import java.io.IOException;

/**
 *
 * @author roygeagea
 */
public class Main {
        static final int N = 10;

        public static void main(String[] args) throws IOException, InterruptedException {
//            for (int x = 1; x <= N; x++) {
//                println(x);
            Thread thread = new Thread(new ClientTCP());
            thread.start();
            thread.join();
//            }
        }
        
        public static void println(int s) {
            System.out.println(s);
        }
}
