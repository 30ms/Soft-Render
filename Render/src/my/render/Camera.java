package my.render;

import math.Matrix4x4;

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
        this.projectionMat = Matrix4x4f.projection(aspect, fov, near, far);
    }

    //更新矩阵
    void update(long deltaTime) {
    }

    public void rotation(Vector3f angle) {
    }

    void reset() {

    }
}
