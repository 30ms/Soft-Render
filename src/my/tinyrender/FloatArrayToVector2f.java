package my.tinyrender;

import my.render.Vector2f;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/4/8 15:26
 **/
public class FloatArrayToVector2f implements Convert<float[], Vector2f>{
    @Override
    public Vector2f convert(float[] floats) {
        return new Vector2f(floats[0], floats[1]);
    }
}
