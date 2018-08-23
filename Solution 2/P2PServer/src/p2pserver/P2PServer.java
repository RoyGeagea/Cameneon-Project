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
package p2pserver;

/**
 *
 * @author roygeagea
 */
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.*;
import java.util.Random;

public class P2PServer {

    static int PORT = 27222;
    static int clientIDs = 0;

    private static P2PServer instance = null;
    private ArrayList<ClientThread> users;
    private ServerSocket ss;
    private static final String QUIT = "QUIT";
    private static final String JOIN = "JOIN";
    private static final String LIST = "LIST";

    private class ClientThread implements Runnable {

        private PrintStream out;
        private Scanner in;

        private String msg;

        private int id;
        private InetAddress ip;
        private int port;
        private String etat;

        public ClientThread(Socket c) {
            try {
                clientIDs = clientIDs + 1;
                id = clientIDs;
                in = new Scanner(c.getInputStream());
                out = new PrintStream(c.getOutputStream(), true);
                ip = c.getInetAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            P2PServer.PORT = P2PServer.PORT + 1;
            this.port = P2PServer.PORT;
            out.println(P2PServer.PORT);

            while (in.hasNextLine()) {
                String input = in.nextLine().toUpperCase();
                msg = "";
                if (input.toUpperCase().equals(QUIT)) {
                    users.remove(this);
                    for (ClientThread u : users) {
                        u.out.println("REMOVE");
                    }
                    msg = "";
                    msg += "(" + this.ip.getHostAddress() + "," + this.port + "," + this.etat + ")";
                    for (ClientThread u : users) {
                        u.out.println(msg);
                    }
                    break;
                } else if (input.toUpperCase().equals(JOIN) && !users.contains(this)) {
                    msg = "Tu as entrer dans le monde";
                    out.println(msg);
                    etat = "Registered";
                    users.add(this);
                    msg = "";
                    for (ClientThread u : users) {
                        msg += "(" + u.ip.getHostAddress() + "," + u.port + "," + u.etat + ") ";
                    }
                    for (ClientThread u : users) {
                        u.out.println(msg);
                    }
                } else {
                    if (input.toUpperCase().equals("UPDATE")) {
                        String etat = in.nextLine();
                        this.etat = etat;
                        for (ClientThread u : users) {
                            msg += "(" + u.ip.getHostAddress() + "," + u.port + "," + u.etat + ") ";
                        }
                        for (ClientThread u : users) {
                            u.out.println(msg);
                        }
                    } else {
                        msg = "invalid command";
                        out.println(msg);
                    }
                }

            }
            out.println("done");
            in.close();
            out.close();
        }

        public int getId() {
            return id;
        }

        public ClientThread getUser() {
            return this;
        }

        public PrintStream getPrintStream() {
            return out;
        }

        public Scanner getUserScanner() {
            return in;
        }
    }

    private P2PServer() {
        try {
            ss = new ServerSocket(PORT);
            users = new ArrayList<ClientThread>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get users currently joined the server
     *
     * @return users
     */
    public ArrayList<ClientThread> getUsers() {
        return users;
    }

    /**
     * Inserts the user in the server room
     *
     * @param users
     */
    public void setUsers(ArrayList<ClientThread> users) {
        this.users = users;
    }

    /**
     * Gets the number of users in the room
     *
     * @return numUsers
     */
    public static int getNumUsers() {
        return P2PServer.clientIDs;
    }

    /**
     * Sets the number of the users currently in the room
     *
     * @param numUsers
     */
    public static void setNumUsers(int numUsers) {
        P2PServer.clientIDs = numUsers;
    }

    /**
     * Gets the port number of ther server
     *
     * @return
     */
    public static int getPort() {
        return PORT;
    }

    /**
     * Returns a singleton object of the server
     *
     * @return Server
     */
    public static P2PServer getInstance() {
        if (instance == null) {
            instance = new P2PServer();
        }
        return instance;
    }

    /**
     *
     * Listening for client connection and create new thread for each clients
     * this ensures that the server will accept multiple clients at the same
     * time
     */
    public void listen() {
        try {
            while (true) {
                System.out.println("Waiting for a connection..");
                Socket client = ss.accept();
                ClientThread c = new ClientThread(client);
                Thread t = new Thread(c);
                //clients.add(c);
                t.start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        P2PServer server = P2PServer.getInstance();
        server.listen();
    }

}
