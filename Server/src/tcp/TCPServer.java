package tcp;

import utils.ClipboardUtils;
import utils.Constants;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class TCPServer {

    private static final Logger LOG = Logger.getLogger(TCPServer.class.getName());

    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        Socket client = null;
        ObjectInputStream objectInputStream;

        try {
            server = new ServerSocket(Constants.DEFAULT_SERVER_PORT);
            LOG.info("Waiting connection request...");
            client = server.accept();
            LOG.info(String.format("Client accepted: %s", client));

            while (true) {
                Thread.sleep(Constants.DEFAULT_AWAIT_TIME);

                if (client.isClosed()) {
                    client = server.accept();
                    LOG.info(String.format("Client accepted: %s", client));
                }

                objectInputStream = new ObjectInputStream(client.getInputStream());

                byte[] bytes = (byte[]) objectInputStream.readObject();
                BufferedImage bufferedImage = ClipboardUtils.convertByteArrayToBufferedImage(bytes);
                ClipboardUtils.setImageToClipboard(bufferedImage);
                objectInputStream.close();

                LOG.info("Added image to clipboard");
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
            LOG.severe(e.getMessage());
        } finally {
            if (server != null) server.close();
            if (client != null) client.close();
        }
    }
}
