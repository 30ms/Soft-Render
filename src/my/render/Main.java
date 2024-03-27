package my.render;

import java.util.Collections;

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

    static Texture<Vector4f> texture = Texture.loadFromFile("img/wood_box.jpg");
    //光源位置
    static Vector3f LIGHT_POS = new Vector3f(0.5f, 0.5f, 2);
    static Vector3f LIGHT_COLOR = new Vector3f(1, 1, 1);

    public static void main(String[] args) {

        Vector2i terminalSize = new Vector2i(50, 50);
        DisplayManager displayManager = new WindowsConsoleDisplayManager(terminalSize.X, terminalSize.Y);

        AbstractShader shader = new PhongShader();
        Model model = new Model(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1),
                new Vector3f(0, 0, 0),
                new Mesh(vertices,
                        uvs,
                        normals,
                        faces,
                        Collections.singletonMap("texture", texture)),
                shader){
            @Override
            public void update(long delta) {
                rotation.Y += delta * 0.003;
                rotation.X += delta * 0.003;
                rotation.Z += delta * 0.003;
            }
        };
        Camera camera = new Camera(new Vector3f(0, 0, 1.5f), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), 90, (float) terminalSize.X / terminalSize.Y, 0.5f, 50);
        //模型的着色器设置
        model.setupShader = (s) -> {
            //设置着色器参数
            s.putUniformV3f("lightPos", LIGHT_POS);
            s.putUniformV3f("lightColor", LIGHT_COLOR);
            s.putUniformMat4x4("modelMat", model.getModelMatrix());
            s.putUniformMat4x4("viewMat", camera.getViewMat());
            s.putUniformMat4x4("projMat", camera.getProjectionMat());
            s.putUniformMat4x4("normalMat", model.getModelMatrix().inverse().transpose());
            s.putUniformV3f("viewPos", camera.position);
        };
        SceneManager sceneManager = new SceneManager();
        sceneManager.addScene("main", new Scene(Collections.singletonList(model)));
        sceneManager.switchScene("main");
        RenderManager renderManager = new RenderManager(sceneManager, terminalSize.X, terminalSize.Y);
        renderManager.setClearColor(new Vector4f(1,1,1,1));

        RenderLoopSimpleImpl renderLoop = new RenderLoopSimpleImpl(renderManager, displayManager) {

            @Override
            protected void processRending(RenderMonitorInfo monitorInfo) {
                super.processRending(monitorInfo);
                //显示文字
                getDisplayManager().drawText(0, displayManager.getHeight() - 2, new Vector3i(255, 255, 255), "摄影机: w,s,a,d,z,c 前后左右上下, i,k,j,l 上下左右摇头 | x 退出");
                getDisplayManager().drawText(0, displayManager.getHeight() - 1, new Vector3i(255, 255, 255), monitorInfo.fps + "FPS | time:" + monitorInfo.time / 1000);
            }
        };
        //注册按键控制监听
        renderLoop.addKeyListener('w', () -> camera.forward(0.1f));
        renderLoop.addKeyListener('s', () -> camera.backward(0.1f));
        renderLoop.addKeyListener('a', () -> camera.left(0.1f));
        renderLoop.addKeyListener('d', () -> camera.right(0.1f));
        renderLoop.addKeyListener('z', () -> camera.up(0.1f));
        renderLoop.addKeyListener('c', () -> camera.down(0.1f));
        renderLoop.addKeyListener('j', () -> camera.rotation(0, 10, 0));
        renderLoop.addKeyListener('l', () -> camera.rotation(0, -10, 0));
        renderLoop.addKeyListener('i', () -> camera.rotation(10, 0, 0));
        renderLoop.addKeyListener('k', () -> camera.rotation(-10, 0, 0));
        //退出循环
        renderLoop.addKeyListener('x', renderLoop::stop);

        renderLoop.start();
    }

}
