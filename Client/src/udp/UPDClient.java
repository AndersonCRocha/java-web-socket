package udp;

import utils.ClipboardUtils;
import utils.Constants;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Objects;
import java.util.logging.Logger;

public class UPDClient {
    public static final Logger LOG = Logger.getLogger(UPDClient.class.getName());

    public static void main(String[] args) {
        LOG.info("Client is running!");

        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(Constants.DEFAULT_SERVER_ADDRESS);

            while (true) {
                Thread.sleep(Constants.DEFAULT_AWAIT_TIME);

                BufferedImage bufferedImage = ClipboardUtils.getImageFromClipboard();
                if (Objects.isNull(bufferedImage)) {
                    System.out.println("There is no image in clipboard");
                    continue;
                }

                byte[] dataToSent = ClipboardUtils.convertBufferedImageToByteArray(bufferedImage);
                DatagramPacket packetToSent = new DatagramPacket(dataToSent, dataToSent.length, address,
                        Constants.DEFAULT_SERVER_PORT);
                socket.send(packetToSent);

                DatagramPacket receivedPacket = new DatagramPacket(new byte[Constants.DATA_LENGTH],
                        Constants.DATA_LENGTH);
                socket.receive(receivedPacket);
                String receivedSentence = new String(receivedPacket.getData()).trim();

                System.out.println("RECEIVED FROM SERVER: " + receivedSentence);
                if (receivedSentence.toUpperCase().equals("EXIT")) {
                    socket.close();
                    break;
                }
            }
        } catch (SocketException ex) {
            LOG.severe(String.format("Socket error: %s", ex.getMessage()));
        } catch (IOException ex) {
            ex.printStackTrace();
            LOG.severe(String.format("IO error: %s", ex.getMessage()));
        } catch (Exception ex) {
            LOG.severe(String.format("Unknown error: %s", ex.getMessage()));
        }
    }
}
