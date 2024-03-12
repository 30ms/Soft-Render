package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:54
 **/
public class Camera {
    Vector3f position;
    Vector3f target;
    Vector3f up;

    //my.render.Frustum 视锥体相关属性
    float fov;
    float aspect;
    float near;
    float far;
    Matrix4x4f viewMat;
    Matrix4x4f projectionMat;

    public Camera(Vector3f position, Vector3f target, Vector3f up, float fov, float aspect, float near, float far) {
        this.position = position;
        this.target = target;
        this.up = up;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        updateMat();
    }

    private void updateMat() {
        this.viewMat = Matrix4x4f.lookAt(position, target, up);
        this.projectionMat = Matrix4x4f.perspectiveProjection(aspect, fov, near, far);
    }

    //更新矩阵
    void update(long deltaTime) {
        updateMat();
    }

    public void forward(float length) {
        Vector3f front = target.reduce(position);
        front.normalized();
        position = position.add(front.scale(length));
        target = target.add(front.scale(length));
    }

    public void backward(float length) {
        Vector3f front = target.reduce(position);
        front.normalized();
        position = position.reduce(front.scale(length));
        target = target.reduce(front.scale(length));
    }

    public void right(float length) {
        Vector3f front = target.reduce(position);
        Vector3f right = front.cross(up);
        right.normalized();
        position = position.add(right.scale(length));
        target = target.add(right.scale(length));
    }

    public void left(float length) {
        Vector3f front = target.reduce(position);
        Vector3f right = front.cross(up);
        right.normalized();
        position = position.reduce(right.scale(length));
        target = target.reduce(right.scale(length));
    }

    public void up(float length) {
        position = position.add(up.scale(length));
        target  = target.add(up.scale(length));
    }

    public void down(float length) {
        position = position.reduce(up.scale(length));
        target  = target.reduce(up.scale(length));
    }

    public void rotation(float x, float y, float z) {
        Matrix4x4f t1 = Matrix4x4f.translation(position.scale(-1));
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
