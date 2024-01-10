import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
    public int port = 6666;

	public ServerSocket mss = null;
	public Socket clientSocket = null;
	public BufferedInputStream bis = null;
	public BufferedOutputStream bos = null;

    public int byteSize = 2;

    public void run() throws IOException {
        boolean serverActive = true;
        boolean sameCon = true;
        int n = 1024;
        int nRead = 0;
        byte[] dataT = new byte[n];
        String dataString = "";
        int packetLength = 0;
        String protocolNum = "";
        String isn = "";  // Might change to int
        String errCheck = "";

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

            while (sameCon) {
                nRead = bis.read(dataT);
                byte[] data = byteCutoff(dataT, nRead);
                dataString = byteToHex(data);

                dataString = removeWhiteSpace(dataString);

                int len = dataString.length();

                System.out.println("Input: " + dataString);

                if (dataString.substring(0, 2*byteSize).equals("7878")) {
                    packetLength = Integer.parseInt(dataString.substring(2*byteSize, 3*byteSize), 16);
                    System.out.println(packetLength);

                    protocolNum = dataString.substring(3*byteSize, 4*byteSize);

                    // isn = Integer.parseInt(dataString.substring(len-6*byteSize, len-4*byteSize), 16);   // When isn is of type int
                    isn = dataString.substring(len-6*byteSize, len-4*byteSize);

                    errCheck = dataString.substring(len-4*byteSize, len-2*byteSize);

                    System.out.println(errorCheck(dataString.substring(4, len-4*byteSize), errCheck));

                    switch (protocolNum) {
                        case "01":
                            System.out.println("Login Message");
                            handleLogin(dataString, isn);
                            break;
                        case "22":
                            break;
                        case "12":
                            break;
                        case "13":
                            System.out.println("Status Message");
                            handleStatus(dataString, isn);
                            break;
                        case "15":
                            break;
                        case "26":
                            break;
                        case "16":
                            break;
                        case "80":
                            break;
                        case "f3":
                            break;
                        case "f1":
                            break;
                        case "f2":
                            break;
                        case "8a":
                            break;
                        default:
                            System.out.println("No such command");
                            break;
                    }
                }
                else {
                    System.out.println("Wrong start");
                }
            }
            serverActive = false;
        }

        bis.close();
        bos.close();
        clientSocket.close();
        mss.close();
    }

    private void handleStatus(String d, String isn) {
        String tic = d.substring(4*byteSize, 5*byteSize);
        String volLevel = d.substring(5*byteSize, 6*byteSize);
        String sigStrength = d.substring(6*byteSize, 7*byteSize);
        String alarm = d.substring(7*byteSize, 8*byteSize);
        String language = d.substring(8*byteSize, 9*byteSize);
        
        try {
            respondToStatus(isn);
        } catch(IOException e) {
            System.out.println("Fail to send Status");
        }
    }

    private void respondToStatus(String isn) throws IOException {
        String respond = "";

        String protNum = "13";
        String serialNum = isn;
        int packLenInt = (protNum.length() + serialNum.length())/2 + 2;
        String packLenStr = String.format("%02X", packLenInt);
        
        respond = packLenStr + protNum + serialNum;
        String crc = crcCalc(respond);
        respond += crc;

        respond = addStartEnd(respond);

        byte[] bArr = hexStrToByteArr(respond);

        bos.write(bArr);
        bos.flush();
    }

    private void handleLogin(String d, String isn) {
        String IMEI = d.substring(4*byteSize, 12*byteSize);
        String typeID = d.substring(12*byteSize, 14*byteSize);
        String timeZone = d.substring(14*byteSize, 16*byteSize);

        try {
            respondToLogin(isn);
        } catch(IOException e) {
            System.out.println("Fail to send Login");
        }
    }

    private void respondToLogin(String isn) throws IOException {
        String respond = "";

        String protNum = "01";
        String serialNum = isn;
        int packLenInt = (protNum.length() + serialNum.length())/2 + 2;
        String packLenStr = String.format("%02X", packLenInt);

        respond = packLenStr + protNum + serialNum;
        String crc = crcCalc(respond);
        respond += crc;

        respond = addStartEnd(respond);

        byte[] bArr = hexStrToByteArr(respond);

        bos.write(bArr);
        bos.flush();
    }

    private String addStartEnd(String str) {
        return "7878" + str + "0d0a";
    }

    private byte[] byteCutoff(byte[] dataT, int nRead) {
        byte[] d = new byte[nRead];

        for (int i = 0; i < nRead; i++) {
            d[i] = dataT[i];
        }
        return d;
    }

    private String removeWhiteSpace(String in) {
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

    private String byteToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    private byte[] hexStrToByteArr(String data) {
        int len = data.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                                + Character.digit(data.charAt(i+1), 16));
        }
        return bytes;
    }

    private String crcCalc(String data) {
        byte[] dataArr = hexStrToByteArr(data);
        CRC_Table crcObj = new CRC_Table();
        return crcObj.getCRC(dataArr);
    }

    private boolean errorCheck(String data, String comp) {
        String res = crcCalc(data);
        return res.equalsIgnoreCase(comp);
    }
}