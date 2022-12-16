package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:34
 **/
public class Model {
    /*
     位置
     */
    private Vector3f position;
    /*
     缩放
     */
    private Vector3f scale;
    /*
     旋转
     */
    private Vector3f rotation;
    private Mesh mesh;
    private Matrix4x4f modelMat;

    public Model(Vector3f position, Vector3f scale, Vector3f rotation, Mesh mesh) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        this.mesh = mesh;
    }

    public void update(long deltaTime) {
        modelMat = Matrix4x4f.translation(position)
                .multiply(Matrix4x4f.scale(scale))
                .multiply(Matrix4x4f.rotationX(rotation.X))
                .multiply(Matrix4x4f.rotationY(rotation.Y))
                .multiply(Matrix4x4f.rotationZ(rotation.Z));
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Matrix4x4f getModelMat() {
        return modelMat;
    }
}
