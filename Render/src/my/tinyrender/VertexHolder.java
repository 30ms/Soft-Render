package my.tinyrender;

import my.render.Vector3f;
import my.render.Vector4f;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/3/28 10:32
 **/
public class VertexHolder {
    boolean discard;
    //clip space position
    Vector4f clipPos;
    //screen space position
    Vector3f fragPos;
}
