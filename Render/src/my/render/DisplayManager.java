package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/16 15:50
 **/
public class DisplayManager {

    private int width;
    private int height;

    public DisplayManager(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void swapBuffer(Buffer<Vector3i> pixelBuffer) {
        //TODO 渲染像素
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
