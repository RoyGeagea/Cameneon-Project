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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import servertcp.Cameneon.Etat;

/**
 *
 * @author roygeagea
 */
public class EchoService implements Runnable {

    private final Cameneon ic;
    private GestionListeClients listeClients;
    
    public EchoService(Socket s, GestionListeClients listeClients, int id) throws IOException {
        ic=new Cameneon(s, id);
        this.listeClients=listeClients;
        listeClients.add(ic);
    }

    @Override
    public void run() {        
        try {
//            System.out.println(ic.getServiceClientSocket().getRemoteSocketAddress());
            String couleur = ic.getReader().readLine();
            if (couleur.equalsIgnoreCase("Bleu")) {
                ic.setCouleur(Cameneon.Couleur.Bleu);
            }
            else if(couleur.equalsIgnoreCase("Jaune")) {
                ic.setCouleur(Cameneon.Couleur.Jaune);
            }
            else {
                ic.setCouleur(Cameneon.Couleur.Rouge);
            }
            ic.setEtat(Etat.Available);
            System.out.println("Un Cameneon a connecter avec la couleur suivante: " + ic.getCouleur().getCouleur());
            String line;
            while (!(line = ic.getReader().readLine()).equals("exit")) {
                 if (ic.getEtat() == Etat.Unavailable && (line.equalsIgnoreCase("entrainer") || line.equalsIgnoreCase("manger"))) {
                     ic.getWriter().println("Tu est entraine de jouer maintenant");
                 }
                 else {
                 if (line.equalsIgnoreCase("manger")) {
                    System.out.print("Yamii.....");
                     try {
                         Thread.sleep(3000);
                     } catch (InterruptedException ex) {
                         Logger.getLogger(EchoService.class.getName()).log(Level.SEVERE, null, ex);
                     }
                    System.out.print("\n");
                }
                else if (line.equalsIgnoreCase("entrainer")) {
                    try {
                        trainingEngine(ic);
                    } catch (InterruptedException ex) {
                         Logger.getLogger(EchoService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.print("\n");
                }
                else if (line.equalsIgnoreCase("jouer")) {
                    try {
                        gameEngine(ic);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EchoService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {
                    
                }                     
              }
            }
            System.out.println("Apres boucle");
        } catch (IOException ex) {
            System.out.println("Error 1");
            Logger.getLogger(EchoService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println("Error 2");
        }
    }
    
    public void gameEngine(Cameneon primaire) throws InterruptedException {
        Cameneon current = ServerTCP.getRandomCameneon(ic, this.listeClients);
        if (current != null) {
            if (current.getID() != primaire.getID() && current.getEtat() == Etat.Available) {
                primaire.setEtat(Etat.Unavailable);
                current.setEtat(Etat.Unavailable);
                if (current.getCouleur() != primaire.getCouleur()) {
                    engineHelperAvantMutation(primaire, current);
                    if (current.getCouleur() == Cameneon.Couleur.Bleu && primaire.getCouleur() == Cameneon.Couleur.Jaune) {
                       current.setCouleur(Cameneon.Couleur.Rouge);
                       primaire.setCouleur(Cameneon.Couleur.Rouge);
                    }
                    else if (current.getCouleur() == Cameneon.Couleur.Bleu && primaire.getCouleur() == Cameneon.Couleur.Rouge) {
                       current.setCouleur(Cameneon.Couleur.Jaune);
                       primaire.setCouleur(Cameneon.Couleur.Jaune);                        
                    }
                    else if (current.getCouleur() == Cameneon.Couleur.Jaune && primaire.getCouleur() == Cameneon.Couleur.Bleu) {
                       current.setCouleur(Cameneon.Couleur.Rouge);
                       primaire.setCouleur(Cameneon.Couleur.Rouge);                        
                    }
                    else if (current.getCouleur() == Cameneon.Couleur.Jaune && primaire.getCouleur() == Cameneon.Couleur.Rouge) {
                       current.setCouleur(Cameneon.Couleur.Bleu);
                       primaire.setCouleur(Cameneon.Couleur.Bleu);                        
                    }
                    else if (current.getCouleur() == Cameneon.Couleur.Rouge && primaire.getCouleur() == Cameneon.Couleur.Jaune) {
                       current.setCouleur(Cameneon.Couleur.Bleu);
                       primaire.setCouleur(Cameneon.Couleur.Bleu);                        
                    }    
                    else if (current.getCouleur() == Cameneon.Couleur.Rouge && primaire.getCouleur() == Cameneon.Couleur.Bleu) {
                       current.setCouleur(Cameneon.Couleur.Jaune);
                       primaire.setCouleur(Cameneon.Couleur.Jaune);                        
                    }
                    engineHelperApresMutation(primaire, current);
                }
                else {
                    current.getWriter().println("Tu as entrer en jeu avec un Cameneon de meme couleur");
                    primaire.getWriter().println("Tu as entrer en jeu avec un Cameneon de meme couleur");
                    System.out.println("Meme couleur");
                    System.out.println("Les deux Cameneons sont de meme couleur: " + primaire.getCouleur().getCouleur());
                }
                Thread.sleep(5000);
                primaire.setEtat(Etat.Available);
                current.setEtat(Etat.Available);
            }                
        }
        else {
           ic.getWriter().println("Il n y a pas un autre Cameneon a ce maument, Réessayez plus tard");  
        }
    }
    
    public void engineHelperAvantMutation(Cameneon primaire, Cameneon secondaire) {
        System.out.println("Le primaire a la couleur " + primaire.getCouleur().getCouleur());
        System.out.println("Le secondaire a la couleur " + secondaire.getCouleur().getCouleur());
        primaire.getWriter().println("Tu as entrer en jeu avec un Cameneon de couleur differente");
        secondaire.getWriter().println("Tu as entrer en jeu avec un Cameneon de couleur differente");
    }

    public void engineHelperApresMutation(Cameneon a, Cameneon b) {
        System.out.println("La couleur des Cameneons apres la mutation est: " + a.getCouleur().getCouleur());
        a.getWriter().println("La couleur apres la mutation est: " + a.getCouleur().getCouleur());
        b.getWriter().println("La couleur apres la mutation est: " + a.getCouleur().getCouleur());
    }

    public void trainingEngine(Cameneon primaire) throws InterruptedException {
        Cameneon current = ServerTCP.getRandomCameneon(ic, this.listeClients);
        if (current != null) {
            if (current.getID() != primaire.getID() && current.getEtat() == Etat.Available) {
                primaire.setEtat(Etat.Unavailable);
                current.setEtat(Etat.Unavailable);
                primaire.getWriter().println("Tu est entrain d'entrainer");
                current.getWriter().println("Tu as etait choisi pour faire un cour");                
                Thread.sleep(5000);
                primaire.getWriter().println("Tu as fini d'entrainer");
                current.getWriter().println("Le cour a fini");
                primaire.setEtat(Etat.Available);
                current.setEtat(Etat.Available);
            }                
        }
        else {
           ic.getWriter().println("Il n y a pas un autre Cameneon a ce maument, Réessayez plus tard");  
        }
    }
    
}
