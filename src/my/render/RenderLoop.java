package my.render;

/**
 * 渲染循环
 *
 * @author Liuzhenbin
 * @date 2024-03-27 9:55
 **/
public abstract class RenderLoop {
    private boolean isLooping = true;
    private long msPerUpdate;
    private final RenderMonitorInfo monitorInfo = new RenderMonitorInfo();

    public RenderLoop(long msPerUpdate) {
        this.msPerUpdate = msPerUpdate;
    }

    public void start() {
        long previous = System.currentTimeMillis();
        long lag = 0, lastSecondTime = 0, lastSecondFrame = 0;
        while (isLooping) {
            long current = System.currentTimeMillis();
            long elapsed = current - previous;
            previous = current;
            lag += elapsed;
            monitorInfo.time += elapsed;

            if (monitorInfo.time - lastSecondTime >= 1000) {
                monitorInfo.fps = monitorInfo.frame - lastSecondFrame;
                lastSecondFrame = monitorInfo.frame;
                lastSecondTime = monitorInfo.time;
            }

            processInput();

            while (lag >= msPerUpdate) {
                //场景更新
                processUpdate(lag);
                lag -= msPerUpdate;
            }

            processRending(monitorInfo);
            monitorInfo.frame++;
        }
    }

    public void stop() {
        isLooping = false;
    }

    public boolean isLooping() {
        return isLooping;
    }

    abstract protected void processInput();
    abstract protected void processUpdate(long delta);
    abstract protected void processRending(RenderMonitorInfo monitorInfo);

    static protected class RenderMonitorInfo {
        long frame;
        long time;
        long fps;
    }
}
