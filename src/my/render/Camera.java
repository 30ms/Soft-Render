package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:54
 **/
public class Camera {
    public Vector3f position;
    public Vector3f target;
    public Vector3f up;

    //my.render.Frustum 视锥体相关属性
    public float fov;
    public float aspect;
    public float near;
    public float far;

    public Camera(Vector3f position, Vector3f target, Vector3f up, float fov, float aspect, float near, float far) {
        this.position = position;
        this.target = target;
        this.up = up;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
    }

    public Matrix4x4f getViewMat() {
        return Matrix4x4f.lookAt(position, target, up);
    }

    public Matrix4x4f getProjectionMat() {
        return Matrix4x4f.perspectiveProjection(aspect, fov, near, far);
    }

    void update(long deltaTime) {
    }

    public void forward(float length) {
        Vector3f front = target.subtract(position);
        front.normalized();
        position = position.add(front.multiply(length));
        target = target.add(front.multiply(length));
    }

    public void backward(float length) {
        Vector3f front = target.subtract(position);
        front.normalized();
        position = position.subtract(front.multiply(length));
        target = target.subtract(front.multiply(length));
    }

    public void right(float length) {
        Vector3f front = target.subtract(position);
        Vector3f right = front.cross(up);
        right.normalized();
        position = position.add(right.multiply(length));
        target = target.add(right.multiply(length));
    }

    public void left(float length) {
        Vector3f front = target.subtract(position);
        Vector3f right = front.cross(up);
        right.normalized();
        position = position.subtract(right.multiply(length));
        target = target.subtract(right.multiply(length));
    }

    public void up(float length) {
        position = position.add(up.multiply(length));
        target  = target.add(up.multiply(length));
    }

    public void down(float length) {
        position = position.subtract(up.multiply(length));
        target  = target.subtract(up.multiply(length));
    }

    public void rotation(float x, float y, float z) {
        Matrix4x4f t1 = Matrix4x4f.translation(position.multiply(-1));
        Matrix4x4f ro = Matrix4x4f.rotationX(x).multiply(Matrix4x4f.rotationY(y)).multiply(Matrix4x4f.rotationZ(z));
        Matrix4x4f t2 = Matrix4x4f.translation(position);
        Vector4f res = t2.multiply(ro).multiply(t1).multiply(new Vector4f(target));
        target.X = res.X;
        target.Y = res.Y;
        target.Z = res.Z;
    }

    void reset() {

    }
}
