import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.net.ServerSocket;

public class Client {
    public static Socket client = null;
    public static String ip = "localhost";
    public static int port = 4321;
    public static PrintWriter pw = null;

    public static void main(String[] args) {
        try {
            client = new Socket(ip, port);
            pw = new PrintWriter(client.getOutputStream(), true);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String line = "Hello";

        pw.println(line);
        pw.flush();
        pw.close();
    }
}
