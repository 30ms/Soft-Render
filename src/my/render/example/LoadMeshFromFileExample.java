package my.render.example;

import my.render.*;

import java.util.Collections;
import java.util.List;

/**
 *  通过模型文件加载的示例
 *
 * @author Liuzhenbin
 * @date 2024-03-27 17:23
 **/
public class LoadMeshFromFileExample {
    public static void main(String[] args) {
        Vector2i windowSize = new Vector2i(800, 800);

        Vector3f lightPos = new Vector3f(0, 7, 10);
        Vector3f lightAmbient = new Vector3f(0.2f, 0.2f, 0.2f);
        Vector3f lightDiffuse = new Vector3f(1f, 1f, 1f);
        Vector3f lightSpecular = new Vector3f(1.0f, 1.0f, 1.0f);

        Camera camera = new Camera(
                new Vector3f(0, 7, 9),
                new Vector3f(0, 7, 0),
                new Vector3f(0, 1, 0),
                90,
                (float) windowSize.X / windowSize.Y,
                0.5f, 50);
        Model model = new Model();
        //模型沿Y轴旋转
        model.updateCallback = (m, time)-> m.rotation.Y += time * 0.001;
        //加载模型的网格数据
        model.meshes = Mesh.loadMeshes("model/nanosuit/nanosuit.obj");
        AbstractShader shader = new Shader();
        //模型的着色器设置
        model.setupCallback = (m, render)-> {
            render.shader = shader;
            render.shader.putUniformV3f("light.pos", lightPos);
            render.shader.putUniformV3f("light.ambient", lightAmbient);
            render.shader.putUniformV3f("light.diffuse", lightDiffuse);
            render.shader.putUniformV3f("light.specular", lightSpecular);
            render.shader.putUniformMat4x4("modelMat", m.getModelMatrix());
            render.shader.putUniformMat4x4("viewMat", camera.getViewMat());
            render.shader.putUniformMat4x4("projMat", camera.getProjectionMat());
            render.shader.putUniformMat4x4("normalMat", m.getModelMatrix().inverse().transpose());
            render.shader.putUniformV3f("viewPos", camera.position);
        };
        //模型网格的着色器设置
        model.meshes.forEach(mesh -> mesh.setupCallback= (m, render) ->{
            render.shader.putUniformV3f("material.ambient", m.material.ambient);
            render.shader.putUniformV3f("material.diffuse", m.material.diffuse);
            render.shader.putUniformV3f("material.specular", m.material.specular);
            render.shader.putUniformFloat("material.shininess", m.material.shininess);
            render.TEXTURES.add(0, m.material.diffuseTexture);
            render.shader.putUniformInt("material.diffuseTexture", 0);
            if (m.material.specularTexture != null) {
                render.TEXTURES.add(1, m.material.specularTexture);
                render.shader.putUniformInt("material.specularTexture", 1);
            } else {
                render.shader.putUniformInt("material.specularTexture", -1);
            }
        });

        SceneManager sceneManager = new SceneManager();
        sceneManager.addScene("main", new Scene(Collections.singletonList(model)));
        sceneManager.switchScene("main");
        RenderManager renderManager = new RenderManager(sceneManager, windowSize.X, windowSize.Y);
        renderManager.setClearColor(new Vector4f(0.2f, 0.3f, 0.3f, 1.0f));
        RenderLoopSimpleImpl renderLoop = new RenderLoopSimpleImpl(renderManager, new SwingFrameDisplayManager(windowSize.X, windowSize.Y));

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
     * 着色器
     */
    private static class Shader extends AbstractShader {

        @Override
        protected Vector4f vertexShader(Vertex vertex) {
            Matrix4x4f
                    m = getUniformMat4x4("modelMat"),
                    v = getUniformMat4x4("viewMat"),
                    p = getUniformMat4x4("projMat"),
                    n = getUniformMat4x4("normalMat");
            vertex.putV2fVarying("texCoords", vertex.texCoords);
            //世界坐标系下的法向量
            vertex.putV3fVarying("norm", n.multiply(new Vector4f(vertex.normal)).toVector3f().normalize());
            //世界坐标系下的顶点坐标
            vertex.putV3fVarying("pos", m.multiply(vertex.pos).toVector3f());
            return p.multiply(v).multiply(m).multiply(vertex.pos);
        }

        @Override
        protected Vector4f fragmentShader(List<Texture<Vector4f>> textures, Fragment f) {
            Vector3f
                    //材质的属性
                    materialAmbient = getUniformV3f("material.ambient"),
                    materialDiffuse = getUniformV3f("material.diffuse"),
                    materialSpecular = getUniformV3f("material.specular"),
                    //光源的属性
                    lightPos = getUniformV3f("light.pos"),
                    lightAmbient = getUniformV3f("light.ambient"),
                    lightDiffuse = getUniformV3f("light.diffuse"),
                    lightSpecular = getUniformV3f("light.specular"),

                    viewPos = getUniformV3f("viewPos");

            Vector3f
                    norm = f.getV3fVarying("norm"),
                    pos = f.getV3fVarying("pos");

            Vector3f
                    //从点到光源的方向
                    lightDir = lightPos.subtract(pos).normalize(),
                    //从点到视点的方向
                    viewDir = viewPos.subtract(pos).normalize(),
                    //光的反射向量(反射计算需要从光源到顶点的方向)
//                    reflectDir = lightDir.multiply(-1).reflect(norm),
                    //半角向量
                    half = lightDir.add(viewDir).normalize();

            //计算漫反射因子
            float diff = Math.max(0, norm.dotProduct(lightDir));
            //计算镜面光因子
            float spec = (float) Math.pow(Math.max(0, norm.dotProduct(half)), getUniformFloat("material.shininess"));
            //纹理坐标
            Vector2f texCoords = f.getV2fVarying("texCoords");
            Vector4f
                    //材质纹理
                    diffuseTextureColor = textures.get(getUniformInt("material.diffuseTexture")).sample(texCoords),
                    //材质高光纹理
                    specularTextureColor;
            if (getUniformInt("material.specularTexture") != -1) {
                specularTextureColor = textures.get(getUniformInt("material.specularTexture")).sample(texCoords);
            } else {
                specularTextureColor = new Vector4f(0, 0, 0, 1);
            }

            Vector3f materialAmbientColor = diffuseTextureColor.toVector3f().multiply(lightAmbient).multiply(materialAmbient);
            Vector3f materialDiffuseColor = diffuseTextureColor.toVector3f().multiply(diff).multiply(lightDiffuse).multiply(materialDiffuse);
            Vector3f materialSpecularColor = specularTextureColor.toVector3f().multiply(spec).multiply(lightSpecular).multiply(materialSpecular);

            return new Vector4f(materialAmbientColor.add(materialDiffuseColor).add(materialSpecularColor), 1);
        }
    }
}
