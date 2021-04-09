package tcp;

import utils.ClipboardUtils;
import utils.Constants;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Logger;

public class TCPServer {

    private static final Logger LOG = Logger.getLogger(TCPServer.class.getName());

    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        Socket client = null;
        ObjectOutputStream objectOutputStream;
        BufferedImage lastSentImage = null;

        try {
            server = new ServerSocket(Constants.DEFAULT_SERVER_PORT);
            LOG.info("Waiting connection request...");
            client = server.accept();

            while (true) {
                Thread.sleep(Constants.DEFAULT_AWAIT_TIME);

                if (client.isClosed()) {
                    client = server.accept();
                    client.setKeepAlive(true);
                    continue;
                }

                BufferedImage bufferedImage = ClipboardUtils.getImageFromClipboard();
                if (
                    Objects.isNull(bufferedImage) || ClipboardUtils.bufferedImagesAreEquals(lastSentImage,bufferedImage)
                ) {
                    continue;
                }

                lastSentImage = bufferedImage;

                LOG.info("Sending image");
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());

                objectOutputStream.flush();
                objectOutputStream.writeObject(ClipboardUtils.convertBufferedImageToByteArray(bufferedImage));
                objectOutputStream.close();

                LOG.info("Image sent.");
            }
        } catch (IOException | InterruptedException e) {
            LOG.severe(e.getMessage());
        } finally {
            if (server != null) server.close();
            if (client != null) client.close();
        }
    }
}
