package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/17 17:38
 **/
public class TextureMapShader extends AbstractShader{

    Texture<Vector3i> texture;

    public TextureMapShader(Texture<Vector3i> texture) {
        this.texture = texture;
    }

    @Override
    public Vector4f vertex(Vertex vertex) {
        return v.multiply(m).multiply(vertex.pos);
    }

    @Override
    public Vector3i fragment(Vertex fragment) {
        return texture.getPixel((int) (fragment.texCoords.X * texture.width % texture.width), (int) (fragment.texCoords.Y * texture.height % texture.height));
    }
}
