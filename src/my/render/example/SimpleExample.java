package my.render.example;

import my.render.*;

import java.util.Collections;
import java.util.List;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/17 10:56
 **/
public class SimpleExample {

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

    static AbstractShader shader = new PhongShader();

    public static void main(String[] args) {

        Vector2i terminalSize = new Vector2i(50, 50);
        DisplayManager displayManager = new WindowsConsoleDisplayManager(terminalSize.X, terminalSize.Y);

        Camera camera = new Camera(new Vector3f(0, 0, 1.5f), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), 90, (float) terminalSize.X / terminalSize.Y, 0.5f, 50);
        Mesh mesh = new Mesh(vertices, uvs, normals, faces);
        mesh.setupCallback = (m, render) -> {
            //绑定纹理
            render.TEXTURES.add(0, texture);
            render.shader.putUniformInt("texture", 0);
        };

        Model model = new Model(
                new Vector3f(0, 0, 0),
                new Vector3f(1, 1, 1),
                new Vector3f(0, 0, 0),
                mesh);
        model.setupCallback = (m, render)-> {
            render.shader = shader;
            render.shader.putUniformV3f("lightPos", LIGHT_POS);
            render.shader.putUniformV3f("lightColor", LIGHT_COLOR);
            render.shader.putUniformMat4x4("modelMat", m.getModelMatrix());
            render.shader.putUniformMat4x4("viewMat", camera.getViewMat());
            render.shader.putUniformMat4x4("projMat", camera.getProjectionMat());
            render.shader.putUniformMat4x4("normalMat", m.getModelMatrix().inverse().transpose());
            render.shader.putUniformV3f("viewPos", camera.position);
        };
        model.updateCallback = (m, delta) -> {
            m.rotation.Y += delta * 0.003;
            m.rotation.X += delta * 0.003;
            m.rotation.Z += delta * 0.003;
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
                getDisplayManager().drawText(0, displayManager.getHeight() - 1, new Vector3i(255, 255, 255), monitorInfo.getFps() + "FPS | time:" + monitorInfo.getTime() / 1000);
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

    /**
     * Phong光照模型着色
     *
     * @author Liuzhenbin
     * @date 2024-03-12 15:38
     **/
    private static class PhongShader extends AbstractShader{

        @Override
        protected Vector4f vertexShader(Vertex vertex) {
            Matrix4x4f
                    m = getUniformMat4x4("modelMat"),
                    v = getUniformMat4x4("viewMat"),
                    p = getUniformMat4x4("projMat"),
                    normalMat = getUniformMat4x4("normalMat");
            //设置着色器变量
            vertex.putV2fVarying("textureCoords", vertex.texCoords);
            Vector3f norm = normalMat.multiply(new Vector4f(vertex.normal)).toVector3f();
            norm.normalized();
            vertex.putV3fVarying("norm", norm);
            //顶点的世界坐标
            vertex.putV3fVarying("pos", m.multiply(vertex.pos).toVector3f());
            return p.multiply(v).multiply(m).multiply(vertex.pos);
        }

        @Override
        public Vector4f fragmentShader(List<Texture<Vector4f>> textures, Fragment frag) {
            Texture<Vector4f> texture = textures.get(getUniformInt("texture"));
            Vector4f color = texture.sample(frag.getV2fVarying("textureCoords"));

            Vector3f lightPos = getUniformV3f("lightPos");
            Vector3f lightColor = getUniformV3f("lightColor");
            Vector3f pos = frag.getV3fVarying("pos");
            Vector3f norm = frag.getV3fVarying("norm");
            Vector3f viewPos = getUniformV3f("viewPos");

            //环境光强度
            float ambientStrength = 0.1f;
            //环境光
            Vector3f ambient = lightColor.multiply(ambientStrength);

            //光照向量(从顶点指向光源)
            Vector3f ligDir = lightPos.subtract(pos);
            ligDir.normalized();
            float diff = Math.max(0, norm.dotProduct(ligDir));
            //漫反射光
            Vector3f diffuse = lightColor.multiply(diff);

            //镜面强度
            float specularStrength = 0.3f;
            //反光度
            int shininess = 32;
            //视角向量(顶点到相机的方向)
            Vector3f viewDir = viewPos.subtract(pos);
            viewDir.normalized();
            //光的反射向量(反射计算需要从光源到顶点的方向)
            Vector3f reflectDir = ligDir.multiply(-1).reflect(norm);
            float spec = (float) Math.pow(Math.max(0, viewDir.dotProduct(reflectDir)), shininess);
            //镜面反射光
            Vector3f specular = lightColor.multiply(specularStrength * spec);

            return new Vector4f(
                    (ambient.X + diffuse.X + specular.X) * color.X,
                    (ambient.Y + diffuse.Y + specular.Y) * color.Y,
                    (ambient.Z + diffuse.Z + specular.Z) * color.Z,
                    color.W);
        }

    }
}
