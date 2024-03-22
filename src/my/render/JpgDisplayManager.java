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

    public void display(Buffer<Vector4f> pixelBuffer) {

        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < pixelBuffer.width; x++) {
            for (int y = 0; y < pixelBuffer.height; y++) {
                Vector4f rgba = pixelBuffer.get(x, pixelBuffer.height - 1 - y);
                bufferedImage.setRGB(x, y, (int) (rgba.W * 255) << 24 + (int) (rgba.Z * 255) << 16 + (int) (rgba.Y * 255) << 8 + (int) rgba.X * 255);
            }
        }
        try {
            ImageIO.write(bufferedImage, "jpg", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
