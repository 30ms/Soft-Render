package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/14 9:29
 **/
public class Matrix4x4f {
    public float A11, A12, A13, A14,
            A21, A22, A23, A24,
            A31, A32, A33, A34,
            A41, A42, A43, A44;

    public static final Matrix4x4f IDENTITY = new Matrix4x4f(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1);

    public static final Matrix4x4f EMPTY = new Matrix4x4f(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0);

    public Matrix4x4f(float a11, float a12, float a13, float a14,
                      float a21, float a22, float a23, float a24,
                      float a31, float a32, float a33, float a34,
                      float a41, float a42, float a43, float a44) {
        A11 = a11;
        A12 = a12;
        A13 = a13;
        A14 = a14;

        A21 = a21;
        A22 = a22;
        A23 = a23;
        A24 = a24;

        A31 = a31;
        A32 = a32;
        A33 = a33;
        A34 = a34;

        A41 = a41;
        A42 = a42;
        A43 = a43;
        A44 = a44;
    }

    public static Matrix4x4f scale(Vector3f input) {
        return new Matrix4x4f(
                input.X, 0, 0, 0,
                0, input.Y, 0, 0,
                0, 0, input.Z, 0,
                0, 0, 0, 1);
    }

    public static Matrix4x4f rotationX(float angle) {
        double radian = Math.toRadians(angle);
        float cos = (float) Math.cos(radian), sin = (float) Math.sin(radian);
        return new Matrix4x4f(
                1, 0, 0, 0,
                0, cos, -sin, 0,
                0, sin, cos, 0,
                0, 0, 0, 1
        );
    }

    public static Matrix4x4f rotationY(float angle) {
        double radian = Math.toRadians(angle);
        float cos = (float) Math.cos(radian), sin = (float) Math.sin(radian);
        return new Matrix4x4f(
                cos, 0, sin, 0,
                0, 1, 0, 0,
                -sin, 0, cos, 0,
                0, 0, 0, 1);
    }

    public static Matrix4x4f rotationZ(float angle) {
        double radian = Math.toRadians(angle);
        float cos = (float) Math.cos(radian), sin = (float) Math.sin(radian);
        return new Matrix4x4f(
                cos, -sin, 0, 0,
                sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    //右手坐标系的透视投影矩阵
    public static Matrix4x4f perspectiveProjection(float aspect, float fov, float zNear, float zFar) {
        float left, right, bottom, top;
        top = (float) (zNear * Math.tan(Math.toRadians(fov) / 2));
        bottom = -top;
        right = aspect * top;
        left = -right;
        return perspectiveProjection(left, right, bottom, top, zNear, zFar);
    }

    //右手坐标系的透视投影矩阵,XYZ值映射到了[-1,1], near, far为距离值
    public static Matrix4x4f perspectiveProjection(float left, float right, float bottom, float top, float near, float far) {
        return new Matrix4x4f(
                2 * near / (right - left), 0,                         (right + left) / (right - left), 0,
                0,                         2 * near / (top - bottom), (top + bottom) / (top - bottom), 0,
                0,                         0,                         -(near + far) / (far - near),    -(2 * near * far) / (far - near),
                0,                         0,                         -1,                              0
        );
    }

    public static Matrix4x4f othProjection(float left, float right, float bottom, float top, float near, float far) {
        return new Matrix4x4f(
                2 / (right - left), 0, 0, -(right + left) / (right - left),
                0, 2 / (top - bottom), 0, -(top + bottom) / (top - bottom),
                0, 0, 2 / (far - near), -(far + near) / (far - near),
                0, 0, 0, 1
        );
    }

    public static Matrix4x4f lookAt(Vector3f position, Vector3f target, Vector3f up) {
        //z轴为观察方向的反向
        Vector3f z = position.reduce(target);
        z.normalized();
        Vector3f x = up.cross(z);
        x.normalized();
        Vector3f y = z.cross(x);
        return new Matrix4x4f(
                x.X, x.Y, x.Z, -x.dotProduct(position),
                y.X, y.Y, y.Z, -y.dotProduct(position),
                z.X, z.Y, z.Z, -z.dotProduct(position),
                0, 0, 0, 1);
    }

    public static Matrix4x4f translation(Vector3f input) {
        return new Matrix4x4f(
                1, 0, 0, input.X,
                0, 1, 0, input.Y,
                0, 0, 1, input.Z,
                0, 0, 0, 1);
    }

    public Vector4f multiply(Vector4f right) {
        return multiply(right, false);
    }

    public Vector4f multiply(Vector4f right, boolean normalize) {
        float x = (A11 * right.X) + (A12 * right.Y) + (A13 * right.Z) + (A14 * right.W);
        float y = (A21 * right.X) + (A22 * right.Y) + (A23 * right.Z) + (A24 * right.W);
        float z = (A31 * right.X) + (A32 * right.Y) + (A33 * right.Z) + (A34 * right.W);
        float w = (A41 * right.X) + (A42 * right.Y) + (A43 * right.Z) + (A44 * right.W);

        if (normalize) {
            x /= w;
            y /= w;
            z /= w;
            w = 1;
        }

        return new Vector4f(x, y, z, w);
    }

    public Matrix4x4f multiply(Matrix4x4f right) {
        // A1x*Bx1 -> A11*B11 + A12*B21 ...
        float r11 = (A11 * right.A11) + (A12 * right.A21) + (A13 * right.A31) + (A14 * right.A41);
        // A1x*Bx2 -> A11*B12 + A12*B22 ...
        float r12 = (A11 * right.A12) + (A12 * right.A22) + (A13 * right.A32) + (A14 * right.A42);
        // A1x*Bx3 -> A11*B13 + A12*B23 ...
        float r13 = (A11 * right.A13) + (A12 * right.A23) + (A13 * right.A33) + (A14 * right.A43);
        // A1x*Bx4 -> A11*B14 + A12*B24 ...
        float r14 = (A11 * right.A14) + (A12 * right.A24) + (A13 * right.A34) + (A14 * right.A44);

        // A2x*Bx1 -> A21*B12 + A22*B22 ...
        float r21 = (A21 * right.A11) + (A22 * right.A21) + (A23 * right.A31) + (A24 * right.A41);
        float r22 = (A21 * right.A12) + (A22 * right.A22) + (A23 * right.A32) + (A24 * right.A42);
        float r23 = (A21 * right.A13) + (A22 * right.A23) + (A23 * right.A33) + (A24 * right.A43);
        float r24 = (A21 * right.A14) + (A22 * right.A24) + (A23 * right.A34) + (A24 * right.A44);

        // A3x*Bx1 -> A31*B12 + A32*B22 ...
        float r31 = (A31 * right.A11) + (A32 * right.A21) + (A33 * right.A31) + (A34 * right.A41);
        float r32 = (A31 * right.A12) + (A32 * right.A22) + (A33 * right.A32) + (A34 * right.A42);
        float r33 = (A31 * right.A13) + (A32 * right.A23) + (A33 * right.A33) + (A34 * right.A43);
        float r34 = (A31 * right.A14) + (A32 * right.A24) + (A33 * right.A34) + (A34 * right.A44);

        // A4x*Bx1 -> A41*B12 + A42*B22 ...
        float r41 = (A41 * right.A11) + (A42 * right.A21) + (A43 * right.A31) + (A44 * right.A41);
        float r42 = (A41 * right.A12) + (A42 * right.A22) + (A43 * right.A32) + (A44 * right.A42);
        float r43 = (A41 * right.A13) + (A42 * right.A23) + (A43 * right.A33) + (A44 * right.A43);
        float r44 = (A41 * right.A14) + (A42 * right.A24) + (A43 * right.A34) + (A44 * right.A44);

        return new Matrix4x4f(r11, r12, r13, r14, r21, r22, r23, r24, r31, r32, r33, r34, r41, r42, r43, r44);
    }
}

