package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.ServerSocket;
import java.lang.Thread;

public class Server {
    public int port = 6666;

	public ServerSocket mss = null;
    public Socket clientSocket;
	public BufferedInputStream bis = null;
	public BufferedOutputStream bos = null;

    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private HashMap<ClientHandler, Thread> clientThreads = new HashMap<ClientHandler, Thread>();

    public Server() {
        try {
            mss = new ServerSocket(port);
        }
        catch (IOException e) {
            System.out.println("Could not start server");
            e.printStackTrace();
        }
    }

    public void runServer() throws IOException {
        boolean serverActive = true;
        while(serverActive) {
            try {
                clientSocket = mss.accept();

                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);

                Thread cThread = new Thread(client);
                clientThreads.put(client, cThread);

                cThread.start();
            } catch (Exception e) {
                System.out.println("Client not connected");
            }          
        }
        mss.close();
    }

    public void removeClient(ClientHandler client) {
        if (clients.contains(client)) {
            clients.remove(client);
        }
        try {
            client.getSocket().close();
            System.out.println("Client Connection Successfully Shut Down");
            System.out.println();
        }
        catch (IOException e) {
            System.out.println("Failed when closing socket");
            System.out.println();
            e.printStackTrace();
        }
    }

    public void removeDups(String imei, ClientHandler client) {
        if (clients.size() <= 1) {
            System.out.println("No Other Clients With This IMEI");
            System.out.println();
        }
        else {
            if (clients.contains(client)) {
                int i = clients.indexOf(client);
                for (int j = 0; j < i; j++) {
                    if (clients.get(j).getImei().equals(imei)) {
                        removeClient(clients.get(j));
                        System.out.println("Removed A Client");
                        System.out.println();
                    }
                }
            }
            else {
                System.out.println("This Client Does Not Exist");
                System.out.println();
            }
        }
    }
}