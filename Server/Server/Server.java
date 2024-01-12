package Server;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;

public class Server {
    public int port = 6666;

	public ServerSocket mss = null;
    public Socket clientSocket;
	public BufferedInputStream bis = null;
	public BufferedOutputStream bos = null;

    public Server() {

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
            
            // En Input Stream der lytter til requests fra klienten
            // bis = new BufferedInputStream(clientSocket.getInputStream());

            // Output Stream så vi kan skrive til klienten
            // bos = new BufferedOutputStream(clientSocket.getOutputStream());
        }

        // bis.close();
        // bos.close();
        clientSocket.close();
        mss.close();
    }
}