package math;

/**
 * 4维向量
 *
 * @author Liuzhenbin
 * @date 2021-04-22 14:16
 **/
public class Vector4 extends Vector {
    public static final Vector4 BASIS_X = new Vector4(1, 0, 0, 0);
    public static final Vector4 BASIS_Y = new Vector4(0, 1, 0, 0);
    public static final Vector4 BASIS_Z = new Vector4(0, 0, 1, 0);
    public static final Vector4 BASIS_W = new Vector4(0, 0, 0, 1);

    public Vector4(float x, float y, float z, float w) {
        super(new float[]{x, y, z, w});
    }

    public float x() {
        return item(0);
    }

    public float y() {
        return item(1);
    }

    public float z() {
        return item(2);
    }

    public float w() {
        return item(3);
    }
}
