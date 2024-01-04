import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.util.*;

public class Client {
    public static Socket client = null;
    public static String ip = "10.0.0.5";
    public static int port = 4321;
    public static BufferedInputStream bis = null;
    public static BufferedOutputStream bos = null;
    public static Scanner scan = null;

    public static void main(String[] args) throws IOException{
        boolean active = true;

        try {
            client = new Socket(ip, port);
            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());
            scan = new Scanner(System.in);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (active) {
            System.out.println("Write data");

            String data = scan.nextLine();

            System.out.println("Your input: " + data);
            System.out.println("Your input: " + data.toString());

            byte[] b = hexStrToByteArr(data);

            bos.write(b);
            bos.flush();
            bos.close();
        }

        
    }

    private static byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }
}
