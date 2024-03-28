package my.render.example;

import my.render.*;

import java.util.*;

/**
 * 材质phong着色实例
 *
 * @author Liuzhenbin
 * @date 2024-03-27 10:31
 **/
public class MaterialPhongExample {

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

    public static class PointLight {
        Vector3f position;
        //环境分量
        Vector3f ambient;
        //漫反射分量
        Vector3f diffuse;
        //镜面反射分量
        Vector3f specular;
    }

    static PointLight light;
    static {
        light = new PointLight();
        light.position = new Vector3f(0, 0, 10);
        light.ambient = new Vector3f(.1f, .1f, .1f);
        light.diffuse = new Vector3f(.5f, .5f, .5f);
        light.specular = new Vector3f(1, 1, 1);
    }

    /**
     * 着色器
     */
    private static class Shader extends AbstractShader {
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
        protected Vector4f fragmentShader(List<Texture<Vector4f>> textures, Fragment frag) {
            Vector3f lightPos = getUniformV3f("light.pos");
            Vector3f lightAmbient = getUniformV3f("light.ambient");
            Vector3f lightDiffuse = getUniformV3f("light.diffuse");
            Vector3f lightSpecular = getUniformV3f("light.specular");

            Vector3f pos = frag.getV3fVarying("pos");
            Vector3f norm = frag.getV3fVarying("norm");
            Vector2f texCoords = frag.getV2fVarying("textureCoords");
            Vector3f viewPos = getUniformV3f("viewPos");

            Vector4f diffuseColor = textures.get(getUniformInt("material.diffuseTexture")) .sample(texCoords);
            Vector4f specularColor = textures.get(getUniformInt("material.specularTexture")).sample(texCoords);
            //环境光
            Vector3f ambient = lightAmbient.multiply(diffuseColor.toVector3f());

            //光照向量(从顶点指向光源)
            Vector3f ligDir = lightPos.subtract(pos).normalize();
            float diff = Math.max(0, ligDir.dotProduct(norm));
            //漫反射光
            Vector3f diffuse = lightDiffuse.multiply(diffuseColor.toVector3f().multiply(diff));

            //视角向量(顶点到相机的方向)
            Vector3f viewDir = viewPos.subtract(pos);
            viewDir.normalized();
            //光的反射向量(反射计算需要从光源到顶点的方向)
            Vector3f reflectDir = ligDir.multiply(-1).reflect(norm);
            float spec = (float) Math.pow(Math.max(0, viewDir.dotProduct(reflectDir)), getUniformFloat("material.shininess"));
            //镜面反射光
            Vector3f specular = lightSpecular.multiply(specularColor.toVector3f().multiply(spec));

            return new Vector4f(ambient.add(diffuse.add(specular)), diffuseColor.W);
        }
    }

    public static void main(String[] args) {
        Vector2i windowSize = new Vector2i(400, 400);

        Shader shader = new Shader();
        Camera camera = new Camera(
                new Vector3f(0, 0, 5f),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 1, 0),
                90,
                (float) windowSize.X / windowSize.Y,
                0.5f, 50);
        List<Model> models = new ArrayList<>();
        Mesh mesh = new Mesh(vertices, uvs, normals, faces);
        mesh.material = new Material();
        mesh.material.diffuseTexture = Texture.loadFromFile("img/wood_box_diffuse.png");
        mesh.material.diffuseTexture.filtering = Texture.Filtering.BILINEAR;
        mesh.material.specularTexture = Texture.loadFromFile("img/wood_box_specular.png");
        mesh.material.shininess = 32;
        mesh.setupCallback = (m, render)-> {
            render.TEXTURES.add(0, m.material.diffuseTexture);
            render.TEXTURES.add(1, m.material.specularTexture);
            render.shader.putUniformInt("material.diffuseTexture", 0);
            render.shader.putUniformInt("material.specularTexture", 1);
            render.shader.putUniformFloat("material.shininess", m.material.shininess);
        };
        for (int i = 0; i < 3; i++) {
            Model model = new Model(
                    new Vector3f(-2 + i + 1, 0, i + 1),
                    new Vector3f(1, 1, 1),
                    new Vector3f(0, 0, 0),
                    mesh);
            model.updateCallback = (m, deltaTime) -> {
                //旋转
                m.rotation.Y += (float) (deltaTime * 0.003);
                m.rotation.X += (float) (deltaTime * 0.003);
                m.rotation.Z += (float) (deltaTime * 0.003);
            };
            model.setupCallback = (m, render) -> {
                render.shader = shader;
                render.shader.putUniformMat4x4("modelMat", m.getModelMatrix());
                render.shader.putUniformMat4x4("viewMat", camera.getViewMat());
                render.shader.putUniformMat4x4("projMat", camera.getProjectionMat());
                render.shader.putUniformMat4x4("normalMat",m.getModelMatrix().inverse().transpose());

                render.shader.putUniformV3f("viewPos", camera.position);
                render.shader.putUniformV3f("light.pos", light.position);
                render.shader.putUniformV3f("light.ambient", light.ambient);
                render.shader.putUniformV3f("light.diffuse", light.diffuse);
                render.shader.putUniformV3f("light.specular", light.specular);
            };
            models.add(model);
        }


        SceneManager sceneManager = new SceneManager();
        sceneManager.addScene("main", new Scene(models));
        sceneManager.switchScene("main");

        RenderManager renderManager = new RenderManager(sceneManager, windowSize.X, windowSize.Y);
        DisplayManager displayManager = new SwingFrameDisplayManager(windowSize.X, windowSize.Y);
        RenderLoopSimpleImpl renderLoop = new RenderLoopSimpleImpl(renderManager, displayManager) {
            @Override
            protected void processRending(RenderMonitorInfo monitorInfo) {
                super.processRending(monitorInfo);
                //显示文字
                getDisplayManager().drawText(0, displayManager.getHeight() - 2, new Vector3i(255,255,255), "摄影机: w,s,a,d,z,c 前后左右上下, i,k,j,l 上下左右摇头 | x 退出");
                getDisplayManager().drawText(0, displayManager.getHeight() - 1, new Vector3i(255,255,255), monitorInfo.getFps() + "FPS | time:" + monitorInfo.getFps() / 1000);
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
