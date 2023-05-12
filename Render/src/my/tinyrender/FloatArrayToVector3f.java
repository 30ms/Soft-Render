package my.tinyrender;

import my.render.Vector3f;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/4/7 18:26
 **/
public class FloatArrayToVector3f implements Convert<float[], Vector3f>{

    @Override
    public Vector3f convert(float[] floats) {
        return new Vector3f(floats[0], floats[1], floats[2]);
    }

}
