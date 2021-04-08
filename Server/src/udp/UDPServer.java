package udp;

import utils.ClipboardUtils;
import utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;

public class UDPServer {
    public static final Logger LOG = Logger.getLogger(UDPServer.class.getName());

    public static void main(String[] args) {
        LOG.info(String.format("Server is running in port: %d!", Constants.DEFAULT_SERVER_PORT));

        try {
            DatagramSocket socket = new DatagramSocket(Constants.DEFAULT_SERVER_PORT);

            while (true) {
                DatagramPacket receivedPacket = new DatagramPacket(new byte[Constants.DATA_LENGTH],
                        Constants.DATA_LENGTH);
                socket.receive(receivedPacket);

                byte[] receivedBytes = receivedPacket.getData();
                BufferedImage bufferedImage = ClipboardUtils.convertByteArrayToBufferedImage(receivedBytes);

                ClipboardUtils.setImageToClipboard(bufferedImage);

                File outputFile = new File("image.jpg");
                ImageIO.write(bufferedImage, ClipboardUtils.IMAGE_FORMAT, outputFile);
                String imageLength = String.valueOf(outputFile.length());
                System.out.println("RECEIVED FROM CLIENT: Image length" + imageLength);

                InetAddress address = receivedPacket.getAddress();
                int port = receivedPacket.getPort();
                byte[] dataToSent = String.format("Received image length: %s", imageLength).getBytes();
                DatagramPacket sendPacket = new DatagramPacket(dataToSent, dataToSent.length, address, port);
                socket.send(sendPacket);

                if (imageLength.equals("0")) {
                    socket.close();
                    break;
                }
            }
        } catch (SocketException ex) {
            LOG.severe(String.format("Socket error: %s", ex.getMessage()));
        } catch (IOException ex) {
            LOG.severe(String.format("IO error: %s", ex.getMessage()));
        } catch (Exception ex) {
            LOG.severe(String.format("Unknown error: %s", ex.getMessage()));
        }
    }
}
