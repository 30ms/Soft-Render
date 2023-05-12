package my.tinyrender;

import my.render.Vector4f;

import java.util.Map;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/3/31 16:31
 **/
public interface VertexShader {

    Vector4f shader(VertexArrayObject vao, int vertexIndex, Map<String, float[]> varyings, Map<String, float[]> uniforms);

}
