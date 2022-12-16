package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/13 9:44
 **/
public class Vector3f {
    float X,Y,Z;

    public Vector3f(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public Vector3f cross(Vector3f right) {
        return new Vector3f(X * right.Z - Z * right.Y, Z * right.X - X * right.Z, X * right.Y - Y * right.X);
    }

    public Vector3f reduce(Vector3f right) {
        return new Vector3f(X - right.X, Y - right.Y, Z - right.Z);
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

    public float dotProduct(Vector3f right) {
        return X * right.X + Y * right.Y + Z * right.Z;
    }
}
