package tcp;

import utils.ClipboardUtils;
import utils.Constants;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Logger;

public class TCPClient {

    private static final Logger LOG = Logger.getLogger(TCPClient.class.getName());

    public static void main(String[] args) throws IOException {
        Socket server = null;
        ObjectOutputStream objectOutputStream;
        BufferedImage lastSentImage = null;

        try {
            server = new Socket(Constants.DEFAULT_SERVER_ADDRESS, Constants.DEFAULT_SERVER_PORT_NGROK);
            LOG.info("Connecting in server...");

            while (true) {
                try {
                    Thread.sleep(Constants.DEFAULT_AWAIT_TIME);

                    if (server.isClosed()) {
                        server = new Socket(Constants.DEFAULT_SERVER_ADDRESS, Constants.DEFAULT_SERVER_PORT_NGROK);
                        LOG.info("Connecting in server...");
                    }

                    BufferedImage bufferedImage = ClipboardUtils.getImageFromClipboard();
                    if (Objects.isNull(bufferedImage)
                            || ClipboardUtils.bufferedImagesAreEquals(lastSentImage, bufferedImage)) continue;

                    lastSentImage = bufferedImage;

                    LOG.info("Sending image");

                    objectOutputStream = new ObjectOutputStream(server.getOutputStream());

                    objectOutputStream.flush();
                    objectOutputStream.writeObject(ClipboardUtils.convertBufferedImageToByteArray(bufferedImage));
                    objectOutputStream.close();

                    LOG.info("Image sent!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LOG.severe(String.format("Cannot connect in server: %s", ex.getMessage()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (server != null) server.close();
    }

}