import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.net.ServerSocket;

public class Server {
    public static int port = 4321;

	public static ServerSocket mss = null;
	public static Socket clientSocket = null;
	public static BufferedInputStream bis = null;
	public static BufferedOutputStream bos = null;

    public static void main(String[] args) throws IOException {
        boolean serverActive = true;
        int n = 1024;
        int nRead = 0;
        byte[] data = new byte[512];
        String dataString = "";

        while(serverActive) {
            try {
                mss = new ServerSocket(port);
				// Establish connection with the client
				clientSocket = mss.accept();
				System.out.println("Client connected");
                
                // En scanner der lytter til requests fra klienten
                bis = new BufferedInputStream(clientSocket.getInputStream());

                // PrintWriter så vi kan skrive til klienten
                bos = new BufferedOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Client not connected");
			}

            while ((nRead = bis.read(data)) > -1) {
                dataString = byteToHex(data);
                
                System.out.println("Input: " + dataString);
            }

            bis.close();
            bos.close();
            clientSocket.close();
            mss.close();
            serverActive = false;
        }
    }

    private static String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}