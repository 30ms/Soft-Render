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

    private DisplayManager displayManager;

    private SceneManager sceneManager;

    private final SoftwareRender render = SoftwareRender.INSTINCE;

    private final Queue<Model> renderObjectQueue = new ArrayDeque<>();

    public RenderManager(DisplayManager displayManager, SceneManager sceneManager) {
        this.displayManager = displayManager;
        this.sceneManager = sceneManager;
        render.buildBuffer(displayManager.getWidth(), displayManager.getHeight());
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
        displayManager.swapBuffer(render.getRenderTarget());

        //将相机设置为空以防场景发生变化
        render.setCamera(null);
    }

    public void buildRenderQueue() {
        Scene scene = sceneManager.getCurrentScene();
        render.setCamera(scene.getMainCamera());
        //设置渲染队列为可见的模型队列
        scene.getModelsInScene().forEach(renderObjectQueue::offer);
    }
}