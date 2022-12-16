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
    Matrix4x4f p;
    Vector4f cameraPosition;

    abstract public Vector4f vertex(int index, Vector4f vertex, Vector3f normal, Vector3f textureValues);

    abstract public Vector3i fragment(Vector3f barycentric);
}
