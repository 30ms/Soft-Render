package my.render;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 缓冲区
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:29
 **/
public class Buffer<T> {
    int width,height;
    T[] data;

    public Buffer(int width, int height, T defaultValue) {
        this.width = width;
        this.height = height;
        @SuppressWarnings("unchecked")
        T[] tmp = (T[]) Array.newInstance(defaultValue.getClass(), width * height);
        this.data = tmp;
    }

    public T get(int x, int y) {
        return data[x + width * y];
    }

    public void set(int x, int y, T value) {
        data[x + width * y] = value;
    }

    public void clear() {
        Arrays.fill(data, null);
    }
}
