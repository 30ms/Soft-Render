package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/14 9:29
 **/
public class Matrix4x4f {

    public float[] values;

    public Matrix4x4f() {
        this.values = new float[16];
    }

    public Matrix4x4f(float[] values) {
        this.values = values;
    }

    public Matrix4x4f(float a11, float a12, float a13, float a14,
                      float a21, float a22, float a23, float a24,
                      float a31, float a32, float a33, float a34,
                      float a41, float a42, float a43, float a44) {
        values = new float[]{
                a11, a12, a13, a14,
                a21, a22, a23, a24,
                a31, a32, a33, a34,
                a41, a42, a43, a44
        };
    }

    public static final Matrix4x4f IDENTITY = new Matrix4x4f(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1);

    public static Matrix4x4f identify() {
        return new Matrix4x4f(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    public static final Matrix4x4f EMPTY = new Matrix4x4f(
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0);

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

    //右手坐标系的透视投影矩阵, near, far为距离值, 计算后的W分量为-Z
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
        Vector3f z = position.subtract(target);
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
        return new Vector4f(
                (values[0] * right.X) + (values[1] * right.Y) + (values[2] * right.Z) + (values[3] * right.W),
                (values[4] * right.X) + (values[5] * right.Y) + (values[6] * right.Z) + (values[7] * right.W),
                (values[8] * right.X) + (values[9] * right.Y) + (values[10] * right.Z) + (values[11] * right.W),
                (values[12] * right.X) + (values[13] * right.Y) + (values[14] * right.Z) + (values[15] * right.W)
        );
    }

    public Matrix4x4f multiply(Matrix4x4f right) {
        Matrix4x4f res = new Matrix4x4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += values[i * 4 + k] * right.values[k * 4 + j];
                }
                res.values[i * 4 + j] = sum;
            }
        }
        return res;
    }

    public Matrix4x4f transpose() {
        Matrix4x4f res = new Matrix4x4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res.values[i * 4 + j] = values[j * 4 + i];
            }
        }
        return res;
    }

    public Matrix4x4f inverse() {
      Matrix4x4f res = new Matrix4x4f();
      float det = determinant();
      if (det == 0) {
          throw new IllegalStateException("Matrix is singular and cannot be inverted.");
      }
      det = 1.0f / det;
      for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 4; j++) {
              res.values[i * 4 + j] = cofactor(j, i) * det;
          }
      }
      return res;
    }

    private float determinant() {
        return values[0] * cofactor(0, 0) + values[1] * cofactor(0, 1) + values[2] * cofactor(0, 2) + values[3] * cofactor(0, 3);
    }

    private float cofactor(int row, int col) {
        return (float) (Math.pow(-1, row + col) * determinant3x3(getMinor(row, col)));
    }

    private float[] getMinor(int row, int col) {
        float[] res = new float[9];
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (i == row) continue;
            for (int j = 0; j < 4; j++) {
                if (j == col) continue;
                res[count++] = values[i * 4 + j];
            }
        }
        return res;
    }

    private float determinant3x3(float[] tmp) {
        return tmp[0] * (tmp[4] * tmp[8] - tmp[7] * tmp[5]) -
               tmp[1] * (tmp[3] * tmp[8] - tmp[5] * tmp[6]) +
               tmp[2] * (tmp[3] * tmp[7] - tmp[6] * tmp[4]);
    }
}

