package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:12
 **/
public class Mesh {
    private Vector3f[] vertices;
    private Vector2f[] uvs;
    private Vector3f[] normals;
    private Face[] faces;

    public Mesh(Vector3f[] vertices, Vector2f[] uvs, Vector3f[] normals, Face[] faces) {
        this.vertices = vertices;
        this.uvs = uvs;
        this.normals = normals;
        this.faces = faces;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3f[] vertices) {
        this.vertices = vertices;
    }

    public Vector2f[] getUVs() {
        return uvs;
    }

    public void setUVs(Vector2f[] uvs) {
        this.uvs = uvs;
    }

    public Vector3f[] getNormals() {
        return normals;
    }

    public void setNormals(Vector3f[] normals) {
        this.normals = normals;
    }

    public Face[] getFaces() {
        return faces;
    }

    public void setFaces(Face[] faces) {
        this.faces = faces;
    }
}
