package my.render;

import java.util.Arrays;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:22
 **/
public class SoftwareRender {
    private Camera camera;
    private AbstractShader shader = new FlatShader();
    private Buffer<Vector3i> pixelBuffer;
    private Buffer<Float> zBuffer;
    private final Rasterizer rasterizer = Rasterizer.INSTINSE;
    public static final SoftwareRender INSTINCE = new SoftwareRender();

    private SoftwareRender() {
    }

    public void buildBuffer(int width, int height) {
        this.pixelBuffer = new Buffer<>(width, height, new Vector3i(0, 0, 0));
        this.zBuffer = new Buffer<>(width, height, -Float.MAX_VALUE);
    }

    public void clearBuffers() {
        zBuffer.clear();
        pixelBuffer.clear();
    }

    public Buffer<Vector3i> getRenderTarget() {
        return pixelBuffer;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setShader(AbstractShader shader) {
        this.shader = shader;
    }

    public void drawTriangularMesh(Model model) {
        Mesh mesh = model.getMesh();
        Vector3f[] vertices = mesh.getVertices();
        Vector2f[] textures = mesh.getUVs();
        Vector3f[] normals = mesh.getNormals();
        Face[] faces = mesh.getFaces();

        for (Face face : faces) {
            Vector4f[] vertices_f = new Vector4f[face.vertexIndices.length];
            Vector2f[] uvs_f = new Vector2f[face.uvIndices.length];
            Vector3f[] normals_f = new Vector3f[face.normalsIndices.length];
            for (int i = 0; i < face.vertexIndices.length; i++) {
                vertices_f[i] = new Vector4f(vertices[face.vertexIndices[i]]);
            }
            for (int i = 0; i < face.uvIndices.length; i++) {
                uvs_f[i] = textures[face.uvIndices[i]];
            }
            for (int i = 0; i < face.normalsIndices.length; i++) {
                normals_f[i] = normals[face.normalsIndices[i]];
            }

            //TODO 背面裁剪

            //顶点着色
            shader.m = Matrix4x4f.translation(model.position)
                    .multiply(Matrix4x4f.scale(model.scale))
                    .multiply(Matrix4x4f.rotationX(model.rotation.X))
                    .multiply(Matrix4x4f.rotationY(model.rotation.Y))
                    .multiply(Matrix4x4f.rotationZ(model.rotation.Z));
            shader.v = camera.viewMat;
            //顶点着色
            for (int i = 0; i < vertices_f.length; i++) {
                vertices_f[i] = shader.vertex(i, vertices_f[i], null, uvs_f[i]);
            }
            //光栅化
            rasterizer.drawTriangles(vertices_f, camera.projectionMat, shader, zBuffer, pixelBuffer);
        }
    }

    private boolean backFaceCulling(Vector4f normals, Vector4f vertex, Matrix4x4f worldToModel) {
        //摄像机的世界空间位置变换到模型空间， 减去顶点位置得到观察方向向量
        Vector4f viewDir = worldToModel.multiply(new Vector4f(camera.position)).reduce(vertex);
        viewDir.normalized();
        //向量点乘判断夹角是否大于180度
        return viewDir.dotProduct(normals) <= 0.0;
    }

}
