package API;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetInfo {
    private Socket client = null;
    private int port = 1883;

    private BufferedInputStream bis = null;
    private BufferedOutputStream bos = null;

    private Receiver r = null;
    private Sender s = null;

    private boolean active = true;

    public GetInfo() {
    }

    public void runClient() throws IOException {
        URL url = new URL("thingsofinter.net/GetDataForDevice");
        while (active) {
        }
    }
}
