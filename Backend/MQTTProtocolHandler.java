package Backend;

import java.io.IOException;

public class MQTTProtocolHandler {
    private BackendClient bc = null;

    public MQTTProtocolHandler(BackendClient bc) {
        this.bc = bc;
    }

    public void handleMessage(String str) {
        String protocol = str.substring(0, 2);

        switch (protocol) {
            case "20":
                System.out.println("Connack Message Received");
                System.out.println();
                handleConnack(str);
                break;
            case "30":
                System.out.println("Publish Message Received");
                System.out.println();
                handlePublish(str);
                break;
            case "40":
                System.out.println("Puback Message Received");
                System.out.println();
                handlePuback(str);
                break;
            case "50":
                System.out.println("Pubrec Message Received");
                System.out.println();
                handlePubrec(str);
                break;
            case "60":
                System.out.println("Pubrel Message Received");
                System.out.println();
                handlePubrel(str);
                break;
            case "70":
                System.out.println("Pubcomp Message Received");
                System.out.println();
                handlePubcomp(str);
                break;
            case "90":
                System.out.println("Suback Message Received");
                System.out.println();
                handleSuback(str);
                break;
            case "b0":
                System.out.println("Unsuback Message Received");
                System.out.println();
                handleUnSuback(str);
                break;
            case "d0":
                System.out.println("Pingresp Message Received");
                System.out.println();
                handlePingresp(str);
                break;
            default:
                break;
        }
    }

    private void handlePingresp(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePingresp'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePuback'");
    }

    private void handlePublish(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePublish'");
    }

    public void handleConnack(String str) {

    }
}
