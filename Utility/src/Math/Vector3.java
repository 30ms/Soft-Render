package Math;

/**
 * 三维向量
 *
 * @author Liuzhenbin
 * @date 2021-04-21 14:18
 **/
public class Vector3 {

    public static Vector3 ONE = new Vector3(1, 1, 1);
    public static Vector3 ZERO = new Vector3(0, 0, 0);
    public static Vector3 baseX = new Vector3(1, 0, 0);
    public static Vector3 baseY = new Vector3(0, 1, 0);
    public static Vector3 baseZ = new Vector3(0, 0, 1);

    private float x, y, z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }

    public static Vector3 add(Vector3 left, Vector3 right) {
        return new Vector3(left.x + right.x, left.y + right.y, left.z + right.z);
    }

    public static Vector3 subtract(Vector3 left, Vector3 right) {
        return new Vector3(left.x - right.x, left.y - right.y, left.z - right.z);
    }

    public static Vector3 scale(Vector3 vector, float scale) {
        return new Vector3(vector.x * scale, vector.y * scale, vector.z * scale);
    }

    /**
     * 点积，数量积
     * 若 a (x1,y1,z1),b(x2,y2,z2) 则 a · b = x1 * x2 + y1 * y2 + z1 * z2
     * @date 2021-04-21 14:27
     * @param left 左向量
     * @param right 右向量
     * @return float
     **/
    public static float dotProduct(Vector3 left, Vector3 right) {
        return left.x * right.x + left.y * right.y + right.z * right.z;
    }

    /**
     * 叉积, 矢量积
     *
     * @param left  左向量
     * @param right 右向量
     * @date 2021-04-21 13:35
     **/
    public static Vector3 crossProduct(Vector3 left, Vector3 right) {
        return Vector3.add(
                Vector3.subtract(
                        Vector3.scale(baseX, (left.y * right.z - left.z * right.y)),
                        Vector3.scale(baseY, (left.x * right.z - left.z * right.x))
                ),
                Vector3.scale(baseZ, (left.x * right.y - left.y * right.x))
        );
    }
}
