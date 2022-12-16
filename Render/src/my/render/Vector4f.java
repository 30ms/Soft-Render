package my.render;

/**
 * W为0表示向量， W为1时表示点
 *
 * @author Liuzhenbin
 * @date 2022/12/14 9:33
 **/
public class Vector4f {
    public float X, Y, Z, W;

    public Vector4f(float x, float y, float z, float w) {
        X = x;
        Y = y;
        Z = z;
        W = w;
    }

    public Vector4f(float x, float y, float z) {
        this(x, y, z, 1);
    }

    public Vector4f(Vector3f vector3f) {
        this(vector3f.X, vector3f.Y, vector3f.Z);
    }

    public Vector4f reduce(Vector4f right) {
        return new Vector4f(X - right.X, Y - right.Y, Z - right.Z, W - right.W);
    }

    public float length() {
        return (float) Math.sqrt(X * X + Y * Y + Z * Z + W * W);
    }

    public void normalized() {
        float len = length();
        X /= len;
        Y /= len;
        Z /= len;
        W /= len;
    }

    public float dotProduct(Vector4f right) {
        return X * right.X + Y * right.Y + Z * right.Z + W * right.W;
    }
}

