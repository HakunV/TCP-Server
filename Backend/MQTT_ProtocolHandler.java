package Backend;

import java.io.IOException;

public class MQTT_ProtocolHandler {
    private Receiver r = null;
    private int byteSize = 2;

    public MQTT_ProtocolHandler(Receiver r) {
        this.r = r;
    }

    public void handleMessage(String str) {
        String protocol = str.substring(0, 1*byteSize/2);

        switch (protocol) {
            case "2":
                System.out.println("Connack Message Received:");
                System.out.println();
                handleConnack(str);
                break;
            case "3":
                System.out.println("Publish Message Received:");
                System.out.println();
                handlePublish(str);
                break;
            case "4":
                System.out.println("Puback Message Received:");
                System.out.println();
                handlePuback(str);
                break;
            case "5":
                System.out.println("Pubrec Message Received:");
                System.out.println();
                handlePubrec(str);
                break;
            case "6":
                System.out.println("Pubrel Message Received:");
                System.out.println();
                handlePubrel(str);
                break;
            case "7":
                System.out.println("Pubcomp Message Received:");
                System.out.println();
                handlePubcomp(str);
                break;
            case "9":
                System.out.println("Suback Message Received:");
                System.out.println();
                handleSuback(str);
                break;
            case "b":
                System.out.println("Unsuback Message Received:");
                System.out.println();
                handleUnSuback(str);
                break;
            case "d":
                System.out.println("Pingresp Message Received:");
                System.out.println();
                handlePingresp(str);
                break;
            default:
                System.out.println("Packet Type Not Recognized");
                System.out.println();
                break;
        }
    }

    private void handlePingresp(String str) {
        System.out.println("The server responded");
        System.out.println();
    }

    private void handleUnSuback(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleUnSuback'");
    }

    private void handleSuback(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleSuback'");
    }

    private void handlePubcomp(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePubcomp'");
    }

    private void handlePubrel(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePubrel'");
    }

    private void handlePubrec(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePubrec'");
    }

    private void handlePuback(String str) {
        int length = recRemLen(str);

        int packetID = Integer.parseInt(str.substring(2*byteSize, 4*byteSize));
        System.out.println("    Packet Identifier: " + packetID);
        System.out.println();

        if (length > 4*byteSize) {
            String reasonCode = str.substring(4*byteSize, 5*byteSize);
            System.out.println("    Reason Code: ");
            switch (reasonCode) {
                case "00":
                    System.out.print("Success");
                    System.out.println();
                    break;
                case "10":
                    System.out.print("No Matchig Subscribers");
                    System.out.println();
                    break;
                case "80":
                    System.out.print("Unspecified Error");
                    System.out.println();
                    break;
                case "83":
                    System.out.print("Implementation Specific Error");
                    System.out.println();
                    break;
                case "87":
                    System.out.print("Not Authorized");
                    System.out.println();
                    break;
                case "90":
                    System.out.print("Topic Name Invalid");
                    System.out.println();
                    break;
                case "91":
                    System.out.print("Packet Identifier In Use");
                    System.out.println();
                    break;
                case "97":
                    System.out.print("Quota Exceeded");
                    System.out.println();
                    break;
                case "99":
                    System.out.print("Payload Format Invalid");
                    System.out.println();
                    break;
                default:
                    System.out.print("Could Not Recognize Reason Code");
                    System.out.println();
                    break;
            }
        }

        if (length > 5*byteSize) {
            int propLength = Integer.parseInt(str.substring(5*byteSize, 6*byteSize), 16);

            if (propLength != 0) {
                System.out.println("    Property: ");

                String property = str.substring(6*byteSize, 7*byteSize) == "1f" ? "Reason String" : "User Property";
                System.out.print(property);
                System.out.println();
            }
        }
    }

    private void handlePublish(String str) {
        String flags = str.substring(1*byteSize/2, 1*byteSize);
        boolean dup = flags.substring(0, 1).equals("0") ? false : true;
        int qos = Integer.parseInt(flags.substring(1, 3));
        boolean retain = flags.substring(3, 4).equals("0") ? false : true;

        int length = recRemLen(str);

        int topicLength = Integer.parseInt(str.substring(3*byteSize, 5*byteSize));

    }

    private void handleConnack(String str) {
        int length = recRemLen(str);

        boolean session = Integer.parseInt(str.substring(2*byteSize, 3*byteSize), 16) == 1 ? true : false;
        System.out.println("    Session Present: " + session);
        System.out.println();

        String returnCode = str.substring(3*byteSize, 4*byteSize);
        System.out.println("    Return Code: ");
        switch (returnCode) {
            case "00":
                r.setConAcc(true);
                r.wakeUp();
                System.out.println("Connection Accepted");
                System.out.println();
                break;
            case "81":
                System.out.print("Malformed Packet");
                System.out.println();
                break;
            case "82":
                System.out.print("Protocol Error");
                System.out.println();
                break;
            case "84":
                System.out.print("Unsupported Protocol Version");
                System.out.println();
                break;
            case "85":
                System.out.print("Client Identifier Not Valid");
                System.out.println();
                break;
            case "86":
                System.out.print("Bad Username Or Password");
                System.out.println();
                break;
            case "95":
                System.out.print("Packet Too Large");
                System.out.println();
                break;
            case "8a":
                System.out.print("Banned");
                System.out.println();
                break;
            default:
                System.out.print("Could Not Recognize Reason Code");
                System.out.println();
                break;
        }
    }

    public int recRemLen(String inLen) {
        int start = 0;
        int end = 2;

        int multiplier = 1;
        int value = 0;

        int digit = Integer.parseInt(inLen.substring(start, end), 16);

        while ((digit & 128) != 0) {
            digit = Integer.parseInt(inLen.substring(start, end), 16);
            value += (digit & 127) * multiplier;
            multiplier *= 128;

            if (inLen.length() > end) {
                start += 2;
                end += 2;
            }
        }

        return value;
    }
}
