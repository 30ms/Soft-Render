package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:12
 **/
public class Mesh {
    private Vector3f[] vertices;
    private Vector3f[] textures;
    private Vector3f[] normals;
    private Face[] faces;

    public Mesh(Vector3f[] vertices, Vector3f[] textures, Vector3f[] normals, Face[] faces) {
        this.vertices = vertices;
        this.textures = textures;
        this.normals = normals;
        this.faces = faces;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3f[] vertices) {
        this.vertices = vertices;
    }

    public Vector3f[] getTextures() {
        return textures;
    }

    public void setTextures(Vector3f[] textures) {
        this.textures = textures;
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
