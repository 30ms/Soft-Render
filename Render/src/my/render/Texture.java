package my.render;

import java.lang.reflect.Array;

/**
 * 材质
 *
 * @author Liuzhenbin
 * @date 2022/12/12 17:18
 **/
public class Texture<T> {
    int width, height;
    T[][] pixels;

    public Texture(int width, int height, T[][] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

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
        return pixels[x % width][y % height];
    }
}