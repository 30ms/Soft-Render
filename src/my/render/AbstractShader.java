package my.render;

import java.util.HashMap;
import java.util.Map;

/**
 * 着色器
 *
 * @author Liuzhenbin
 * @date 2024-03-22 18:30
 **/
public abstract class AbstractShader {

    //全局变量, 着色器可访问
    private final Map<String, Object> UNIFORMS = new HashMap<>();

    abstract Vector4f vertexShader(Vertex vertex);

    abstract Vector4f fragmentShader(Map<String, Texture> textures, Fragment f);

    public void putUniformV3f(String name, Vector3f v3f) {
        UNIFORMS.put(name, v3f);
    }

    public Vector3f getUniformV3f(String name) {
        return (Vector3f) UNIFORMS.get(name);
    }

    public void putUniformMat4x4(String name, Matrix4x4f mat) {
        UNIFORMS.put(name, mat);
    }

    public Matrix4x4f getUniformMat4x4(String name) {
        return (Matrix4x4f) UNIFORMS.get(name);
    }

}
