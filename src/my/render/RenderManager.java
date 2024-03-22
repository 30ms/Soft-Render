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

        //构建待渲染的模型、摄像机
        buildRenderQueue();

        //渲染模型
        while (!renderObjectQueue.isEmpty()) {
            Model model = renderObjectQueue.poll();
            if (model != null) {
                //渲染模型的三角形面网格
                render.drawTriangularMesh(model);
            }
        }
        //交换显示的像素缓冲区
        render.swapBuffers();
        //将相机设置为空以防场景发生变化
        render.setCamera(null);
    }

    public void buildRenderQueue() {
        Scene scene = sceneManager.getCurrentScene();
        render.setCamera(scene.getMainCamera());
        //设置渲染队列为可见的模型队列
        scene.getModelsInScene().forEach(renderObjectQueue::offer);
    }
    public Buffer<Vector4f> getRenderBuffer() {
        return render.getCurrentBuffer();
    }
}
