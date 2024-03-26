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
    Filtering filtering;

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
        filtering = Filtering.NEAREST;
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

        switch (filtering) {
            case NEAREST:
                return nearest(u, v);
            case BILINEAR:
                return bilinear(u, v);
            default:
                throw new RuntimeException("Unknown filtering method");
        }
    }

    //最邻近点采样(NEAREST)
    private T nearest(float u, float v) {
        int x = (int) (u * (width - 1));
        int y = (int) (v * (height - 1));
        return getPixel(x, y);
    }

    //双线性采样(Bilinear)
    private T bilinear(float u, float v) {
        float x = u * (width - 1);
        float y = v * (height - 1);

        int x0 = (int) x;
        int y0 = (int) y;

        int x1 = x0 + 1;
        int y1 = y0 + 1;
        if(x1 > width -1) x1 = x0;
        if(y1 > height -1) y1 = y0;

        return interpolate(getPixel(x0, y0), getPixel(x1, y0), getPixel(x0, y1), getPixel(x1, y1), x - x0, y - y0);
    }

    private T interpolate(T pixel, T pixel1, T pixel2, T pixel3, float v, float v1) {
        return interpolate(interpolate(pixel, pixel1, v), interpolate(pixel2, pixel3, v), v1);
    }

    private T interpolate(T pixel, T pixel1, float f) {
        if (pixel instanceof Vector4f) {
            Vector4f v1 = (Vector4f) pixel, v2 = (Vector4f) pixel1;
            return (T) new Vector4f(
                    v1.X + f * (v2.X - v1.X),
                    v1.Y + f * (v2.Y - v1.Y),
                    v1.Z + f * (v2.Z - v1.Z),
                    v1.W + f * (v2.W - v1.W));
        }
        throw new RuntimeException("not support");
    }

    public T sample(Vector2f st) {
        return sample(st.X, st.Y);
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

    //纹理过滤
    public enum Filtering {
        NEAREST,
        BILINEAR
    }

}
