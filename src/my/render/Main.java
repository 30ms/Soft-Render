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
            new Vector3f(-0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, 0.5f, 0.5f),
            // Z-
            new Vector3f(-0.5f, 0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, 0.5f, -0.5f),
    };

    static Vector2f[] uvs = new Vector2f[]{
            // Z+
            new Vector2f(0.0f, 0.0f),
            new Vector2f(0.0f, 1.0f),
            new Vector2f(1.0f, 1.0f),
            new Vector2f(1.0f, 0.0f),
    };

    //法向量
    static Vector3f[] normals = new Vector3f[]{
            new Vector3f(0.0f, 0.0f, 1.0f),     //Z+
            new Vector3f(0.0f, 0.0f, -1.0f),    //Z-
            new Vector3f(0.0f, 1.0f, 0.0f),     //Y+
            new Vector3f(0.0f, -1.0f, 0.0f),    //Y-
            new Vector3f(1.0f, 0.0f, 0.0f),     //X+
            new Vector3f(-1.0f, 0.0f, 0.0f)     //X-
    };

    static Face[] faces = new Face[]
            {
                    new Face(new int[]{0, 1, 2}, new int[]{0, 1, 2}, new int[]{0,0,0}),     //Z+
                    new Face(new int[]{0, 2, 3}, new int[]{0, 2, 3}, new int[]{0,0,0}),

                    new Face(new int[]{7, 6, 5}, new int[]{0, 1, 2}, new int[]{1,1,1}),     //Z-
                    new Face(new int[]{7, 5, 4}, new int[]{0, 2, 3}, new int[]{1,1,1}),

                    new Face(new int[]{4, 0, 3}, new int[]{0, 1, 2}, new int[]{2,2,2}),     //Y+
                    new Face(new int[]{4, 3, 7}, new int[]{0, 2, 3}, new int[]{2,2,2}),

                    new Face(new int[]{1, 5, 6}, new int[]{0, 1, 2}, new int[]{3,3,3}),     //Y-
                    new Face(new int[]{1, 6, 2}, new int[]{0, 2, 3}, new int[]{3,3,3}),

                    new Face(new int[]{3, 2, 6}, new int[]{0, 1, 2}, new int[]{4,4,4}),     //x+
                    new Face(new int[]{3, 6, 7}, new int[]{0, 2, 3}, new int[]{4,4,4}),

                    new Face(new int[]{4, 5, 1}, new int[]{0, 1, 2}, new int[]{5,5,5}),     //x-
                    new Face(new int[]{4, 1, 0}, new int[]{0, 2, 3}, new int[]{5,5,5}),
            };

    static Texture<Vector3i> texture = Texture.loadFromFile("img/wood_box.jpg");

    //主循环
    static boolean MainLoop = true;
    //每16ms更新一次
    static long MS_PER_UPDATE = 16;
    static Vector3i COLOR_WHITE = new Vector3i(255, 255, 255);
    //平行光向量
    static Vector3f LIGHT_DIR = new Vector3f(0, 0, -1);

    public static void main(String[] args) {

        Vector2i terminalSize = new Vector2i(120, 50);
        DisplayManager displayManager = new WindowsConsoleDisplayManager(terminalSize.X, terminalSize.Y);


        Model model = new Model(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), new Mesh(vertices, uvs, normals, faces));
        Model[] models = new Model[]{model};
        Camera camera = new Camera(new Vector3f(0, 0, 1.5f), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), 90, (float) terminalSize.X / terminalSize.Y, 0.5f, 50);
        SceneManager sceneManager = new SceneManager();
        sceneManager.addScene("main", new Scene(camera, Arrays.stream(models).collect(Collectors.toList())));
        sceneManager.switchScene("main");
        RenderManager renderManager = new RenderManager(sceneManager, terminalSize.X, terminalSize.Y);
        renderManager.setShader(new PhongShader(LIGHT_DIR, texture));
        renderManager.setClearColor(COLOR_WHITE);
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

            renderManager.render();
            frame++;

            //同步显示像素缓冲区的数据(将扫描缓冲区打印显示也加入到一帧中,否则会因为帧率大于显示刷新率而出现闪烁)
            displayManager.display(renderManager.getRenderBuffer());
            displayManager.drawText(0, displayManager.getHeight() - 2, COLOR_WHITE, "摄影机: w,s,a,d,z,c 前后左右上下, i,k,j,l 上下左右摇头 | x 退出");
            displayManager.drawText(0, displayManager.getHeight() - 1, COLOR_WHITE, framesPerSecond + "FPS | time:" + time / 1000);
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
                                camera.forward(0.1f);
                                break;
                            case 's':
                                camera.backward(0.1f);
                                break;
                            case 'a':
                                camera.left(0.1f);
                                break;
                            case 'd':
                                camera.right(0.1f);
                                break;
                            case 'z':
                                camera.up(0.1f);
                                break;
                            case 'c' :
                                camera.down(0.1f);
                                break;
                            case 'j':
                                camera.rotation(0, 10, 0);
                                break;
                            case 'l':
                                camera.rotation(0, -10, 0);
                                break;
                            case 'i':
                                camera.rotation(10, 0, 0);
                                break;
                            case 'k':
                                camera.rotation(-10, 0, 0);
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
