package my.render;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 帧缓冲区
 *
 * @author Liuzhenbin
 * @date 2022/12/12 17:05
 **/
public class FrameBuffer<T> {
    T[] buffer;
    int width, height;
    T defaultValue;
    float[] zBuffer;

    public FrameBuffer(int width, int height, T defaultValue) {
        this.width = width;
        this.height = height;
        this.defaultValue = defaultValue;

        @SuppressWarnings("unchecked")
        T[] b = (T[]) Array.newInstance(defaultValue.getClass(), width * height);
        this.buffer = b;
        this.zBuffer = new float[width * height];
    }

    public void fill(T value) {
        Arrays.fill(buffer, value);
    }

    public void clear() {
        fill(defaultValue);
    }

    public T get(int index) {
        return (index > 0 && index < buffer.length) ? buffer[index] : defaultValue;
    }

    public void set(int index, T value) {
        if (index > 0 && index < buffer.length) {
            buffer[index] = value;
        }
    }

}
