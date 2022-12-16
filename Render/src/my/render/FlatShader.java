package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/16 18:25
 **/
public class FlatShader extends AbstractShader {

    Vector3i rgb = new Vector3i(255, 255, 255);


    @Override
    public Vector4f vertex(int index, Vector4f vertex, Vector3f normal, Vector3f textureValues) {
        return p.multiply(v).multiply(m).multiply(vertex);
    }

    @Override
    public Vector3i fragment(Vector3f barycentric) {
        return rgb;
    }
}
