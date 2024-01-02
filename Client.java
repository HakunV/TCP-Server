import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.net.ServerSocket;

public class Client {
    public static Socket client = null;
    public static String ip = "localhost";
    public static int port = 8081;

    public static void main(String[] args) {
        try {
            client = new Socket(ip, port);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }
}
