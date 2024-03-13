package my.render;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 材质
 *
 * @author Liuzhenbin
 * @date 2022/12/12 17:18
 **/
public class Texture<T> {
    int width, height;
    T[][] pixels;

    public Texture(T[] rawRgbData, int width)
    {
        this.width = width;
        height = rawRgbData.length / this.width;
        @SuppressWarnings("unchecked")
        T[][] tmp = (T[][]) Array.newInstance(rawRgbData.getClass().getComponentType(), new int[]{this.width, this.height});
        pixels = tmp;

        for (int i = 0; i < rawRgbData.length; i++)
        {
            int x = i % width, y = i / width;
            pixels[x][y] = rawRgbData[i];
        }
    }

    public T getPixel(int x, int y)
    {
        return pixels[x][y];
    }

    public T getPixel(float u, float v) {
        return getPixel((int) (u * width % width), (int) (v * height % height));
    }

    public static Texture<Vector3i> loadFromFile(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int w = image.getWidth(), h = image.getHeight();
        Vector3i[] data = Arrays.stream(image.getRGB(0, 0, w, h, null, 0, w))
                .mapToObj(color -> new Vector3i(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF))
                .toArray(Vector3i[]::new);
        return new Texture<>(data, w);
    }

}
