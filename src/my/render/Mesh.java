package my.render;

import java.util.Map;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:12
 **/
public class Mesh {
    public Vector3f[] vertices;
    public Vector2f[] uvs;
    public Vector3f[] normals;
    public Face[] faces;
    public Map<String, Texture<Vector4f>> textures;

    public Mesh(Vector3f[] vertices, Vector2f[] uvs, Vector3f[] normals, Face[] faces, Map<String, Texture<Vector4f>> textures) {
        this.vertices = vertices;
        this.uvs = uvs;
        this.normals = normals;
        this.faces = faces;
        this.textures = textures;
    }
}
