package my.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:12
 **/
public class Mesh {

    public String name;
    public List<Vector3f> vertices;
    public List<Vector2f> uvs;
    public List<Vector3f> normals;
    public List<Face> faces;
    public Material material;
    public Mesh() {}

    public Mesh(Vector3f[] vertices, Vector2f[] uvs, Vector3f[] normals, Face[] faces) {
        this.vertices = Arrays.stream(vertices).collect(Collectors.toList());
        this.uvs = Arrays.stream(uvs).collect(Collectors.toList());
        this.normals = Arrays.stream(normals).collect(Collectors.toList());
        this.faces = Arrays.stream(faces).collect(Collectors.toList());
    }

    public BiConsumer<Mesh,SoftwareRender> setupCallback = (mesh, render) -> {};


    public static List<Mesh> loadMeshes(String path) {
        File file = new File(path);
        Mesh mesh = null;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Mesh> meshes = new ArrayList<>();
        Map<String, Material> materials = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "mtllib":
                        // 加载材质库
                        materials.putAll(loadMaterial(Paths.get(file.getParent(), parts[1]).toAbsolutePath().toString()));
                        break;
                    case "o":
                        mesh = new Mesh();
                        mesh.name = parts[1];
                        mesh.vertices = vertices;
                        mesh.uvs = uvs;
                        mesh.normals = normals;
                        mesh.faces = new ArrayList<>();
                        meshes.add(mesh);
                        break;
                    case "v":
                        vertices.add(new Vector3f(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])));
                        break;
                    case "vt":
                        uvs.add(new Vector2f(
                                Float.parseFloat(parts[1]),
                                -Float.parseFloat(parts[2])));
                        break;
                    case "vn":
                        normals.add(new Vector3f(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])));
                        break;
                    case "usemtl":
                        mesh.material = materials.get(parts[1]);
                        break;
                    case "f":
                        Face face = new Face();
                        face.vertexIndices = new ArrayList<>();
                        face.uvIndices = new ArrayList<>();
                        face.normalsIndices = new ArrayList<>();
                        for (int i = 1; i < parts.length; i++) {
                            String[] faceParts = parts[i].split("/");
                            face.vertexIndices.add(Integer.parseInt(faceParts[0]) - 1);
                            face.uvIndices.add(Integer.parseInt(faceParts[1]) - 1);
                            face.normalsIndices.add(Integer.parseInt(faceParts[2]) - 1);
                        }
                        mesh.faces.add(face);
                        break;
                }
                line = bufferedReader.readLine();
            }
            return meshes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Material> loadMaterial(String path) {
        String parentPath = new File(path).getParent();
        Material material = null;
        Map<String, Material> materials = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "newmtl":
                        String materialName = parts[1];
                        material = new Material();
                        materials.put(materialName, material);
                        break;
                    case "Ka":
                        material.ambient = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                        break;
                    case "Kd":
                        material.diffuse = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                        break;
                    case "Ks":
                        material.specular = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                        break;
                    case "Ns":
                        material.shininess = Float.parseFloat(parts[1]);
                        break;
                    case "map_Kd":
                        material.diffuseTexture = Texture.loadFromFile(Paths.get(parentPath, parts[1]).toAbsolutePath().toString());
                        break;
                    case "map_Ks":
                        material.specularTexture = Texture.loadFromFile(Paths.get(parentPath, parts[1]).toAbsolutePath().toString());
                        break;
                }
                line = bufferedReader.readLine();
            }
            return materials;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
