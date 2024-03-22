package my.render;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * java Swing 窗口显示
 *
 * @author Liuzhenbin
 * @date 2024-03-09 15:46
 **/
public class SwingFrameDisplayManager extends AbstractDisplayManager {
    JFrame jFrame;
    BufferedImage image;
    public SwingFrameDisplayManager(int width, int height) {
        super(width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        jFrame = new JFrame() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, jFrame);
            }
        };
        jFrame.setSize(width, height);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void display(Buffer<Vector4f> pixelBuffer) {
        for (int y = 0; y < pixelBuffer.height; y++) {
            for (int x = 0; x < pixelBuffer.width; x++) {
                Vector4f rgba = pixelBuffer.get(x, pixelBuffer.height - 1 - y);
                image.setRGB(x, y, (int) (rgba.W * 255) << 24 | (int) (rgba.X * 255) << 16 | (int) (rgba.Y * 255) << 8 |(int) (rgba.Z * 255));
            }
        }
        jFrame.repaint();
    }

    @Override
    public void drawText(int x, int y, Vector3i rgb, String text) {
        System.out.println(text);
    }
}
