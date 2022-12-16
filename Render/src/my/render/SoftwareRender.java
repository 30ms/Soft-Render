package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:22
 **/
public class SoftwareRender {
    private Camera camera;
    private AbstractShader shader;
    private Buffer<Vector3i> pixelBuffer;
    private Buffer<Float> zBuffer;
    private final Rasterizer rasterizer = Rasterizer.INSTINSE;
    public static final SoftwareRender INSTINCE = new SoftwareRender();

    private SoftwareRender() {
    }

    public void buildBuffer(int width, int height) {
        this.pixelBuffer = new Buffer<>(width, height, new Vector3i(0, 0, 0));
        this.zBuffer = new Buffer<>(width, height, 1.0f);
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
        Vector3f[] textures = mesh.getTextures();
        Vector3f[] normals = mesh.getNormals();
        Face[] faces = mesh.getFaces();

        for (Face face : faces) {
            Vector4f[] vertices_f = new Vector4f[face.vertexIndices.length];
            Vector3f[] textures_f = new Vector3f[face.textureIndices.length];
            Vector3f[] normals_f = new Vector3f[face.normalsIndices.length];
            for (int i = 0; i < face.vertexIndices.length; i++) {
                vertices_f[i] = new Vector4f(vertices[face.vertexIndices[i]]);
            }
            for (int i = 0; i < face.textureIndices.length; i++) {
                textures_f[i] = textures[face.textureIndices[i]];
            }
            for (int i = 0; i < face.normalsIndices.length; i++) {
                normals_f[i] = normals[face.normalsIndices[i]];
            }

            //TODO 背面裁剪

            //顶点着色
            shader.m = model.getModelMat();
            shader.v = camera.viewMat;
            shader.p = camera.projectionMat;
            shader.cameraPosition = new Vector4f(camera.position);
            for (int i = 0; i < vertices_f.length; i++) {
                vertices_f[i] = shader.vertex(i, vertices_f[i], normals_f[i], textures_f[i]);
            }

            //TODO 视锥体裁剪, 此面不用渲染
            if(clipping(vertices_f)) continue;

            //投影
            for (Vector4f vertex : vertices_f) {
                vertex.X /= vertex.W;
                vertex.Y /= vertex.W;
                vertex.Z /= vertex.W;
            }

            //光栅化
            rasterizer.drawTriangles(vertices, shader, zBuffer, pixelBuffer);
        }
    }

    private boolean backFaceCulling(Vector4f normals, Vector4f vertex, Matrix4x4f worldToModel) {
        //摄像机的世界空间位置变换到模型空间， 减去顶点位置得到观察方向向量
        Vector4f viewDir = worldToModel.multiply(new Vector4f(camera.position)).reduce(vertex);
        viewDir.normalized();
        //向量点乘判断夹角是否大于180度
        return viewDir.dotProduct(normals) <= 0.0;
    }

    private boolean clipping(Vector4f[] clipSpaceVertices) {
        int count = 0;
        for (Vector4f vertex : clipSpaceVertices) {
            //判断点是否在外面
            if((vertex.X < -vertex.W || vertex.X > vertex.W)  && (vertex.Y < -vertex.W || vertex.Y > vertex.W) && (vertex.Z < -vertex.W || vertex.Z > vertex.W))
                count++;
        }
        //是否全部点都在外面
        return count == clipSpaceVertices.length;
    }

}