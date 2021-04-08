package tcp;

import utils.ClipboardUtils;
import utils.Constants;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class TCPClient {

    public static void main(String[] args) throws IOException {
        Socket server = null;
        ObjectInputStream objectInputStream = null;

        try {
            while (true) {
                try {
                    Thread.sleep(Constants.DEFAULT_AWAIT_TIME);
                    System.out.println("Connecting in server...");

                    server = new Socket(Constants.DEFAULT_SERVER_ADDRESS, Constants.DEFAULT_SERVER_PORT_NGROK);

                    objectInputStream = new ObjectInputStream(server.getInputStream());

                    byte[] bytes = (byte[]) objectInputStream.readObject();
                    BufferedImage bufferedImage = ClipboardUtils.convertByteArrayToBufferedImage(bytes);
                    ClipboardUtils.setImageToClipboard(bufferedImage);

                    FileOutputStream file = new FileOutputStream("resources/image.jpg");
                    file.write(ClipboardUtils.convertBufferedImageToByteArray(bufferedImage));

                    System.out.println("Added image to clipboard!");
                } catch (IOException ex) {
                    System.err.println("Cannot connect in server");
                } catch ( InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (server != null) server.close();
            if (objectInputStream != null) objectInputStream.close();
        }
    }

}