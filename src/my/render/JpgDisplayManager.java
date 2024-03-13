package my.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/3/24 9:34
 **/
public class JpgDisplayManager extends AbstractDisplayManager {
    private File file;

    public JpgDisplayManager(int width, int height, File file) {
        super(width,height);
        this.file = file;
    }

    public void display(Buffer<Vector3i> pixelBuffer) {

        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < pixelBuffer.width; x++) {
            for (int y = 0; y < pixelBuffer.height; y++) {
                Vector3i value = pixelBuffer.get(x, pixelBuffer.height - 1 - y);
                int r = value.X;
                int g = value.Y;
                int b = value.Z;
                bufferedImage.setRGB(x, y, 65536 * b + 256 * g + r);
            }
        }
        try {
            ImageIO.write(bufferedImage, "jpg", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
