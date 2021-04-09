package tcp;

import utils.ClipboardUtils;
import utils.Constants;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class TCPClient {

    private static final Logger LOG = Logger.getLogger(TCPClient.class.getName());

    public static void main(String[] args) throws IOException {
        Socket server = null;
        ObjectInputStream objectInputStream = null;

        try {
            while (true) {
                try {
                    Thread.sleep(Constants.DEFAULT_AWAIT_TIME);
                    LOG.info("Connecting in server...");

                    server = new Socket(Constants.DEFAULT_SERVER_ADDRESS, Constants.DEFAULT_SERVER_PORT_NGROK);

                    objectInputStream = new ObjectInputStream(server.getInputStream());

                    byte[] bytes = (byte[]) objectInputStream.readObject();
                    BufferedImage bufferedImage = ClipboardUtils.convertByteArrayToBufferedImage(bytes);
                    ClipboardUtils.setImageToClipboard(bufferedImage);

                    LOG.info("Added image to clipboard!");
                } catch (IOException ex) {
                    LOG.severe(String.format("Cannot connect in server: %s", ex.getMessage()));
                } catch ( InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            LOG.severe(ex.getMessage());
        } finally {
            if (server != null) server.close();
            if (objectInputStream != null) objectInputStream.close();
        }
    }

}