package my.render;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/17 10:56
 **/
public class Main {

    static Vector3f[] vertices = new Vector3f[]{
            // Z+
            new Vector3f(-0.5f,  0.5f, 0.5f),
            new Vector3f( 0.5f,  0.5f, 0.5f),
            new Vector3f( 0.5f, -0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, 0.5f),

            // Z-
            new Vector3f(-0.5f,  0.5f, -0.5f),
            new Vector3f( 0.5f,  0.5f, -0.5f),
            new Vector3f( 0.5f, -0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),

            // Y+
            new Vector3f( 0.5f,  0.5f, -0.5f),
            new Vector3f( 0.5f,  0.5f,  0.5f),
            new Vector3f(-0.5f,  0.5f,  0.5f),
            new Vector3f(-0.5f,  0.5f, -0.5f),

            // Y-
            new Vector3f( 0.5f, -0.5f, -0.5f),
            new Vector3f( 0.5f, -0.5f,  0.5f),
            new Vector3f(-0.5f, -0.5f,  0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),

            // X+
            new Vector3f( 0.5f, -0.5f,  0.5f),
            new Vector3f( 0.5f,  0.5f,  0.5f),
            new Vector3f( 0.5f,  0.5f, -0.5f),
            new Vector3f( 0.5f, -0.5f, -0.5f),

            // X-
            new Vector3f(-0.5f, -0.5f,  0.5f),
            new Vector3f(-0.5f,  0.5f,  0.5f),
            new Vector3f(-0.5f,  0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
    };

    static Vector2f[] uvs = new Vector2f[]{
            // Z+
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),

            // Z-
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),

            // Y+
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),

            // Y-
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),

            // X+
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),

            // X-
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
            new Vector2f(0.0f, 0.0f),
    };

    static Face[] faces = new Face[]
            {
                    new Face(new int[]{0, 3, 1}, new int[]{0, 3, 1}, new int[]{}),     //Z+
                    new Face(new int[]{1, 3, 2}, new int[]{1, 3, 2}, new int[]{}),

                    new Face(new int[]{4, 5, 7}, new int[]{4, 5, 7}, new int[]{}),    //Z-
                    new Face(new int[]{5, 6, 7}, new int[]{5, 6, 7}, new int[]{}),

                    new Face(new int[]{8, 11, 9}, new int[]{8, 11, 9}, new int[]{}),    //Y+
                    new Face(new int[]{9, 11, 10}, new int[]{9, 11, 10}, new int[]{}),

                    new Face(new int[]{12, 13, 15}, new int[]{12, 13, 15}, new int[]{}), // Y- 0, 1, 3,
                    new Face(new int[]{13, 14, 15}, new int[]{13, 14, 15}, new int[]{}), //    1, 2, 3,

                    new Face(new int[]{16, 19, 17}, new int[]{16, 19, 17}, new int[]{}), // X+ 0, 3, 1,
                    new Face(new int[]{17, 19, 18}, new int[]{17, 19, 18}, new int[]{}), //    1, 3, 2,

                    new Face(new int[]{20, 21, 23}, new int[]{20, 21, 23}, new int[]{}), // X- 0, 1, 3,
                    new Face(new int[]{21, 22, 23}, new int[]{21, 22, 23}, new int[]{})  //    1, 2, 3,
            };

    static Texture<Vector3i> texture = new Texture<>(new Vector3i[]
            {
                    new Vector3i(255, 0, 0),
                    new Vector3i(0, 255, 0),
                    new Vector3i(0, 0, 255),
                    new Vector3i(0, 255, 255),
            }, 2);

    //主循环
    static boolean MainLoop = true;
    //每16ms更新一次
    static long MS_PER_UPDATE = 16;
    static Vector3i COLOR_WHITE = new Vector3i(255, 255, 255);
    public static void main(String[] args) throws Exception {

        int consoleModeFlags = WindowsInterop.KERNEL32_ENABLE_LINE_INPUT | WindowsInterop.KERNEL32_ENABLE_PROCESSED_INPUT | WindowsInterop.KERNEL32_ENABLE_ECHO_INPUT;
        // Run Powershell with a predefined script that can change the terminal to non-canonical mode.
        WindowsInterop.runPowershellScript(WindowsInterop.getStdinModeChangePowershellScript(consoleModeFlags, false));
        String[] sizeString = WindowsInterop.runPowershellScript("$Host.Ui.RawUi.WindowSize.ToString()").split(",");

        int width = Integer.parseInt(sizeString[0]);
        int height = Integer.parseInt(sizeString[1]);

        Vector2i terminalSize = new Vector2i(width, height);

        Model model = new Model(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), new Mesh(vertices, uvs, new Vector3f[]{}, faces));
        Model[] models = new Model[]{model};
        Camera camera = new Camera(new Vector3f(0, 0, 2f), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), 45, (float) terminalSize.X / terminalSize.Y, 0.1f, 50);
        DisplayManager displayManager = new DisplayManager(terminalSize.X, terminalSize.Y);
        SceneManager sceneManager = new SceneManager();
        sceneManager.addScene("main", new Scene(camera, Arrays.stream(models).collect(Collectors.toList())));
        sceneManager.switchScene("main");
        RenderManager renderManager = new RenderManager(displayManager, sceneManager);
        renderManager.setShader(new TextureMapShader(texture));
        long previous = System.currentTimeMillis();
        long lag = 0, time = 0;
        long frame = 0, lastSecondTime = 0, lastSecondFrame = 0, framesPerSecond = 0;
        while (MainLoop)
        {
            long current = System.currentTimeMillis();
            long elapsed = current - previous;
            previous = current;
            lag += elapsed;
            time += elapsed;

            if (time - lastSecondTime >= 1000) {
                framesPerSecond = frame - lastSecondFrame;
                lastSecondFrame = frame;
                lastSecondTime = time;
            }

            processInput(sceneManager);

            while (lag >= MS_PER_UPDATE)
            {
                update(sceneManager, lag);
                lag -= MS_PER_UPDATE;
            }

            render(renderManager);
            displayManager.drawText(0, displayManager.getHeight() - 2, COLOR_WHITE, "摄影机: w,s,a,d 前后左右 q,e 左右旋转 | x 退出");
            displayManager.drawText(0, displayManager.getHeight() - 1, COLOR_WHITE, framesPerSecond + "FPS | time:" + time / 1000);
            frame++;
        }
    }

    public static void processInput(SceneManager sceneManager) {
        Thread thread = new Thread(() -> {
            while (MainLoop) {
                try {
                    if (System.in.available() > 0) {
                        Camera camera = sceneManager.getCurrentScene().getMainCamera();
                        char key = (char) System.in.read();
                        switch (key) {
                            case 'w':
                                camera.forward(1);
                                break;
                            case 's':
                                camera.backward(1);
                                break;
                            case 'a':
                                camera.left(1);
                                break;
                            case 'd':
                                camera.right(1);
                                break;
                            case 'q':
                                camera.rotation(0, 10, 0);
                                break;
                            case 'e':
                                camera.rotation(0, -10, 0);
                                break;
                            case 'x':
                                MainLoop = false;
                        }
                    }
                    Thread.sleep(167);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public static void update(SceneManager sceneManager, long delta) {
        sceneManager.updateScene(delta);
        //绕XYZ轴旋转
        sceneManager.getCurrentScene().getModelsInScene().forEach(model -> {
            model.rotation.Y += delta * 0.003;
            model.rotation.X += delta * 0.003;
            model.rotation.Z += delta * 0.003;
        });
    }

    public static void render(RenderManager renderManager) {
        renderManager.render();
    }
}
