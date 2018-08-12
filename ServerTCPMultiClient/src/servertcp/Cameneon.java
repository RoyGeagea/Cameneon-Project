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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe de la socket Cliente avec la preparation de reader et writer
 * Cette classe est pour aider la gestion des stream (on pourrait s'en passer)
 * @author pascalfares
 */

public class Cameneon {
    
    public enum Couleur {
        Bleu,
        Jaune,
        Rouge;
        
        public String getCouleur() {
            if (this == Bleu) {
                return "Bleu";
            }
            else if (this == Jaune) {
                return "Jaune";
            }
            else {
                return "Rouge";
            }
        }
    }
    
    public enum Etat {
        Available,
        Unavailable,
        Unregistered;
    }
    
    /**
     * La socket de service du client
     */
    private Socket serviceClientSocket;
    /**
     * La reader de la socket dún client
     */
    private BufferedReader reader;

    /**
     * La writer de la socket d'un client
     */
    private PrintWriter writer;
    
    private int ID;
    private Couleur couleur;
    private Cameneon partenaire;
    private Etat etat;
    
    public Cameneon(Socket s, int id) throws IOException {
        serviceClientSocket=s;
        this.ID = id;
        this.etat = Etat.Unregistered;
        reader = getInput(s);
        writer = getoutput(s);
    }

    BufferedReader getInput(Socket p) throws IOException {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    PrintWriter getoutput(Socket p) throws IOException {
        return new PrintWriter(new OutputStreamWriter(p.getOutputStream()),true);
    }
    /**
     * @return the serviceClientSocket
     */
    public Socket getServiceClientSocket() {
        return serviceClientSocket;
    }

    /**
     * @param serviceClientSocket the serviceClientSocket to set
     */
    public void setServiceClientSocket(Socket serviceClientSocket) {
        this.serviceClientSocket = serviceClientSocket;
    }

    /**
     * @return the reader
     */
    public BufferedReader getReader() {
        return reader;
    }

    /**
     * @param reader the reader to set
     */
    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * @return the writer
     */
    public PrintWriter getWriter() {
        return writer;
    }

    /**
     * @param writer the writer to set
     */
    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
    
    public Couleur getCouleur() {
        return this.couleur;
    }
        
    public void setCouleur(Couleur c) {
        this.couleur = c;
    }
    
    public Cameneon getPartenaire() {
        return this.partenaire;
    }
    
    public void setPartenaire(Cameneon c) {
        this.partenaire = c;
    }

    public int getID() {
        return this.ID;
    }
    
    public Etat getEtat() {
        return this.etat;
    }
    
    public void setEtat(Etat etat) {
        this.etat = etat;
    }
}
