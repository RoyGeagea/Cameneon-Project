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
package p2pclient;

/**
 *
 * @author roygeagea
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class P2PClient {

    public enum Couleur {
        Bleu,
        Jaune,
        Rouge;

        public String getCouleurInString() {
            if (this == Bleu) {
                return "Bleu";
            } else if (this == Jaune) {
                return "Jaune";
            } else {
                return "Rouge";
            }
        }

        public Couleur getCouleurInType(String couleur) {
            if (couleur == "Bleu") {
                return Couleur.Bleu;
            } else if (couleur == "Jaune") {
                return Couleur.Jaune;
            } else {
                return Couleur.Rouge;
            }
        }
    }

    public enum Etat {
        demande,
        repondre,
        reserved,
        Registered;

        public String getEtatInString() {
            if (this == demande) {
                return "demande";
            } else if (this == repondre) {
                return "repondre";
            } else if (this == reserved) {
                return "reserved";
            } else {
                return "Registered";
            }
        }

        public Etat getEtatInType(String etat) {
            if (etat == "demande") {
                return Etat.demande;
            } else if (etat == "repondre") {
                return Etat.repondre;
            } else if (etat == "reserved") {
                return Etat.reserved;
            } else {
                return Etat.Registered;
            }
        }

    }

    private final static String DEFAULT = "not connected";
    private final static String CONNECTED = "connected";
    private String id;
    private Socket controlSocket;
    private Socket peerSocket;
    private ServerSocket peerServerSocket;
    private String host;
    private int port;
    private String status;
    private Scanner userInput;
    private String cmd;

    private PeerServerThread pt;
    private Thread t;
    private Semaphore mutex;

    private int assignedPort;

    private Couleur couleur;
    private Etat etat;
    private ArrayList<Couleur> colors;
    private ServerResponse ps;
    private ArrayList<P2PClient> clients;

    public P2PClient() {

    }

    public P2PClient(String hostName, int port) {
        colors = new ArrayList<Couleur>();
        clients = new ArrayList<P2PClient>();
        colors.add(Couleur.Bleu);
        colors.add(Couleur.Jaune);
        colors.add(Couleur.Rouge);
        etat = Etat.Registered;
        couleur = colors.get(new Random().nextInt(colors.size()));
        this.setHost(hostName);
        this.setPort(port);
        status = DEFAULT;
        userInput = new Scanner(System.in);
        mutex = new Semaphore(1, true);
        this.connect();
        this.handleServerSocketConnection();
    }

    public String getStatus() {
        return this.status;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    protected Socket getControlSocket() {
        return this.controlSocket;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void connect() {
        try {
            controlSocket = new Socket(host, port);
            if (controlSocket.isBound()) {
                status = CONNECTED;

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleServerSocketConnection() {
        try {
            System.out.println("INSTRUCTIONS:");
            System.out.println("- Ecrire 'manger' pour manger");
            System.out.println("- Ecrire 'entrainer' pour s'entrainer");
            System.out.println("- Ecrire 'demande' pour pouvoir lancer une demande de rendez-vous");
            System.out.println("- Ecrire 'repondre' pour répondre à une demande d’autres \"caménéons\".");
            Scanner in = new Scanner(getControlSocket().getInputStream());
            PrintStream out = new PrintStream(getControlSocket().getOutputStream());
            String response = in.nextLine(); // get the assigned port
            this.assignedPort = Integer.parseInt(response);
            cmd = "join";
            out.println(cmd);
            System.out.println(in.nextLine());
            pt = new PeerServerThread(this.assignedPort);
            t = new Thread(pt);
            t.start(); // create the client server to start listening
            System.out.println("Tu a la couleur: " + this.couleur.getCouleurInString());
            ps = new ServerResponse(in, this);
            Thread w = new Thread(ps);
            w.start();
            while (true) {
//                mutex.acquire();
                System.out.println("Enter a command");
                cmd = userInput.nextLine();
                if (cmd.equals("demande") && peerServerSocket != null) {
                    this.etat = Etat.demande;
                    this.handleDemandeRequest();
                } 
                else if (cmd.equals("repondre") && peerServerSocket != null) {
                    this.etat = Etat.repondre;
                }
                else if (cmd.equals("manger")) {
                    System.out.println("YAMIII");
                } 
                else if (cmd.equals("entrainer")) {
                    handleTrainingRequest();
                }
                else {
                    
                }
                if (response.toUpperCase().equals("EXIT")) {
                    break;
                }
//                mutex.release();
            }
            in.close();
            out.close();
            status = DEFAULT;
            System.out.println("Disconnected from the server");
            peerServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void connectToPeer() throws InterruptedException {
        handlePeerConnection();
    }

    public void handlePeerConnection() throws InterruptedException {
        PeerResponsesThread prt = new PeerResponsesThread();
        Thread t = new Thread(prt);
        t.start();
        t.join();
    }

    private class PeerResponsesThread implements Runnable {
        @Override
        public void run() {
            try {
                Scanner in = new Scanner(peerSocket.getInputStream());
                PrintStream out = new PrintStream(peerSocket.getOutputStream());
                out.println("Playing");
                System.out.println("En attente de réponse de la part de l'autre...");
                String secondPartyAccept = in.nextLine();
                if (secondPartyAccept.equalsIgnoreCase("yes")) {
                    etat = Etat.reserved;
                    // send to the server that you are reserved
                    Scanner inServer = new Scanner(getControlSocket().getInputStream());
                    PrintStream outServer = new PrintStream(getControlSocket().getOutputStream());
                    outServer.println("update");
                    outServer.println("reserved");
                    String toSend = couleur.getCouleurInString();
                    out.println(toSend); // send my color
                    System.out.println("Tu as la couleur " + toSend);
                    couleur = Couleur.valueOf(in.nextLine()); // color after the mutation
                    System.out.println("Ta couleur apres la mutation est: " + couleur.getCouleurInString());
                    etat = Etat.Registered;
                    // send to the server that you are Registered,
                    outServer.println("update");
                    outServer.println("Registered");
                    out.println("leave");
                    in.close();
                    peerSocket.close();
                }
                else {
                    in.close();
                    System.out.println("Aucun veut jouer a ce moment");
                    peerSocket.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public void connectToPeerForTraining() throws InterruptedException {
        handlePeerConnectionForTraining();
    }
    
    public void handlePeerConnectionForTraining() throws InterruptedException {
        PeerResponsesThreadForTraining prt = new PeerResponsesThreadForTraining();
        Thread t = new Thread(prt);
        t.start();
        t.join();
    }
    
    private class PeerResponsesThreadForTraining implements Runnable {
        @Override
        public void run() {
            try {
                Scanner in = new Scanner(peerSocket.getInputStream());
                PrintStream out = new PrintStream(peerSocket.getOutputStream());
                out.println("Training");
                String secondPartyAccept = in.nextLine();
                if (secondPartyAccept.equalsIgnoreCase("yes")) {
                    etat = Etat.reserved;
                    // send to the server that you are reserved
                    Scanner inServer = new Scanner(getControlSocket().getInputStream());
                    PrintStream outServer = new PrintStream(getControlSocket().getOutputStream());
                    outServer.println("update");
                    outServer.println("reserved");
                    etat = Etat.Registered;
                    // send to the server that you are Registered,
                    outServer.println("update");
                    outServer.println("Registered");
                    out.println("leave");
                    in.close();
                    peerSocket.close();
                }
                else {
                    in.close();
                    System.out.println("Aucun veut jouer a ce moment");
                    peerSocket.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }    

    private class PeerServerThread implements Runnable {

        private Scanner peerServerInput;

        public PeerServerThread(int assignedPort) {
            try {
                peerServerSocket = new ServerSocket(assignedPort);
                peerServerInput = new Scanner(System.in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
//                    mutex.acquire();
                    Socket client = peerServerSocket.accept();
                    PeerResponse res = new PeerResponse(client);
                    Thread a = new Thread(res);
                    a.start();
//                    mutex.release();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private class ServerResponse implements Runnable {

        private Scanner in;
        private P2PClient client;

        public ServerResponse(Scanner in, P2PClient client) {
            this.in = in;
            this.client = client;
        }

        @Override
        public void run() {
            while (in.hasNextLine()) {
                String input = in.nextLine();
                if (input.toUpperCase().equals("REMOVE")) {
                    input = in.nextLine();
                    String a = input.replace("(", "");
                    a = a.replace(")", "");
                    String[] sockData = a.split(",");
                    String host = sockData[0];
                    String port = sockData[1];
                    String etat = sockData[2];
                    if (!(host.equalsIgnoreCase(this.client.host) && port.equalsIgnoreCase(Integer.toString(this.client.assignedPort)))) {
                        Result res = contains(Integer.valueOf(port));
                        if (res.isFound) {
                            clients.remove(res.atIndex);
                        }
                    }
                } else {
                    String[] data = input.split(" ");
                    for (String current : data) {
//                        System.out.println(current);
                        String a = current.replace("(", "");
                        a = a.replace(")", "");
                        String[] sockData = a.split(",");
                        String host = sockData[0];
                        String port = sockData[1];
                        String etat = sockData[2];
                        if (!(host.equalsIgnoreCase(this.client.host) && port.equalsIgnoreCase(Integer.toString(this.client.assignedPort)))) {
                            Result res = contains(Integer.valueOf(port));
                            if (!res.isFound) {
                                P2PClient toAdd = new P2PClient();
                                toAdd.host = host;
                                toAdd.assignedPort = Integer.valueOf(port);
                                toAdd.etat = Etat.valueOf(etat);
                                clients.add(toAdd);
                            } else {
                                P2PClient toEdit = clients.get(res.atIndex);
                                toEdit.host = host;
                                toEdit.assignedPort = Integer.valueOf(port);
                                toEdit.etat = Etat.valueOf(etat);
                            }
                        }
                    }
                }
            }
        }
        
        public Result contains(int c) {
            boolean isFound = false;
            int atIndex = -1;
            for (int i=0; i<clients.size(); i++) {
                P2PClient currentClient = clients.get(i);
                if (currentClient.port == c) {
                    isFound = true;
                }
            }
            return new Result(isFound, atIndex);
        }
        
        private class Result {
            boolean isFound = false;
            int atIndex = -1;
            
            public Result(boolean isFound, int atIndex) {
                this.isFound = isFound;
                this.atIndex = atIndex;
            }
        }
    }
    
    private class PeerResponse implements Runnable {

        private Socket client;

        public PeerResponse(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                Scanner in = new Scanner(client.getInputStream());
                PrintStream out = new PrintStream(client.getOutputStream());
                String toCompare = in.nextLine();
                if (toCompare.equals("Training")) {
                    String psIn = "";
                    if (etat == Etat.reserved || etat == Etat.demande || etat == Etat.repondre) {
                        psIn = "no";
                        out.println(psIn);
                        in.close();
                    } else {
                        psIn = "yes";
                        out.println(psIn);
                        etat = Etat.reserved;
                        // send to the server that you are reserved
                        Scanner inServer = new Scanner(getControlSocket().getInputStream());
                        PrintStream outServer = new PrintStream(getControlSocket().getOutputStream());
                        outServer.println("update");
                        outServer.println("reserved");
                        String response = "";
                        System.out.println("J'ai été formé");
                        etat = Etat.Registered;
                        // send to the server that you are Registered
                        outServer.println("update");
                        outServer.println("Registered");
                        while (!response.equals("leave")) {
                            response = in.nextLine();
                        }
                        out.println("leave");
                        in.close();
                    }
                } else {
                    String psIn = "";
                    if (etat == Etat.reserved || etat == Etat.Registered) {
                        psIn = "no";
                        out.println(psIn);
                        in.close();
                    } else {
                        psIn = "yes";
                        out.println(psIn);
                        etat = Etat.reserved;
                        // send to the server that you are reserved
                        Scanner inServer = new Scanner(getControlSocket().getInputStream());
                        PrintStream outServer = new PrintStream(getControlSocket().getOutputStream());
                        outServer.println("update");
                        outServer.println("reserved");
                        // out.println("Peer accepted your chat request");
                        String response = in.nextLine();
                        System.out.println("Tu as la couleur " + couleur.getCouleurInString());
                        handleColorMutation(Couleur.valueOf(response), couleur, out);
                        etat = Etat.Registered;
                        // send to the server that you are Registered
                        outServer.println("update");
                        outServer.println("Registered");
                        while (!response.equals("leave")) {
                            response = in.nextLine();
                        }
                        out.println("leave");
                        in.close();
                        System.out.println("Enter a command");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(P2PClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(P2PClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void handleDemandeRequest() throws IOException, InterruptedException {
        if (clients.size() == 0) {
            System.out.println("Il n'ya pas un autre Cameneon a ce moment, réessayez plus tard");
            this.etat = Etat.Registered;
        }
        else {
            ArrayList<P2PClient> availableClients = new ArrayList<P2PClient>();
            for (P2PClient client : clients) {
                if (client.etat != Etat.reserved) {
                    availableClients.add(client);
                }
            }
            if (availableClients.size() > 0) {
                P2PClient toPlayWith = availableClients.get(new Random().nextInt(availableClients.size()));
                String peerIp = toPlayWith.host;
                Integer peerPort = toPlayWith.assignedPort;
                peerSocket = new Socket(peerIp, peerPort);
                connectToPeer();
            }
            else {
                System.out.println("Il n'ya pas un autre Cameneon a ce moment, réessayez plus tard");
                this.etat = Etat.Registered;
            }
        }
    }
    
    public void handleTrainingRequest() throws IOException, InterruptedException {
        if (clients.size() == 0) {
            System.out.println("Il n'ya pas un autre Cameneon a ce moment, réessayez plus tard");
            this.etat = Etat.Registered;
        }
        else {
            ArrayList<P2PClient> availableClients = new ArrayList<P2PClient>();
            for (P2PClient client : clients) {
                if (client.etat != Etat.reserved) {
                    availableClients.add(client);
                }
            }
            if (availableClients.size() > 0) {
                P2PClient toPlayWith = availableClients.get(new Random().nextInt(availableClients.size()));
                String peerIp = toPlayWith.host;
                Integer peerPort = toPlayWith.assignedPort;
                peerSocket = new Socket(peerIp, peerPort);
                connectToPeerForTraining();
            }
            else {
                System.out.println("Il n'ya pas un autre Cameneon a ce moment, réessayez plus tard");
                this.etat = Etat.Registered;
            }
        }
    }
    
    public void handleColorMutation(Couleur cA, Couleur cB, PrintStream out) throws InterruptedException {
        if (cA != cB) {
//            Thread.sleep(5000);
            if (cA == Couleur.Bleu && cB == Couleur.Jaune) {
                couleur = Couleur.Rouge;
                out.println(Couleur.Rouge.getCouleurInString());
            } else if (cA == Couleur.Bleu && cB == Couleur.Rouge) {
                couleur = Couleur.Jaune;
                out.println(Couleur.Jaune.getCouleurInString());
            } else if (cA == Couleur.Jaune && cB == Couleur.Bleu) {
                couleur = Couleur.Rouge;
                out.println(Couleur.Rouge.getCouleurInString());
            } else if (cA == Couleur.Jaune && cB == Couleur.Rouge) {
                couleur = Couleur.Bleu;
                out.println(Couleur.Bleu.getCouleurInString());
            } else if (cA == Couleur.Rouge && cB == Couleur.Jaune) {
                couleur = Couleur.Bleu;
                out.println(Couleur.Bleu.getCouleurInString());
            } else if (cA == Couleur.Rouge && cB == Couleur.Bleu) {
                couleur = Couleur.Jaune;
                out.println(Couleur.Jaune.getCouleurInString());
            }
            System.out.println("Ton couleur apres la mutation est: " + couleur.getCouleurInString());
        } else {
            String message = "Tu as entrer en jeu avec un Cameneon de meme couleur";
            System.out.println(message);
            out.println(cA);
        }
    }
    
    public static void main(String[] args) {
        String hostName = "127.0.0.1";
        int portNumber = 27222;
        P2PClient client = new P2PClient(hostName, portNumber);
    }
}
