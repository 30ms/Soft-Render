package my.render;

/**
 *
 * @author Liuzhenbin
 * @date 2022/12/16 15:50
 **/
public class WindowsConsoleDisplayManager extends AbstractDisplayManager {

    public WindowsConsoleDisplayManager(int width, int height){
        super(width,height);
        try {
            WindowsInterop.runPowershellScript("mode con cols=" + width + " lines=" + height);
            int consoleModeFlags = WindowsInterop.KERNEL32_ENABLE_LINE_INPUT | WindowsInterop.KERNEL32_ENABLE_PROCESSED_INPUT | WindowsInterop.KERNEL32_ENABLE_ECHO_INPUT;
            // Run Powershell with a predefined script that can change the terminal to non-canonical mode.
            WindowsInterop.runPowershellScript(WindowsInterop.getStdinModeChangePowershellScript(consoleModeFlags, false));
//        String[] sizeString = WindowsInterop.runPowershellScript("$Host.Ui.RawUi.WindowSize.ToString()").split(",");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void swapBuffer(Buffer<Vector3i> pixelBuffer) {
        //TODO 渲染像素
        int ansiColorCode = -1;
        int lastColorRgb8 = -1;
        setConsoleCursorPosition(0, 0);
        //左手系，所以y轴反向
        for (int y = pixelBuffer.height - 1; y >= 0; y--) {
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
            System.out.print("\n"); //  Move to the beginning of the next line.
        }
    }

    private void setConsoleCursorPosition(int x, int y)
    {
        System.out.print("\u001b[" + (y + 1) + ";" + (x + 1) + "H");
    }

    private String getAnsiCodeForFgRgb24(int r, int g, int b) {
        return "\u001b[38;2;" + Math.min(r, 255) + ";" + Math.min(g, 255) + ";" + Math.min(b, 255) + "m";
    }
    public void drawText(int x, int y, Vector3i rgb, String text) {
        setConsoleCursorPosition(x, y);
        System.out.print(getAnsiCodeForFgRgb24(rgb.X, rgb.Y, rgb.Z));
        System.out.print(text);
    }
}
