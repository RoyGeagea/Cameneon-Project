/*
 * Copyright © <Pascal Fares @ ISSAE - Cnam Liban>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * The Software is provided “as is”, without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, 
 * fitness for a particular purpose and noninfringement. In no event shall the authors or copyright holders be liable for any claim, damages or other liability, 
 * whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the Software. »
 */
package servertcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pascalfares
 */
public class GestionListeClients {
    List<Cameneon> listeClients;
    
    public GestionListeClients() {
        listeClients = new ArrayList<>();
    }
    
    public void add(Cameneon c){
        listeClients.add(c);
    }
    
    public void remove(Cameneon c) throws IOException{
        c.getReader().close();
        c.getWriter().close();
        c.getServiceClientSocket().close();
        listeClients.remove(c);
    }
    
    public void sendMessageToAll(String m, Cameneon envoyeur) {
        for (Cameneon ic : listeClients) {
            if (ic != envoyeur) {
                System.out.printf("A transmettre %s à %s\n", m, ic.getServiceClientSocket().getRemoteSocketAddress());
                ic.getWriter().println(m);
            }
        }
    }
}
