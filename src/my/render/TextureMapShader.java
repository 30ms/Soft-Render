package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/17 17:38
 **/
public class TextureMapShader extends AbstractShader{

    Texture<Vector4f> texture;

    public TextureMapShader(Texture<Vector4f> texture) {
        this.texture = texture;
    }

    @Override
    public Vector4f vertex(Vertex vertex) {
        return v.multiply(m).multiply(vertex.pos);
    }

    @Override
    public Vector4f fragment(Vertex fragment) {
        return texture.sample(fragment.texCoords.X, fragment.texCoords.Y);
    }
}
