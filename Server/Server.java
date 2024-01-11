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

                    switch (protocolNum) {
                        case "01":
                            System.out.println("Login Message");
                            System.out.println();
                            handleLogin(dataString, isn);
                            break;
                        case "22":
                            System.out.println("Location Message");
                            System.out.println();
                            handleLocation(dataString);
                            break;
                        case "12":
                            break;
                        case "13":
                            System.out.println("Status Message");
                            System.out.println();
                            handleStatus(dataString, isn);
                            sendCommand(isn);
                            break;
                        case "15":
                            System.out.println("Command Response");
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

    private void handleLocation(String d) {
        String dateAndTime = d.substring(4*byteSize, 10*byteSize);
        System.out.println("Date and Time:");
        checkDate(dateAndTime);

        String gpsQual = d.substring(10*byteSize, 11*byteSize);
        System.out.println("Quality of GPS:");
        checkGPS(gpsQual);

        String latitude = d.substring(11*byteSize, 15*byteSize);
        System.out.println("Latitude:");
        checkLat(latitude);

        String longitude = d.substring(15*byteSize, 19*byteSize);
        System.out.println("Longitude:");
        checkLong(longitude);

        String speed = d.substring(19*byteSize, 20*byteSize);
        System.out.println("Speed:");
        checkSpeed(speed);

        String course = d.substring(20*byteSize, 22*byteSize);
        System.out.println("Course and Status:");
        checkCourse(course);

        String mcc = d.substring(22*byteSize, 24*byteSize);
        System.out.println("Mobile Country Code:");
        checkMcc(mcc);

        String mnc = d.substring(24*byteSize, 25*byteSize);
        System.out.println("Mobile Network Code:");
        checkMnc(mnc);

        String lac = d.substring(25*byteSize, 27*byteSize);
        System.out.println("Location Area Code:");
        checkLac(lac);

        String cellID = d.substring(27*byteSize, 30*byteSize);
        System.out.println("Cell ID:");
        checkCell(cellID);

        String acc = d.substring(30*byteSize, 31*byteSize);
        System.out.println("ACC:");
        checkAcc(acc);

        String escalation = d.substring(31*byteSize, 32*byteSize);
        System.out.println("Data Escalation Mode:");
        checkEsc(escalation);

        String realTime = d.substring(32*byteSize, 33*byteSize);
        System.out.println("GPS Real-time Retransmission:");
        checkReal(realTime);
    }

    private void checkCourse(String course) {
        int courseInt = Integer.parseInt(course, 16);
        String c = String.format("%16s", Integer.toBinaryString(courseInt)).replace(" ", "0");

        if (c.substring(0, 1).equals("1")) {
            System.out.println("    ACC: On");
            System.out.println();
        }
        else {
            System.out.println("    ACC: Off");
            System.out.println();
        }

        if (c.substring(1, 2).equals("1")) {
            System.out.println("    Input2: On");
            System.out.println();
        }
        else {
            System.out.println("    Input2: Off");
            System.out.println();
        }

        if (c.substring(2, 3).equals("1")) {
            System.out.println("    GPS: Real-time");
            System.out.println();
        }
        else {
            System.out.println("    GPS: Differential Positioning");
            System.out.println();
        }

        if (c.substring(3, 4).equals("1")) {
            System.out.println("    GPS: Positioned");
            System.out.println();
        }
        else {
            System.out.println("    GPS: Not Positioned");
            System.out.println();
        }

        if (c.substring(4, 5).equals("1")) {
            System.out.println("    Longitude: West");
            System.out.println();
        }
        else {
            System.out.println("    Longitude: East");
            System.out.println();
        }

        if (c.substring(5, 6).equals("1")) {
            System.out.println("    Latitude: North");
            System.out.println();
        }
        else {
            System.out.println("    Latitude: South");
            System.out.println();
        }

        int courseDegrees = Integer.parseInt(c.substring(6), 2);
        System.out.println("    Course In Degrees: " + courseDegrees);
        System.out.println();
    }

    private void checkReal(String realTime) {
        if (realTime.equals("01")) {
            System.out.println("    Differential Positioning Upload");
            System.out.println();
        }
        else {
            System.out.println("    Not Known");
            System.out.println();
        }
    }

    private void checkEsc(String escalation) {
        if (escalation.equals("00")) {
            System.out.println("    Timed Upload");
            System.out.println();
        }
        else {
            System.out.println("    Not Known");
            System.out.println();
        }
    }

    private void checkAcc(String acc) {
        if (acc.equals("00")) {
            System.out.println("    ACC is Off");
            System.out.println();
        }
        else {
            System.out.println("    Not Known");
            System.out.println();
        }
    }

    private void checkCell(String cellID) {
        int c = Integer.parseInt(cellID, 16);

        System.out.println("    " + c);
        System.out.println();
    }

    private void checkLac(String lac) {
        int l = Integer.parseInt(lac, 16);

        System.out.println("    " + l);
        System.out.println();
    }

    private void checkMnc(String mnc) {
        int m = Integer.parseInt(mnc, 16);

        System.out.println("    " + m);
        System.out.println();
    }

    private void checkMcc(String mcc) {
        int m = Integer.parseInt(mcc, 16);

        System.out.println("    " + m);
        System.out.println();
    }

    private void checkSpeed(String speed) {
        int s = Integer.parseInt(speed, 16);

        System.out.println("    " + s + " km/h");
        System.out.println();
    }

    private void checkLong(String longitude) {
        int longInt = Integer.parseInt(longitude, 16);

        float longFloat = ((float) 180 / (float) 324000000)*longInt;

        System.out.println("    " + longFloat);
        System.out.println();
    }

    private void checkLat(String latitude) {
        int latInt = Integer.parseInt(latitude, 16);

        float latDouble = ((float) 90 / (float) 162000000)*latInt;

        System.out.println("    " + latDouble);
        System.out.println();
    }

    private void checkGPS(String str) {
        int gpsBits = Integer.parseInt(str.substring(0, 1), 16);
        System.out.println("    Bits of GPS Info: " + gpsBits);
        System.out.println();

        int sat = Integer.parseInt(str.substring(1), 16);
        System.out.println("    Satelittes connected: " + sat);
        System.out.println();
    }

    private void checkDate(String dateAndTime) {
        int year = Integer.parseInt(dateAndTime.substring(0, 1*byteSize), 16);
        System.out.print("    20" + year);

        int month = Integer.parseInt(dateAndTime.substring(1*byteSize, 2*byteSize), 16);
        System.out.print("." + month);

        int day = Integer.parseInt(dateAndTime.substring(2*byteSize, 3*byteSize), 16);
        System.out.print("." + day);

        int hour = Integer.parseInt(dateAndTime.substring(3*byteSize, 4*byteSize), 16);
        System.out.print("   " + hour);

        int minute = Integer.parseInt(dateAndTime.substring(4*byteSize, 5*byteSize), 16);
        System.out.print("." + minute);

        int second = Integer.parseInt(dateAndTime.substring(5*byteSize), 16);
        System.out.print("." + second);
        System.out.println();
        System.out.println();
    }

    private void sendCommand(String isn) throws IOException {
        String respond = "";

        String protNum = "80";
        String serverFlags = "00000001";
        String command = "54494D45522C313023";
        String language = "0002";

        int isnInt = Integer.parseInt(isn, 16);
        String serNum = String.format("%04X", isnInt+1);

        int commandLen = command.length()/2;
        String comLenStr = String.format("%02X", commandLen);

        respond = protNum + comLenStr + serverFlags + command + language + serNum;

        int packLenInt = respond.length()/2+2;
        String packLenStr = String.format("%02X", packLenInt);

        respond = packLenStr + respond;
        String crc = crcCalc(respond);
        respond += crc;

        respond = addStartEnd(respond);

        byte[] bArr = hexStrToByteArr(respond);

        bos.write(bArr);
        bos.flush();
    }

    private void handleStatus(String d, String isn) {
        String tic = d.substring(4*byteSize, 5*byteSize);
        System.out.println("Terminal Information Content:");
        checkTic(tic);

        String volLevel = d.substring(5*byteSize, 6*byteSize);
        System.out.println("Voltage Level:");
        checkVol(volLevel);

        String sigStrength = d.substring(6*byteSize, 7*byteSize);
        System.out.println("Signal Strength:");
        checkSig(sigStrength);

        String alarm = d.substring(7*byteSize, 8*byteSize);
        System.out.println("Alarm:");
        checkAlarm(alarm);

        String language = d.substring(8*byteSize, 9*byteSize);
        System.out.println("Language:");
        checkLanguage(language);
        
        try {
            respondToStatus(isn);
        } catch(IOException e) {
            System.out.println("Fail to send Status");
        }
    }

    private void checkTic(String tic) {
        int ticInt = Integer.parseInt(tic, 16);
        String t = String.format("%8s", ticInt).replace(" ", "0");

        if (t.substring(0, 1).equals("1")) {
            System.out.println("    Oil and Electricity: Disconnected");
            System.out.println();
        }
        else {
            System.out.println("    Oil and Electricity: Connected");
            System.out.println();
        }

        if (t.substring(1, 2).equals("1")) {
            System.out.println("    GPS Tracking: On");
            System.out.println();
        }
        else {
            System.out.println("    GPS Tracking: Off");
            System.out.println();
        }

        System.out.println("    Alarm Status");
        switch (t.substring(4, 5) + t.substring(3, 4) + t.substring(2, 3)) {
            case "000":
                System.out.print(" Normal");
                System.out.println();
                break;
            case "001":
                System.out.print("Shock Alarm");
                System.out.println();
                break;
            case "010":
                System.out.print(" Power Cut Alarm");
                System.out.println();
                break;
            case "011":
                System.out.print(" Low Battery Alarm");
                System.out.println();
                break;
            case "100":
                System.out.print(" SOS");
                System.out.println();
                break;
            default:
                System.out.print(" Not known");
                System.out.println();
                break;
        }

        if (t.substring(5, 6).equals("1")) {
            System.out.println("    Charge: On");
            System.out.println();
        }
        else {
            System.out.println("    Charge: Off");
            System.out.println();
        }

        if (t.substring(6, 7).equals("1")) {
            System.out.println("    ACC: High");
            System.out.println();
        }
        else {
            System.out.println("    ACC: Low");
            System.out.println();
        }

        if (t.substring(7).equals("1")) {
            System.out.println("    Air Condition: On");
            System.out.println();
        }
        else {
            System.out.println("Air Condition: Off");
            System.out.println();
        }
    }

    private void checkLanguage(String language) {
        switch (language) {
            case "01":
                System.out.println("    Language is Chinese");
                System.out.println();
                break;
            case "02":
                System.out.println("    Language is English");
                System.out.println();
                break;
            default:
                System.out.println("    Not known");
                break;
        }
    }

    private void checkAlarm(String alarm) {
        int a = Integer.parseInt(alarm, 16);

        switch (a) {
            case 0:
                System.out.println("    Normal");
                System.out.println();
                break;
            case 1:
                System.out.println("    SOS");
                System.out.println();
                break;
            case 2:
                System.out.println("    Power Cut Alarm");
                System.out.println();
                break;
            case 3:
                System.out.println("    Shock Alarm");
                System.out.println();
                break;
            case 4:
                System.out.println("    Fence In Alarm");
                System.out.println();
                break;
            case 5:
                System.out.println("    Fence Out Alarm");
                System.out.println();
                break;
            default:
                System.out.println("    Not known");
                break;
        }
    }

    private void checkVol(String volLevel) {
        int vol = Integer.parseInt(volLevel, 16);

        switch (vol) {
            case 0:
                System.out.println("    No power");
                System.out.println();
                break;
            case 1:
                System.out.println("    Extremely low power");
                System.out.println();
                break;
            case 2:
                System.out.println("    Very low battery");
                System.out.println();
                break;
            case 3:
                System.out.println("    Low battery");
                System.out.println();
                break;
            case 4:
                System.out.println("    Medium");
                System.out.println();
                break;
            case 5:
                System.out.println("    High");
                System.out.println();
                break;
            case 6:
                System.out.println("    Very high");
                System.out.println();
                break;
            default:
                System.out.println("    Not known");
                break;
        }
    }

    private void checkSig(String sig) {
        int s = Integer.parseInt(sig, 16);

        switch (s) {
            case 0:
                System.out.println("    No signal");
                System.out.println();
                break;
            case 1:
                System.out.println("    Extremely weak signal");
                System.out.println();
                break;
            case 2:
                System.out.println("    Very weak signal");
                System.out.println();
                break;
            case 3:
                System.out.println("    Good signal");
                System.out.println();
                break;
            case 4:
                System.out.println("    Strong signal");
                System.out.println();
                break;
            default:
                System.out.println("    Not known");
                break;
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
        String imei = d.substring(4*byteSize, 12*byteSize);
        imei = removeProZeros(imei);
        System.out.println("IMEI number: " + imei);
        System.out.println();

        String typeID = d.substring(12*byteSize, 14*byteSize);
        System.out.println("Type ID: " + typeID);
        System.out.println();

        String timeZone = d.substring(14*byteSize, 16*byteSize);
        System.out.println("Time zone: " + timeZone);
        System.out.println();

        try {
            respondToLogin(isn);
        } catch(IOException e) {
            System.out.println("Fail to send Login");
        }
    }

    private String removeProZeros(String imei) {
        int i = 0;
        while(imei.charAt(i) == '0') {
            i += 1;
        }

        return imei.substring(i);
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