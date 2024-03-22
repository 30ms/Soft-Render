package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2024-03-09 17:14
 **/
public interface DisplayManager {

    void display(Buffer<Vector4f> pixelBuffer);

    void drawText(int x, int y, Vector3i rgb, String text);

    int getWidth();

    int getHeight();

}
