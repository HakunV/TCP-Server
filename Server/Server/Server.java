package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.lang.Thread;

public class Server {
    public int port = 6666;

	public ServerSocket mss = null;
    public Socket clientSocket;
	public BufferedInputStream bis = null;
	public BufferedOutputStream bos = null;

    public ArrayList<Client> clients = new ArrayList<Client>();

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

                System.out.println("Client connected");

                Client client = new Client(clientSocket, this);

                Thread clientThread = new Thread(client);
                clientThread.start();
            } catch (Exception e) {
                System.out.println("Client not connected");
            }          
        }
        clientSocket.close();
        mss.close();
    }
}