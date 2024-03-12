package my.tinyrender;

import my.render.Matrix4x4f;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/4/8 16:23
 **/
public class FloatArrayToMatrix4x4f implements Convert<float[], Matrix4x4f> {
    @Override
    public Matrix4x4f convert(float[] floats) {
        return new Matrix4x4f(
                floats[0], floats[1], floats[2], floats[3],
                floats[4], floats[5], floats[6], floats[7],
                floats[8], floats[9], floats[10], floats[11],
                floats[12], floats[13], floats[14], floats[15]);
    }
}
