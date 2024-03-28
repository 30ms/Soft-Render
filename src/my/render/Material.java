package my.render;

/**
 * 材质
 *
 * @author Liuzhenbin
 * @date 2024-03-27 19:43
 **/
public class Material {

    /**
     * 材质环境光系数
     */
    public Vector3f ambient;

    /**
     * 材质漫反射系数
     */
    public Vector3f diffuse;

    /**
     * 材质镜面反射系数
     */
    public Vector3f specular;

    /**
     * 材质的镜面反射指数
     */
    public float shininess;

    public Texture<Vector4f> diffuseTexture;

    public Texture<Vector4f> specularTexture;
}
