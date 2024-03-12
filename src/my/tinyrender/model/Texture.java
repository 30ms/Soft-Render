package my.tinyrender.model;

import java.lang.reflect.Array;

/**
 * 纹理
 *
 **/
public class Texture<T> {
    public int width, height;
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
}
