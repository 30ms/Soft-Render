package my.render;

/**
 * 颜色混合算法
 *
 * @author Liuzhenbin
 * @date 2024-04-02 11:06
 **/
public interface BlendFunc {
    Vector4f blend(Vector4f src, Vector4f dst);
    BlendFunc ADD = Vector4f::add;
    BlendFunc SUBTRACT = Vector4f::subtract;
    BlendFunc REVERSE_SUB = (src, dst) -> dst.subtract(src);
    BlendFunc MIN = Vector4f::min;
    BlendFunc MAX = Vector4f::max;
}
