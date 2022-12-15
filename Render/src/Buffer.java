import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:29
 **/
public class Buffer<T> {
    int width,height;
    T[] data;

    public Buffer(int width, int height, T[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
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
