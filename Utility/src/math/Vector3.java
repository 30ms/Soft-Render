package math;

/**
 * 3维向量
 *
 * @author Liuzhenbin
 * @date 2021-04-22 14:04
 **/
public class Vector3 extends Vector{
    public static final Vector BASIS_X = new Vector3(1, 0, 0);
    public static final Vector BASIS_Y = new Vector3(0, 1, 0);
    public static final Vector BASIS_Z = new Vector3(0, 0, 1);

    public Vector3(float x, float y, float z) {
        super(new float[]{x, y, z});
    }

    public float x() {
        return this.item(0);
    }

    public float y() {
        return this.item(1);
    }

    public float z() {
        return this.item(2);
    }

    /**
     * 叉积, 矢量积
     *
     * @param left  左向量
     * @param right 右向量
     * @date 2021-04-21 13:35
     **/
    public static Vector3 crossProduct(Vector3 left, Vector3 right) {
        return (Vector3) Vector3.add(
                Vector3.subtract(
                        Vector3.scale(BASIS_X, (left.y() * right.z() - left.z() * right.y())),
                        Vector3.scale(BASIS_Y, (left.x() * right.z() - left.z() * right.x()))
                ),
                Vector3.scale(BASIS_Z, (left.x() * right.y() - left.y() * right.x()))
        );

    }
}
