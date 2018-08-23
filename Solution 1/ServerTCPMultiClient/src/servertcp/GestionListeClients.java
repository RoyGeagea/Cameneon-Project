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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author roygeagea
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
