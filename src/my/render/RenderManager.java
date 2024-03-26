package my.render;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/16 15:16
 **/
public class RenderManager {

    private SceneManager sceneManager;

    private final SoftwareRender render = SoftwareRender.INSTINCE;

    private final Queue<Model> renderObjectQueue = new ArrayDeque<>();

    public RenderManager(SceneManager sceneManager, int width, int height) {
        this.sceneManager = sceneManager;
        render.buildBuffer(width, height);
    }

    public void setShader(AbstractShader shader) {
        render.setShader(shader);
    }

    public void setClearColor(Vector4f color) {
        render.setClearColor(color);
    }

    public void render() {
        //重置缓冲区
        render.clearBuffers();

        Scene scene = sceneManager.getCurrentScene();
        Camera camera = scene.getMainCamera();
        //设置渲染队列为可见的模型队列
        scene.getModelsInScene().forEach(renderObjectQueue::offer);
        //设置全局变量
        render.shader.putUniformV3f("lightPos", scene.getLightPos());
        render.shader.putUniformV3f("lightColor", scene.getLightColor());
        render.shader.putUniformV3f("viewPos", camera.position);
        render.shader.putUniformMat4x4("viewMat", camera.viewMat);
        render.shader.putUniformMat4x4("projMat", camera.projectionMat);

        //渲染模型
        while (!renderObjectQueue.isEmpty()) {
            Model model = renderObjectQueue.poll();
            if (model != null) {
                //设置模型变换矩阵全局变量
                Matrix4x4f modelMat = Matrix4x4f.translation(model.position)
                        .multiply(Matrix4x4f.scale(model.scale))
                        .multiply(Matrix4x4f.rotationX(model.rotation.X))
                        .multiply(Matrix4x4f.rotationY(model.rotation.Y))
                        .multiply(Matrix4x4f.rotationZ(model.rotation.Z));
                render.shader.putUniformMat4x4("modelMat", modelMat);
                //向量不能直接应用模型变换, 需要取逆然后转置 (m^-1)T
                render.shader.putUniformMat4x4("normalMat", modelMat.inverse().transpose());
                Mesh mesh = model.getMesh();
                //绑定纹理
                render.TEXTURES.putAll(mesh.getTextures());

                //遍历模型的每个面
                for (Face face : mesh.getFaces()) {
                    Vertex[] vertices = new Vertex[face.vertexIndices.length];
                    for (int i = 0; i < face.vertexIndices.length; i++) {
                        vertices[i] = new Vertex();
                        vertices[i].pos = new Vector4f(mesh.getVertices()[face.vertexIndices[i]]);
                    }
                    for (int i = 0; i < face.uvIndices.length; i++) {
                        vertices[i].texCoords = mesh.getUVs()[face.uvIndices[i]];
                    }
                    for (int i = 0; i < face.normalsIndices.length; i++) {
                        vertices[i].normal = mesh.getNormals()[face.normalsIndices[i]];
                    }
                    //渲染模型的三角形面
                    render.drawTriangular(vertices);
                }
            }
        }
        //交换显示的像素缓冲区
        render.swapBuffers();
    }

    public Buffer<Vector4f> getRenderBuffer() {
        return render.getCurrentBuffer();
    }
}
