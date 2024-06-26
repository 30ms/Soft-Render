package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/13 9:44
 **/
public class Vector3f {
    public float X,Y,Z;

    public Vector3f(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public Vector3f cross(Vector3f right) {
        return new Vector3f(Y * right.Z - Z * right.Y, Z * right.X - X * right.Z, X * right.Y - Y * right.X);
    }

    public Vector3f subtract(Vector3f right) {
        return new Vector3f(X - right.X, Y - right.Y, Z - right.Z);
    }

    public Vector3f add(Vector3f right) {
        return new Vector3f(X + right.X, Y + right.Y, Z + right.Z);
    }

    public Vector3f multiply(float s) {
        return new Vector3f(X * s, Y * s, Z * s);
    }

    public float length() {
        return (float) Math.sqrt(X * X + Y * Y + Z * Z );
    }

    public void normalized() {
        float len = length();
        X /= len;
        Y /= len;
        Z /= len;
    }

    public Vector3f normalize() {
        float len = length();
        return new Vector3f(X / len, Y / len, Z / len);
    }

    public float dotProduct(Vector3f right) {
        return X * right.X + Y * right.Y + Z * right.Z;
    }

    public Vector3f multiply(Vector3f right) {
        return new Vector3f(X * right.X, Y * right.Y, Z * right.Z);
    }

    ///反射向量
    public Vector3f reflect(Vector3f normal) {
        return this.subtract(normal.multiply(2 * this.dotProduct(normal)));
    }
}
