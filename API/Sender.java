package API;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class Sender {
    private GetInfo gi = null;
    private BufferedOutputStream bos = null;

    public Sender(GetInfo gi, BufferedOutputStream bos) {
        this.gi = gi;
        this.bos = bos;
    }

    public void sendGET() {
        String mes = "";
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
}