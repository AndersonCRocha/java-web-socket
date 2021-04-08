package tcp;

import utils.ClipboardUtils;
import utils.Constants;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class TCPServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        Socket client = null;
        ObjectOutputStream objectOutputStream = null;
        BufferedImage lastSentImage = null;

        try {
            server = new ServerSocket(Constants.DEFAULT_SERVER_PORT);
            System.out.println("Waiting connection request...");
            client = server.accept();

            while (true) {
                Thread.sleep(Constants.DEFAULT_AWAIT_TIME);

                if (!client.isConnected()) {
                    System.out.println("Waiting connection request...");
                    client = server.accept();
                    client.setKeepAlive(true);
                    continue;
                }

                System.out.println("Connection accepted : " + client);

                BufferedImage bufferedImage = ClipboardUtils.getImageFromClipboard();
                if (Objects.isNull(bufferedImage)) {
                    System.out.println("There is no image in clipboard");
                    continue;
                }

                if (ClipboardUtils.bufferedImagesAreEquals(lastSentImage, bufferedImage)) {
                    continue;
                }

                lastSentImage = bufferedImage;

                System.out.println("Sending image");
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());

                objectOutputStream.flush();
                objectOutputStream.writeObject(ClipboardUtils.convertBufferedImageToByteArray(bufferedImage));

                System.out.println("Image sent.\n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (server != null) server.close();
            if (client != null) client.close();
            if (objectOutputStream != null) objectOutputStream.close();
        }
    }
}
