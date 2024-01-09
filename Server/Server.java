import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.net.ServerSocket;

public class Server {
    public static int port = 4321;

	public static ServerSocket mss = null;
	public static Socket clientSocket = null;
	public static BufferedInputStream bis = null;
	public static BufferedOutputStream bos = null;

    public static void main(String[] args) throws IOException {
        boolean serverActive = true;
        int n = 1024;
        int nRead = 0;
        byte[] dataT = new byte[n];
        String dataString = "";
        int packetLength = 0;
        String protocolNum = "";

        while(serverActive) {
            try {
                mss = new ServerSocket(port);
				// Establish connection with the client
				clientSocket = mss.accept();
				System.out.println("Client connected");
                
                // En scanner der lytter til requests fra klienten
                bis = new BufferedInputStream(clientSocket.getInputStream());

                // PrintWriter så vi kan skrive til klienten
                bos = new BufferedOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Client not connected");
			}

            while ((nRead = bis.read(dataT)) > -1) {
                byte[] data = byteCutoff(dataT, nRead);
                dataString = byteToHex(data);

                dataString = removeWhiteSpace(dataString);

                System.out.println("Input: " + dataString);

                packetLength = Integer.parseInt(dataString.substring(4, 6), 16);
                System.out.println(packetLength);

                protocolNum = dataString.substring(6, 8);

                switch (protocolNum) {
                    case "01":
                        System.out.println("Login Message");
                        handleLogin();
                        break;
                    case "22":
                        break;
                    case "12":
                        break;
                    case "13":
                        System.out.println("Status Message");
                        handleStatus();
                        break;
                    case "15":
                        break;
                    case "26":
                        break;
                    case "16":
                        break;
                    case "80":
                        break;
                    case "F3":
                        break;
                    case "F1":
                        break;
                    case "F2":
                        break;
                    case "8A":
                        break;
                    default:
                        System.out.println("No such command");
                        break;
                }
            }
            serverActive = false;
        }

        bis.close();
        bos.close();
        clientSocket.close();
        mss.close();
    }

    private static void handleStatus() {
        try {
            respondToStatus();
        } catch(IOException e) {
            System.out.println("Fail to send Status");
        }
    }

    private static void respondToStatus() throws IOException {
        String respond = "787805130003D9DF0D0A";

        byte[] bArr = hexStrToByteArr(respond);

        bos.write(bArr);
        bos.flush();
    }

    private static void handleLogin() {
        try {
            respondToLogin();
        } catch(IOException e) {
            System.out.println("Fail to send Login");
        }
    }

    private static void respondToLogin() throws IOException {
        String respond = "787805010003D9DF0D0A";

        byte[] bArr = hexStrToByteArr(respond);

        bos.write(bArr);
        bos.flush();
    }

    private static byte[] byteCutoff(byte[] dataT, int nRead) {
        byte[] d = new byte[nRead];

        for (int i = 0; i < nRead; i++) {
            d[i] = dataT[i];
        }
        return d;
    }

    private static String removeWhiteSpace(String in) {
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

    private static String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
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