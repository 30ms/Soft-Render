package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/16 18:25
 **/
public class FlatShader extends AbstractShader {

    Vector4f rgba = new Vector4f(1, 1, 1, 1);


    @Override
    public Vector4f vertex(Vertex vertex) {
        return v.multiply(m).multiply(vertex.pos);
    }

    @Override
    public Vector4f fragment(Vertex fragment) {
        return rgba;
    }
}
