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
            //此处宽*2 是因为使用两个'█'字符代表一个像素
            WindowsInterop.runPowershellScript("mode con cols=" + width * 2 + " lines=" + height);
            int consoleModeFlags = WindowsInterop.KERNEL32_ENABLE_LINE_INPUT | WindowsInterop.KERNEL32_ENABLE_PROCESSED_INPUT | WindowsInterop.KERNEL32_ENABLE_ECHO_INPUT;
            // Run Powershell with a predefined script that can change the terminal to non-canonical mode.
            WindowsInterop.runPowershellScript(WindowsInterop.getStdinModeChangePowershellScript(consoleModeFlags, false));
//        String[] sizeString = WindowsInterop.runPowershellScript("$Host.Ui.RawUi.WindowSize.ToString()").split(",");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void display(Buffer<Vector3i> pixelBuffer) {
        int curColor = -1;
        int lastColor = -1;
        setConsoleCursorPosition(0, 0);
        //左手系，所以y轴反向
        Vector3i rgb;
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = pixelBuffer.height - 1; y >= 0; y--) {
            for (int x = 0; x < pixelBuffer.width; x++) {
                rgb = pixelBuffer.get(x,y);
                curColor = rgb.X << 16 | rgb.Y << 8 | rgb.Z;
                if (curColor != lastColor)
                {
                    stringBuilder.append("\u001b[38;2;").append(rgb.X).append(";").append(rgb.Y).append(";").append(rgb.Z).append("m");
                    lastColor = curColor;
                }
                //使用两个'█'字符代表一个像素
                stringBuilder.append("██");
            }
            stringBuilder.append("\u001b[1E"); //  Move to the beginning of the next line.
        }
        System.out.print(stringBuilder);
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
