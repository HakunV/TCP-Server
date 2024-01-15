package Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    public Server server;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private ProtocolHandler ph;
    private ClientWriter cw;
    private String imei = "";

    public String isn = "0000";  // Might change to int

    public int byteSize = 2;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        boolean sameCon = true;
        int n = 1024;
        int nRead = 0;
        byte[] dataT = new byte[n];
        String dataString = "";
        int packetLength = 0;
        String protocolNum = "";
        String errCheck = "";

        try {
            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());

            this.ph = new ProtocolHandler(this);
            this.cw = new ClientWriter(this, bos);
        } catch (IOException e) {
            System.out.println("Wrong when setting up");
            e.printStackTrace();
        }

        try {
            while (sameCon) {
                while ((nRead = bis.read(dataT)) != -1) {
                    byte[] data = byteCutoff(dataT, nRead);
                    dataString = byteToHex(data);

                    dataString = removeWhiteSpace(dataString);

                    int len = dataString.length();

                    System.out.println("Input: " + dataString);
                    System.out.println();

                    if (dataString.substring(0, 2*byteSize).equals("7878")) {
                        packetLength = Integer.parseInt(dataString.substring(2*byteSize, 3*byteSize), 16);
                        System.out.println("Length of the packet: " + packetLength);
                        System.out.println();

                        protocolNum = dataString.substring(3*byteSize, 4*byteSize);

                        // isn = Integer.parseInt(dataString.substring(len-6*byteSize, len-4*byteSize), 16);   // When isn is of type int
                        isn = dataString.substring(len-6*byteSize, len-4*byteSize);
                        System.out.println("Information Serial Number: " + isn);
                        System.out.println();

                        errCheck = dataString.substring(len-4*byteSize, len-2*byteSize);

                        System.out.println(errorCheck(dataString.substring(4, len-4*byteSize), errCheck));
                        System.out.println();

                        ph.handleProtocol(protocolNum, dataString);
                    }
                    else {
                        System.out.println("Wrong start");
                    }
                    if (Thread.interrupted()) {
                        throw new IOException();
                    }
                }
                if (Thread.interrupted()) {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            System.out.println("Reading from bis is failing");

            try {
                bis.close();
                bos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            cw.closeWriter();
            server.removeClient(this);

            e.printStackTrace();
        }
    }

    public void respondToStatus(String isn) throws IOException {
        cw.respondStandard("13", isn);
    }

    public void respondToLogin(String isn) throws IOException {
        cw.respondStandard("01", isn);
        // cw.sendCommand("3C535042534A2A503A42534A4750532A33503A303E");
    }

    public void respondToAlarm(String isn) throws IOException {
        cw.respondStandard("26", isn);
    }

    public void setName(String name) {
        this.imei = name;
        cw.setWindowName(name);
    }

    public String getImei() {
        return this.imei;
    }

    public void checkDups() {
        server.removeDups(this.imei, this);;
    }

    public void disconnectSelf() {
        Thread.currentThread().interrupt();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String removeProZeros(String imei) {
        int i = 0;
        while(imei.charAt(i) == '0') {
            i += 1;
        }

        return imei.substring(i);
    }

    public String addStartEnd(String str) {
        return "7878" + str + "0d0a";
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

    public byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }

    public String crcCalc(String data) {
        byte[] dataArr = hexStrToByteArr(data);
        CRC_Table crcObj = new CRC_Table();
        return crcObj.getCRC(dataArr);
    }

    public boolean errorCheck(String data, String comp) {
        String res = crcCalc(data);
        return res.equalsIgnoreCase(comp);
    }
    
}