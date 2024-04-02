package my.render;

/**
 * 混合因子
 *
 * @author Liuzhenbin
 * @date 2024-04-02 11:44
 **/
public interface BlendFactor {

    Vector4f factor(Vector4f src, Vector4f dst);

    BlendFactor ONE = (src, dst) -> new Vector4f(1, 1, 1, 1);
    BlendFactor ZERO = (src, dst) -> new Vector4f(0, 0, 0, 0);
    BlendFactor SRC_COLOR = (src, dst) -> new Vector4f(src.X, src.Y, src.Z, 1);
    BlendFactor DST_COLOR = (src, dst) -> new Vector4f(dst.X, dst.Y, dst.Z, 1);
    BlendFactor ONE_MINUS_SRC_COLOR = (src, dst) -> new Vector4f(1, 1, 1, 1).subtract(src);
    BlendFactor ONE_MINUS_DST_COLOR = (src, dst) -> new Vector4f(1, 1, 1, 1).subtract(dst);
    BlendFactor SRC_ALPHA = (src, dst) -> new Vector4f(src.W, src.W, src.W, src.W);
    BlendFactor DST_ALPHA = (src, dst) -> new Vector4f(dst.W, dst.W, dst.W, dst.W);
    BlendFactor ONE_MINUS_SRC_ALPHA = (src, dst) -> new Vector4f(1, 1, 1, 1).subtract(new Vector4f(src.W, src.W, src.W, src.W));
    BlendFactor ONE_MINUS_DST_ALPHA = (src, dst) -> new Vector4f(1, 1, 1, 1).subtract(new Vector4f(dst.W, dst.W, dst.W, dst.W));
}
