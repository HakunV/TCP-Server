package Backend;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class BackendClient {
    private Socket client = null;
    private String ip = "thingsofinter.net";
    private int port = 1883;

    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    private Receiver r = null;
    private Sender s = null;

    private boolean active = true;

    public BackendClient() {
        try {
            client = new Socket(ip, port);
            System.out.println("Connected to DTU");

            bis = new BufferedInputStream(client.getInputStream());
            bos = new BufferedOutputStream(client.getOutputStream());

            s = new Sender(this, bos);

            r = new Receiver(bis);
            new Thread(r).start();
        }
        catch(IOException e) {
            System.out.println("Could not connect to DTU");
            e.printStackTrace();
        }
    }

    public void runClient() throws IOException {
        while (active) {
            s.sendConnect();
            this.active = false;
        }
    }
}
