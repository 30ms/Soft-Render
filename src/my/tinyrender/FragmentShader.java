package my.tinyrender;

import my.render.Vector4f;

import java.util.Map;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/3/31 16:12
 **/
public interface FragmentShader {

    Vector4f shader(Map<String, float[]> varyings, Map<String, float[]> uniforms);

}

