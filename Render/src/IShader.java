/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:40
 **/
public interface IShader {
    Vector3f vertex(int index, Vector3f vertex, Vector3f normal, Vector3f textureValues, Vector3f light);

    Vector3i fragment(Vector3f barycentric);
}
