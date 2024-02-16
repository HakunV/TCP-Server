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
            // ArrayList<Integer> mes = new ArrayList<Integer>();
            String tempMes = "";
            String response = "";
            
            int fixed = Integer.parseInt("00010000", 2);
            // tempMes.add(fixed);
            response += String.format("%02X", fixed);

            // int lenMSB = Integer.parseInt("00000000", 2);
            // // tempMes.add(msb);
            // tempMes += String.format("%02X", lenMSB);

            // int lenLSB = Integer.parseInt("00110000", 2);
            // // tempMes.add(lsb);
            // tempMes += String.format("%02X", lenLSB);

            int protLen = 4;
            tempMes += String.format("%04X", protLen);

            int m = 'M';
            // tempMes.add(m);
            tempMes += String.format("%02X", m);

            int q = 'Q';
            // tempMes.add(q);
            tempMes += String.format("%02X", q);

            int t = 'T';
            // tempMes.add(t);
            // tempMes.add(t);
            tempMes += String.format("%02X", t);
            tempMes += String.format("%02X", t);

            int level = Integer.parseInt("00000100", 2);
            // tempMes.add(level);
            tempMes += String.format("%02X", level);

            int flags = Integer.parseInt("10000010", 2);
            // tempMes.add(flags);
            tempMes += String.format("%02X", flags);

            int aliveMSB = Integer.parseInt("00000000", 2);
            tempMes += String.format("%02X", aliveMSB);

            int aliveLSB = Integer.parseInt("00001010", 2);
            tempMes += String.format("%02X", aliveLSB);
            
            // Payload

            String client_ID = "8xQ6SK";
            String id_hex = textToHex(client_ID);

            int lengthIDInt = id_hex.length()/2;
            String lengthID = String.format("%04X", lengthIDInt);

            tempMes += lengthID;
            tempMes += id_hex;

            String username = "6a2b0454-7bcb-46eb-8e77-37005d22d72c";
            String userHex = textToHex(username);
            
            int lengthUserInt = userHex.length()/2;
            String lengthUser = String.format("%04X", lengthUserInt);

            tempMes += lengthUser;
            tempMes += userHex;

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
