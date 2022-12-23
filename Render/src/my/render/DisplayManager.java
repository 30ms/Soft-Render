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
        int ansiColorCode = -1;
        int lastColorRgb8 = -1;
        System.out.print("\u001b[" + 1 + ";" + 1 + "H");
        for (int y = 0; y < pixelBuffer.height; y++) {
            for (int x = 0; x < pixelBuffer.width; x++) {
                Vector3i value = pixelBuffer.get(x,y);
                int r = Math.min((int) ((value.X / 256.0f) * 6), 5) * 36;
                int g = Math.min((int) ((value.Y / 256.0f) * 6), 5) * 6;
                int b = Math.min((int) ((value.Z / 256.0f) * 6), 5);
                //if (0 <= value.X && value.Z <= 5)
                ansiColorCode = 16 + r + g + b;
                if (ansiColorCode != lastColorRgb8)
                {
                    System.out.print("\u001b[38;5;" + ansiColorCode + "m");
                    lastColorRgb8 = ansiColorCode;
                }
                System.out.print('\u2588');
            }
            System.out.print("\n"); // Mo
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
