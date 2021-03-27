package client;

import utils.ClipboardUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Objects;
import java.util.logging.Logger;

public class Client {
    public static final Logger LOG = Logger.getLogger(Client.class.getName());

    public static final int DATA_LENGTH = 32 * 1024;
    public static final int SERVER_PORT = 8080;
    public static final String LOCALHOST = "50d6a29aaa8f.ngrok.io";

    public static void main(String[] args) {
        LOG.info("Client is running!");

        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(LOCALHOST);

            while (true) {
                Thread.sleep(500);
                BufferedImage bufferedImage = ClipboardUtils.getImageFromClipboard();
                if (Objects.isNull(bufferedImage)) {
                    System.out.println("There is no image in clipboard");
                    continue;
                }

                byte[] dataToSent = ClipboardUtils.convertBufferedImageToByteArray(bufferedImage);
                DatagramPacket packetToSent = new DatagramPacket(dataToSent, dataToSent.length, address,
                        SERVER_PORT);
                socket.send(packetToSent);

                DatagramPacket receivedPacket = new DatagramPacket(new byte[DATA_LENGTH], DATA_LENGTH);
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
