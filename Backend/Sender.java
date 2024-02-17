package Backend;

import java.io.*;

public class Sender {
    private BackendClient bc = null;
    private BufferedOutputStream bos = null;

    private boolean pubDup = false;

    public Sender(BackendClient bc, BufferedOutputStream bos) {
        this.bc = bc;
        this.bos = bos;
    }

    public void sendConnect() {
        String tempMes = "";
        String message = "";
        
        int fixed = Integer.parseInt("00010000", 2);
        message += String.format("%02X", fixed);

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
        message += String.format("%02X", mesLength);
        message += tempMes;

        try {
            sendMessage(message, bos);
        }
        catch (IOException e) {
            System.out.println("Could Not Send Connect Packet");
            e.printStackTrace();
        }
    }

    public void sendPublish() {
        String message = "";
        String tempMes = "";

        // Fixed Header

        String packetType = String.format("%01X", 3);
        message += packetType;

        String dup = pubDup ? "1" : "0";
        String qos = "00";
        String retain = "1";
        message += String.format("%01X", Integer.parseInt(dup+qos+retain, 2));

        // Variable Header

        String topic = "DTU-IWP-DeviceData";
        String topicHex = textToHex(topic);

        int topicLength = topic.length();
        tempMes += String.format("%04X", topicLength);
        tempMes += topicHex;

        String packetID = "";
        if (Integer.parseInt(qos) > 0) {
            packetID = "24583";
            tempMes += String.format("%04X", Integer.parseInt(packetID));
        }

        // Payload

        String payload = "";
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
