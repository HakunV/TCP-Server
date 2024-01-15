package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ClientDummy {
    public static Socket client = null;
    public static String ip = "localhost";
    public static int port = 6666;
    public static BufferedInputStream bis = null;
    public static BufferedOutputStream bos = null;
    public static Scanner scan = null;

    public static Receiver r;

    public static void main(String[] args) throws IOException{
        boolean active = true;

        try {
            client = new Socket(ip, port);
            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());
            scan = new Scanner(System.in);

            r = new Receiver(bis);
            new Thread(r).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (active) {
            System.out.println("Write data");

            String data = scan.nextLine();

            System.out.println("Your input: " + data);

            byte[] b = hexStrToByteArr(data);

            bos.write(b);
            bos.flush();
        }
        bos.close();
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
