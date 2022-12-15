import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 15:14
 **/
class WindowsInterop
{
    // Source: https://learn.microsoft.com/es-es/windows/console/setconsolemode

    static final String CSHARP_OVER_POWERSHELL_INTEROP_KERNEL32 = "$sigKernel32 = @'\n" +
            "[DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "public static extern IntPtr GetStdHandle(int nStdHandle);\n" +
            "[DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "public static extern uint GetConsoleMode(IntPtr hConsoleHandle, out uint lpMode);\n" +
            "[DllImport(\"kernel32.dll\", SetLastError = true)]\n" +
            "public static extern uint SetConsoleMode(IntPtr hConsoleHandle, uint dwMode);\n" +
            "public const int STD_INPUT_HANDLE = -10;\n" +
            "'@\n";

    static final String POWERSHELL_GET_STDIN_HANDLE = CSHARP_OVER_POWERSHELL_INTEROP_KERNEL32 +
            "$Kernel32Api = Add-Type -MemberDefinition $sigKernel32 -Name Kernel32Api -Namespace ThreeDee -PassThru\n" +
            "$handle = $Kernel32Api::GetStdHandle($Kernel32Api::STD_INPUT_HANDLE)\n" +
            "if ($handle -eq -1) {\n" +
            "	throw \"Cannot get standard input handle.\"\n" +
            "}\n";

    public static final int KERNEL32_ENABLE_PROCESSED_INPUT = 0x0001;
    public static final int KERNEL32_ENABLE_LINE_INPUT = 0x0002;
    public static final int KERNEL32_ENABLE_ECHO_INPUT = 0x0004;

    public static String getStdinModeChangePowershellScript(int flag, boolean enable)
    {
        return POWERSHELL_GET_STDIN_HANDLE +
                "$mode = 0\n" +
                "$ret = $Kernel32Api::GetConsoleMode($handle, [ref]$mode)\n" +
                "if ($ret -eq 0) {\n" +
                "	throw \"Cannot get standard input mode.\"\n" +
                "}\n" +
                // "Echo \"Console mode is $mode\"\n" +
                "$newmode = $mode " +
                (
                        enable ?
                                "-bor " + flag :
                                "-band (-bnot " + flag + ")"
                ) + "\n" +
                // "Echo \"New console mode is $newmode\"\n" +
                "$ret = $Kernel32Api::SetConsoleMode($handle, $newmode)\n" +
                "if ($ret -eq 0) {\n" +
                "	throw \"Cannot set standard input mode.\"\n" +
                "}\n" +
                "$ret = $Kernel32Api::GetConsoleMode($handle, [ref]$mode)\n";
        // "Echo \"Console mode is now $mode\"\n";
    }

    public static String runPowershellScript(String script) throws Exception, IOException, InterruptedException
    {
        ProcessBuilder powershellProcessBuilder = null;
        if (script.contains("\n"))
        {
            String wrappedScript = "& {\n" + script + "\n}";

            Base64.Encoder b64enc = Base64.getEncoder();
            String encodedScript = b64enc.encodeToString(wrappedScript.getBytes(StandardCharsets.UTF_16LE));

            powershellProcessBuilder = new ProcessBuilder("powershell", "-NoP", "-NonInteractive", "-EncodedCommand", encodedScript);
        }
        else
        {
            powershellProcessBuilder = new ProcessBuilder("powershell", "-NoP", "-command", script);
        }

        // AFAIK, this is required to modify the current `conhost.exe` instance.
        powershellProcessBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        Process powershellProcess = powershellProcessBuilder.start();
        int powershellStatus = powershellProcess.waitFor();

        if (powershellStatus != 0)
        {
            throw new Exception("Powershell script failed with status code " + powershellStatus + ".");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(powershellProcess.getInputStream()));
        String ret = String.join("\n", br.lines().collect(Collectors.toList()));
        br.close();
        return ret;
    }
}

