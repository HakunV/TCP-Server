package Backend;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // WiFi_Config wc = new WiFi_Config();

        // wc.configure();

        BackendClient bc = new BackendClient();

        try {
            bc.runClient();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
