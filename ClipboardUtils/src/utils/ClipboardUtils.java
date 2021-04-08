package utils;

import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ClipboardUtils {

    public static final String IMAGE_FORMAT = "jpg";

    public static String getStringFromClipboard() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String) clipboard.getData(DataFlavor.stringFlavor);
    }

    public static BufferedImage getImageFromClipboard() {
        Optional<Transferable> optional =
                Optional.ofNullable(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null));

        Optional<BufferedImage> bufferedImage = optional.map(transferable -> {
            if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                try {
                    return (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        return bufferedImage.orElse(null);
    }

    public static byte[] convertBufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, IMAGE_FORMAT, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static BufferedImage convertByteArrayToBufferedImage(byte[] image) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image);
        return ImageIO.read(byteArrayInputStream);
    }

    public static void setImageToClipboard(BufferedImage image) {
        if (Objects.isNull(image)) {
            throw new IllegalArgumentException ("Image can't be null");
        }

        Transferable transferable = new TransferableImage(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    public static boolean bufferedImagesAreEquals(BufferedImage imageOne, BufferedImage imageTwo) {
        if (Objects.isNull(imageOne) || Objects.isNull(imageTwo) ) {
            return false;
        }

        if (imageOne.getWidth() == imageTwo.getWidth() && imageOne.getHeight() == imageTwo.getHeight()) {
            for (int x = 0; x < imageOne.getWidth(); x++) {
                for (int y = 0; y < imageOne.getHeight(); y++) {
                    if (imageOne.getRGB(x, y) != imageTwo.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }
}
