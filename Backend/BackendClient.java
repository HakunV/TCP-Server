package Backend;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class BackendClient {
    private Socket client = null;
    private String ip = "thingsofinter.net";
    private int port = 1883;

    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    private Receiver r = null;

    private boolean active = true;

    public BackendClient() {
        try {
            client = new Socket(ip, port);
            System.out.println("Connected to DTU");

            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());

            r = new Receiver(bis);
            new Thread(r).start();
        }
        catch(IOException e) {
            System.out.println("Could not connect to DTU");
            e.printStackTrace();
        }
    }

    public void runClient() throws IOException {
        while (active) {
            String tempMes = "";
            String response = "";
            
            int fixed = Integer.parseInt("00010000", 2);
            response += String.format("%02X", fixed);

            int protLen = 4;
            tempMes += String.format("%04X", protLen);

            int m = 'M';
            tempMes += String.format("%02X", m);

            int q = 'Q';
            tempMes += String.format("%02X", q);

            int t = 'T';
            tempMes += String.format("%02X", t);
            tempMes += String.format("%02X", t);

            int level = Integer.parseInt("00000100", 2);
            tempMes += String.format("%02X", level);

            int flags = Integer.parseInt("11000010", 2);
            tempMes += String.format("%02X", flags);

            int aliveMSB = Integer.parseInt("00000000", 2);
            tempMes += String.format("%02X", aliveMSB);

            int aliveLSB = Integer.parseInt("00001010", 2);
            tempMes += String.format("%02X", aliveLSB);
            
            // Payload

            String client_ID = "8xQ6SK";
            String id_hex = textToHex(client_ID);

            String lengthID = String.format("%04X", client_ID);

            tempMes += lengthID;
            tempMes += id_hex;

            String username = "dtuadmin";
            String userHex = textToHex(username);
            
            String lengthUser = String.format("%04X", username.length());

            tempMes += lengthUser;
            tempMes += userHex;

            String password = "$admiN@DTU#8024";
            String passHex = textToHex(password);

            String lengthPass = String.format("%04X", password.length());

            tempMes += lengthPass;
            tempMes += passHex;

            int mesLength = tempMes.length()/2;
            response += String.format("%02X", mesLength);
            response += tempMes;

            sendMessage(response, bos);
            this.active = false;
        }
    }

    public void sendMessage(String mes, BufferedOutputStream bos) throws IOException {
        System.out.println("Message: " + mes);

        byte[] mesBytes = hexStrToByteArr(mes);

        bos.write(mesBytes);
        bos.flush();

        System.out.println("Sent packet");
    }

    public String textToHex(String s) {
        char[] userArr = s.toCharArray();
        String userHex = "";
        for (int i = 0; i < userArr.length; i++) {
            userHex += String.format("%02X", (int) userArr[i]);
        }
        return userHex;
    }

    public byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }
}
