package my.tinyrender;

import my.render.Vector4f;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/3/28 11:19
 **/
public class FrameBuffer {
    int width;

    int height;

    int[] colorBuffer;

    float[] depthBuffer;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        depthBuffer = new float[width * height];
        colorBuffer = new int[width * height * 4];
    }

    public void clearColor(Vector4f color) {
        int count = width * height;
        for (int i = 0; i < count; i++) {
            colorBuffer[i * 4] = (int) color.X;
            colorBuffer[i * 4 + 1] = (int) color.Y;
            colorBuffer[i * 4 + 2] = (int) color.Z;
            colorBuffer[i * 4 + 3] = (int) color.W;
        }
    }

    public void clearDepth(float depth) {
        int count = width * height;
        for (int i = 0; i < count; i++) {
            depthBuffer[i] = depth;
        }
    }

    public float depth(int x, int y) {
        return depthBuffer[x + width * y];
    }

    public void depth(int x, int y, float dp) {
        depthBuffer[x + width * y] = dp;
    }

    public Vector4f color(int x, int y) {
        int index = x + width * y;
        return new Vector4f(colorBuffer[index * 4], colorBuffer[index * 4 + 1], colorBuffer[index * 4 + 2], colorBuffer[index * 4 + 3]);
    }

    public void color(int x, int y, Vector4f color) {
        int index = x + width * y;
        colorBuffer[index * 4] = (int) color.X;
        colorBuffer[index * 4 + 1] = (int) color.Y;
        colorBuffer[index * 4 + 2] = (int) color.Z;
        colorBuffer[index * 4 + 3] = (int) color.W;
    }
}
