package my.render;

import java.util.HashMap;
import java.util.Map;

/**
 * 片元
 *
 * @author Liuzhenbin
 * @date 2024-03-22 16:12
 **/
public class Fragment {

    //片元位置
    public Vector3f pos;

    //着色器变量
   Map<String, float[]> shaderVaryings = new HashMap<>();

    public Vector2f getV2fVarying(String name) {
        float[] v = shaderVaryings.get(name);
        return new Vector2f(v[0], v[1]);
    }

    public void putV2fVarying(String name, Vector2f vector2f) {
        shaderVaryings.put(name, new float[]{vector2f.X, vector2f.Y});
    }

    public Vector3f getV3fVarying(String name) {
        float[] v = shaderVaryings.get(name);
        return new Vector3f(v[0], v[1], v[2]);
    }
    public void putV3fVarying(String name, Vector3f vector3f) {
        shaderVaryings.put(name, new float[]{vector3f.X, vector3f.Y, vector3f.Z});
    }
}
