package my.tinyrender;

import my.render.Vector4f;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/3/28 17:23
 **/
public class RenderState {

    boolean depthTest = false;
    DepthFunction depthFunc = DepthFunction.DepthFunc_LESS;

    boolean cullFace = false;
    PolygonMode polygonMode = PolygonMode.PolygonMode_FILL;

    float lineWidth = 1.f;
    float pointSize = 1.f;

    ClearState clearState = new ClearState();

    public enum PolygonMode {
        PolygonMode_POINT,
        PolygonMode_LINE,
        PolygonMode_FILL,
    };

    public static class ClearState {
        boolean depthFlag = true;
        boolean colorFlag = true;
        Vector4f clearColor = new Vector4f(0, 0, 0, 0);
    };

    public enum DepthFunction {
        DepthFunc_NEVER,
        DepthFunc_LESS,
        DepthFunc_EQUAL,
        DepthFunc_LEQUAL,
        DepthFunc_GREATER,
        DepthFunc_NOTEQUAL,
        DepthFunc_GEQUAL,
        DepthFunc_ALWAYS,
    };
}
