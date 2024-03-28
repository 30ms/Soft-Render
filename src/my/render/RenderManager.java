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

    public void setClearColor(Vector4f color) {
        render.setClearColor(color);
    }

    public void render() {
        //重置缓冲区
        render.clearBuffers();

        Scene scene = sceneManager.getCurrentScene();
        //设置渲染队列为可见的模型队列
        scene.getModelsInScene().forEach(renderObjectQueue::offer);
        //渲染模型
        while (!renderObjectQueue.isEmpty()) {
            Model model = renderObjectQueue.poll();
            if (model != null) {
                model.setupCallback.accept(model, render);
                for (Mesh mesh : model.meshes) {
                    mesh.setupCallback.accept(mesh, render);
                    //遍历模型的每个面
                    for (Face face : mesh.faces) {
                        Vertex[] vertices = new Vertex[face.vertexIndices.size()];
                        for (int i = 0; i < face.vertexIndices.size(); i++) {
                            vertices[i] = new Vertex();
                            vertices[i].pos = new Vector4f(mesh.vertices.get(face.vertexIndices.get(i)));
                        }
                        for (int i = 0; i < face.uvIndices.size(); i++) {
                            vertices[i].texCoords = mesh.uvs.get(face.uvIndices.get(i));
                        }
                        for (int i = 0; i < face.normalsIndices.size(); i++) {
                            vertices[i].normal = mesh.normals.get(face.normalsIndices.get(i));
                        }
                        //渲染模型的三角形面
                        render.drawTriangular(vertices);
                    }
                }
            }
        }
        //交换显示的像素缓冲区
        render.swapBuffers();
    }

    public Buffer<Vector4f> getRenderBuffer() {
        return render.getCurrentBuffer();
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
