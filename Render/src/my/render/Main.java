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
                    new Face(new int[]{0, 3, 1}, new int[]{0, 3, 1}, new int[]{}),
                    new Face(new int[]{1, 3, 2}, new int[]{0, 3, 1}, new int[]{}),

                    new Face(new int[]{4, 5, 7}, new int[]{4, 5, 7}, new int[]{}),
                    new Face(new int[]{5, 6, 7}, new int[]{5, 6, 7}, new int[]{}),

                    new Face(new int[]{8, 11, 9}, new int[]{8, 11, 9}, new int[]{}),
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
                    new Vector3i(240, 0, 16),
                    new Vector3i(0, 216, 32),
                    new Vector3i(26, 0, 240),
                    new Vector3i(255, 255, 0),
            }, 2);
    static long MS_PRE_UPDATE = 16;
    public static void main(String[] args) throws Exception {

/*        int consoleModeFlags = WindowsInterop.KERNEL32_ENABLE_LINE_INPUT |
                WindowsInterop.KERNEL32_ENABLE_PROCESSED_INPUT |
                WindowsInterop.KERNEL32_ENABLE_ECHO_INPUT;
        // Run Powershell with a predefined script that can change the terminal to non-canonical mode.
        WindowsInterop.runPowershellScript(WindowsInterop.getStdinModeChangePowershellScript(consoleModeFlags, false));*/
        Model[] models = new Model[]{
                new Model(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), new Mesh(vertices, uvs, new Vector3f[]{}, faces))
        };
        int WIDTH = 200, HEIGHT = 200;
        Camera camera = new Camera(new Vector3f(0, 0, -3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), 50, (float) WIDTH / HEIGHT, 0.1f, 100f);
        DisplayManager displayManager = new DisplayManager(WIDTH, HEIGHT);
        SceneManager sceneManager = new SceneManager();
        sceneManager.addScene("main", new Scene(camera, Arrays.stream(models).collect(Collectors.toList())));
        sceneManager.switchScene("main");
        RenderManager renderManager = new RenderManager(displayManager, sceneManager);
        renderManager.setShader(new TextureMapShader(texture));
        long pre = System.currentTimeMillis();
        long lag = 0;
        renderManager.render();
    }
}
