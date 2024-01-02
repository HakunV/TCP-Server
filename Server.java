import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.net.ServerSocket;

public class Server {

	public ServerSocket mss = null;
	public Socket clientSocket = null;
	public Scanner netIn = null;
	public PrintWriter pw = null;

    public void runServer() {
        boolean serverActive = true;

        while(serverActive) {
            try {
				// Establish connection with the client
				clientSocket = mss.accept();
				System.out.println("Client connected");
                
                // En scanner der lytter til requests fra klienten
                netIn = new Scanner(clientSocket.getInputStream());
                // PrintWriter så vi kan skrive til klienten
                pw = new PrintWriter(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Client not connected");
			}

            while (netIn.hasNextLine()) {
                String line = netIn.nextLine();
                System.out.println(line);
                
            }
        }
    }
}