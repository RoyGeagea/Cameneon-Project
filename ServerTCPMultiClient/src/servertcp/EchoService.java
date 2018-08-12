/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Pascal Fares
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
                    System.out.print("entrainer...");
                    try {
                        Thread.sleep(3000);
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
                    engineHelperApresMutation(current);
                }
                else {
                    System.out.println("Meme couleur");
                    System.out.println("Les deux Cameneons sont de meme couleur: " + primaire.getCouleur().getCouleur());
                }
            }                
        }
        else {
           ic.getWriter().println("Il n y a pas un autre Cameneon a ce maument, Réessayez plus tard");  
        }
        Thread.sleep(5000);
        primaire.setEtat(Etat.Available);
        current.setEtat(Etat.Available);
    }
    
    public void engineHelperAvantMutation(Cameneon primaire, Cameneon secondaire) {
        System.out.println("Le primaire a la couleur " + primaire.getCouleur().getCouleur());
        System.out.println("Le secondaire a la couleur " + secondaire.getCouleur().getCouleur());
    }

    public void engineHelperApresMutation(Cameneon a) {
        System.out.println("La couleur des Cameneons apres la mutation est: " + a.getCouleur().getCouleur());
    }

}
