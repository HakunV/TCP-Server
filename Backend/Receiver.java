package Backend;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Receiver implements Runnable {
    private BufferedInputStream bis;

    private boolean running = true;

    public Receiver(BufferedInputStream bis) {
        this.bis = bis;
    }

    public void run() {
        byte[] dataT = new byte[512];
        int nRead = 0;
        String dataString = "";

        try {
            while (running) {
                while ((nRead = bis.read(dataT)) != -1) {
                    byte[] data = byteCutoff(dataT, nRead);

                    System.out.println("Input Before Hex: " + dataString);
                    System.out.println();

                    dataString = byteToHex(data);
                    dataString = removeWhiteSpace(dataString);

                    System.out.println("Input After Hex: " + dataString);
                    System.out.println();
                }
            }
        }
        catch (IOException e) {
            System.out.println("Failed To Read the Bis");
            e.printStackTrace();
        }
    }

    public byte[] byteCutoff(byte[] dataT, int nRead) {
        byte[] d = new byte[nRead];

        for (int i = 0; i < nRead; i++) {
            d[i] = dataT[i];
        }
        return d;
    }

    public String removeWhiteSpace(String in) {
        String out = "";
 
        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
 
            // Checking whether is white space or not
            if (!Character.isWhitespace(ch)) {
                out += ch;
            }
        }
        return out;
    }

    public String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
