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
    Vector3f position;
    /*
     缩放
     */
    Vector3f scale;
    /*
     旋转
     */
    Vector3f rotation;
    Mesh mesh;

    public Model(Vector3f position, Vector3f scale, Vector3f rotation, Mesh mesh) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        this.mesh = mesh;
    }

    public void update(long deltaTime) {
    }

    public Mesh getMesh() {
        return mesh;
    }
}
