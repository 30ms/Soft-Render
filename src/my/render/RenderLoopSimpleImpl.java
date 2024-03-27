package my.render;

import java.util.HashMap;
import java.util.Map;

/**
 *  渲染循环的简单实现
 *
 * @author Liuzhenbin
 * @date 2024-03-27 10:12
 **/
public class RenderLoopSimpleImpl extends RenderLoop {

    private RenderManager renderManager;
    private DisplayManager displayManager;
    private Map<Character, Runnable> keyListeners = new HashMap<>();
    private Thread inputThread;

    public RenderLoopSimpleImpl(RenderManager renderManager, DisplayManager displayManager) {
        super(16);
        this.renderManager = renderManager;
        this.displayManager = displayManager;
    }

    @Override
    protected void processInput() {
        if (inputThread == null) {
            inputThread = new Thread(() -> {
                while (isLooping()) {
                    try {
                        if (System.in.available() > 0) {
                            char key = (char) System.in.read();
                            Runnable runnable = keyListeners.get(key);
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                        Thread.sleep(167);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            inputThread.start();
        }
    }

    @Override
    protected void processUpdate(long delta) {
        renderManager.getSceneManager().updateScene(delta);
    }

    @Override
    protected void processRending(RenderMonitorInfo monitorInfo) {
        renderManager.render();
        //同步显示像素缓冲区的数据(将扫描缓冲区打印显示也加入到一帧中,否则会因为帧率大于显示刷新率而出现闪烁)
        displayManager.display(renderManager.getRenderBuffer());
    }

    public void addKeyListener(char key, Runnable runnable) {
        keyListeners.put(key, runnable);
    }

    public RenderManager getRenderManager() {
        return renderManager;
    }

    public DisplayManager getDisplayManager() {
        return displayManager;
    }
}
