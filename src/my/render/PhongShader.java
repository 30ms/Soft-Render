package my.render;

/**
 * Phong着色
 *
 * @author Liuzhenbin
 * @date 2024-03-12 15:38
 **/
public class PhongShader extends AbstractShader{

    private Vector3f lightDir;
    private Texture<Vector4f> texture;

    public PhongShader(Vector3f lightDir, Texture<Vector4f> texture) {
        this.lightDir = lightDir;
        this.texture = texture;
    }

    @Override
    public Vector4f vertex(Vertex vertex) {
        Matrix4x4f mv = v.multiply(m);
        //mv变换法向量
        vertex.normal = mv.multiply(new Vector4f(vertex.normal,0)).toVector3f();
        return mv.multiply(vertex.pos);
    }

    @Override
    public Vector4f fragment(Vertex fragment) {
        Vector4f color = texture.sample(fragment.texCoords.X, fragment.texCoords.Y);
        //光照向量的视图空间坐标
        Vector3f ligDir_view = v.multiply(new Vector4f(lightDir, 0)).toVector3f();
        ligDir_view.normalized();
        //计算光强
        float intensity = ligDir_view.scale(-1).dotProduct(fragment.normal);
        intensity = Math.max(0, intensity);
        return new Vector4f(color.X * intensity, color.Y * intensity, color.Z * intensity, color.W);
    }

}
