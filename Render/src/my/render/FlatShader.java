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
    public Vector4f vertex(Vertex vertex) {
        return v.multiply(m).multiply(vertex.pos);
    }

    @Override
    public Vector3i fragment(Vertex fragment) {
        return rgb;
    }
}
