package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:40
 **/
public abstract class AbstractShader {
    Matrix4x4f m;
    Matrix4x4f v;

    abstract public Vector4f vertex(Vertex vertex);

    abstract public Vector4f fragment(Vertex fragment);
}
