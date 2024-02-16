package Backend;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        BackendClient bc = new BackendClient();

        try {
            bc.runClient();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
