package my.render;

import java.util.Map;

/**
 * Phong光照模型着色
 *
 * @author Liuzhenbin
 * @date 2024-03-12 15:38
 **/
public class PhongShader extends AbstractShader{

    @Override
    protected Vector4f vertexShader(Vertex vertex) {
        Matrix4x4f
                m = getUniformMat4x4("modelMat"),
                v = getUniformMat4x4("viewMat"),
                p = getUniformMat4x4("projMat"),
                normalMat = getUniformMat4x4("normalMat");
        //设置着色器变量
        vertex.putV2fVarying("textureCoords", vertex.texCoords);
        Vector3f norm = normalMat.multiply(new Vector4f(vertex.normal)).toVector3f();
        norm.normalized();
        vertex.putV3fVarying("norm", norm);
        //顶点的世界坐标
        vertex.putV3fVarying("pos", m.multiply(vertex.pos).toVector3f());
        return p.multiply(v).multiply(m).multiply(vertex.pos);
    }

    @Override
    public Vector4f fragmentShader(Map<String, Texture> textures, Fragment frag) {
        Texture<Vector4f> texture = textures.get("texture");
        Vector4f color = texture.sample(frag.getV2fVarying("textureCoords"));

        Vector3f lightPos = getUniformV3f("lightPos");
        Vector3f lightColor = getUniformV3f("lightColor");
        Vector3f pos = frag.getV3fVarying("pos");
        Vector3f norm = frag.getV3fVarying("norm");
        Vector3f viewPos = getUniformV3f("viewPos");

        //环境光强度
        float ambientStrength = 0.1f;
        //环境光
        Vector3f ambient = lightColor.scale(ambientStrength);

        //光照向量(从顶点指向光源)
        Vector3f ligDir = lightPos.reduce(pos);
        ligDir.normalized();
        float diff = Math.max(0, norm.dotProduct(ligDir));
        //漫反射光
        Vector3f diffuse = lightColor.scale(diff);

        //镜面强度
        float specularStrength = 0.3f;
        //反光度
        int shininess = 32;
        //视角向量(顶点到相机的方向)
        Vector3f viewDir = viewPos.reduce(pos);
        viewDir.normalized();
        //光的反射向量(反射计算需要从光源到顶点的方向)
        Vector3f reflectDir = ligDir.scale(-1).reflect(norm);
        float spec = (float) Math.pow(Math.max(0, viewDir.dotProduct(reflectDir)), shininess);
        //镜面反射光
        Vector3f specular = lightColor.scale(specularStrength * spec);

        return new Vector4f(
                (ambient.X + diffuse.X + specular.X) * color.X,
                (ambient.Y + diffuse.Y + specular.Y) * color.Y,
                (ambient.Z + diffuse.Z + specular.Z) * color.Z,
                color.W);
    }

}
