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
    float nearH;
    float nearW;
    float speed;
    Matrix4x4f viewMat;
    Matrix4x4f projectionMat;

    public Camera(Vector3f position, Vector3f target, Vector3f up, float fov, float aspect, float near, float far, float nearH, float nearW, float speed) {
        this.position = position;
        this.target = target;
        this.up = up;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        this.nearH = nearH;
        this.nearW = nearW;
        this.speed = speed;
    }

    //更新矩阵
    void update(long deltaTime) {
        viewMat = Matrix4x4f.lookAt(position, target, up);
        projectionMat = Matrix4x4f.projection(aspect, fov, near, far);
    }

    void reset() {

    }
}
