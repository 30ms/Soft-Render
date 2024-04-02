package my.render;

/**
 * 深度测试函数
 *
 * @author Liuzhenbin
 * @date 2024-04-02 10:42
 **/
public interface DepthTestFunc {

    boolean test(float bufferDepth, float depth);
    DepthTestFunc NEVER = (bufferDepth, depth) -> false;
    DepthTestFunc ALWAYS = (bufferDepth, depth) -> true;
    DepthTestFunc LESS = (bufferDepth, depth) -> depth < bufferDepth;
    DepthTestFunc LESS_EQUAL = (bufferDepth, depth) -> depth <= bufferDepth;
    DepthTestFunc GREATER = (bufferDepth, depth) -> depth > bufferDepth;
    DepthTestFunc GREATER_EQUAL = (bufferDepth, depth) -> depth >= bufferDepth;
    DepthTestFunc EQUAL = (bufferDepth, depth) -> depth == bufferDepth;
    DepthTestFunc NOT_EQUAL = (bufferDepth, depth) -> depth != bufferDepth;
}
