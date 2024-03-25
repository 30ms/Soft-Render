package my.render;

import java.util.HashMap;
import java.util.Map;

/**
 * 顶点
 *
 * @author Liuzhenbin
 * @date 2024-03-08 19:42
 **/
public class Vertex {

    //顶点位置属性
    Vector4f pos;

    //顶点法向量属性
    Vector3f normal;

    //顶点纹理坐标属性
    Vector2f texCoords;

    //着色器变量
   Map<String, float[]> shaderVaryings = new HashMap<>();

   public void putV2fVarying(String name, Vector2f vector2f) {
       shaderVaryings.put(name, new float[]{vector2f.X, vector2f.Y});
   }

    public void putV3fVarying(String name, Vector3f vector3f) {
        shaderVaryings.put(name, new float[]{vector3f.X, vector3f.Y, vector3f.Z});
    }
}
