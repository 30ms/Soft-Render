package my.render;

/**
 *
 * @author Liuzhenbin
 * @date 2024-03-09 17:18
 **/
public abstract class AbstractDisplayManager implements DisplayManager {
    private int width;

    private int height;

    public AbstractDisplayManager(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void drawText(int x, int y, Vector3i rgb, String text) {

    }
}
