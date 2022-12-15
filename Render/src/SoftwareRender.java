import java.util.List;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:22
 **/
public class SoftwareRender {

    void drawTriangularMesh(Model model) {
        Mesh mesh = model.mesh;
        List<Vector3f> vertices = mesh.vertices;
        List<Vector3f> textures = mesh.textures;
        List<Vector3f> normals = mesh.normals;
        List<Face> faces = mesh.faces;
    }
}
