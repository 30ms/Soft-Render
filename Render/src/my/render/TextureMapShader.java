package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/17 17:38
 **/
public class TextureMapShader extends AbstractShader{

    Texture<Vector3i> texture;
    Vector2f[] uvs = new Vector2f[3];
    Vector3f[] normals = new Vector3f[3];

    public TextureMapShader(Texture<Vector3i> texture) {
        this.texture = texture;
    }

    @Override
    public Vector4f vertex(int index, Vector4f vertex, Vector3f normal, Vector2f uv) {
        uvs[index] = uv;
        normals[index] = normal;
        return p.multiply(v).multiply(m).multiply(vertex);
    }

    @Override
    public Vector3i fragment(Vector3f barycentric) {
        int u = (int) (barycentric.X * uvs[0].X + barycentric.Y * uvs[1].X + barycentric.Z * uvs[2].X);
        int v = (int) (barycentric.X * uvs[0].Y + barycentric.Y * uvs[1].Y + barycentric.Z * uvs[2].Y);
        return texture.getPixel(u, v);
    }
}
