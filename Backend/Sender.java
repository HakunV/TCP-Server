package Backend;

import java.io.*;
import java.math.BigInteger;

import com.google.gson.Gson;

public class Sender {
    private BackendClient bc = null;
    private BufferedOutputStream bos = null;
    private MQTT_PubPayload mpp = null;
    private WiFi_Config wc = null;

    private String nothingImportant = "6a2b0454-7bcb-46eb-8e77-37005d22d72c";

    private boolean pubDup = false;

    public Sender(BackendClient bc, BufferedOutputStream bos) {
        this.bc = bc;
        this.bos = bos;
        this.mpp = new MQTT_PubPayload();
        this.wc = new WiFi_Config();
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

        String client_ID = "43235";
        String id_hex = textToHex(client_ID);

        String lengthID = String.format("%04X", client_ID.length());

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

    public void sendPublish(double lat, double lon) {
        String message = "";
        String tempMes = "";

        // Fixed Header

        String packetType = String.format("%01X", 3);
        message += packetType;

        String dup = pubDup ? "1" : "0";
        String qos = "01";
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

        String payload = getJSON(lat, lon);
        String payloadHex = textToHex(payload);

        tempMes += payloadHex;

        int mesLength = tempMes.length()/2;
        System.out.println();
        System.out.println("Remaining Length: " + mesLength);
        message += calcRemLen(mesLength);
        message += tempMes;

        try {
            sendMessage(message, bos);
        }
        catch (IOException e) {
            System.out.println("Could Not Send Publish Packet Packet");
            e.printStackTrace();
        }
    }

    /*
     * The wifi_config elements are as follows:
     * [0]: IPv4 address
     * [1]: Gateway IPv4 address
     * [2]: SSID
     * [3]: BSSID
     * [4]: Channel
     * [5]: Signal aka RSSI
     */
    public String getJSON(double lat, double lon) {
        String[] wifi_config = wc.configure();

        mpp.setName("W15");
        mpp.setMAC("8xQ6SK");
        mpp.setTechnology("wifi");
        mpp.setIP(wifi_config[0]);
        mpp.setRssi(Integer.parseInt(wifi_config[5]));
        mpp.setSsid(wifi_config[2]);
        mpp.setHost("192.38.81.6");
        mpp.setGwIP(wifi_config[1]);
        mpp.setBSSID(wifi_config[3]);
        mpp.setChannel(Integer.parseInt(wifi_config[4]));
        mpp.setData(lat, lon);
        mpp.setAuthToken(nothingImportant);

        Gson gson = new Gson();
        String json = gson.toJson(mpp);

        System.out.println(json);
        return json;
    }

    public void sendMessage(String mes, BufferedOutputStream bos) throws IOException {
        System.out.println("Message: " + mes);

        byte[] mesBytes = hexStrToByteArr(mes);
        // byte[] mesBytes = mes.getBytes();

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

    public String calcRemLen(int x) {
        int xn = x;
        String res = "";

        while (xn > 0) {
            int digit = xn % 128;
            xn = xn / 128;
            if (xn > 0) {
                digit = digit | 128;
            }
            res += String.format("%02X", digit);
        }
        return res;
    }
}
