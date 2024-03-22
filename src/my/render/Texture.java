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

    /**
     * 纹理采样
     */
    public T sample(float u, float v) {
        //超出[0,1]范围处理
        if(u > 1) u = u - (int)u;
        if(u < 0) u = 1 - ((int) u - u);
        if(v > 1) v = v - (int)v;
        if(v < 0) v = 1 - ((int) v - v);

        return getPixel((int) (u * width % width), (int) (v * height % height));
    }

    public static Texture<Vector4f> loadFromFile(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int w = image.getWidth(), h = image.getHeight();
        Vector4f[] data = Arrays.stream(image.getRGB(0, 0, w, h, null, 0, w))
                .mapToObj(color -> new Vector4f((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, (color >> 24 & 0xFF) / 255f))
                .toArray(Vector4f[]::new);
        return new Texture<>(data, w);
    }

}
