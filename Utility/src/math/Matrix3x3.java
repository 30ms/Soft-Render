package math;

/**
 * 3 x 3 矩阵
 *
 * @author Liuzhenbin
 * @date 2021-04-22 13:58
 **/
public class Matrix3x3 extends Matrix{
    public static final Matrix3x3 IDENTITY = new Matrix3x3(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1);
    public Matrix3x3(float m11, float m12, float m13,
                     float m21, float m22, float m23,
                     float m31, float m32, float m33) {
        super(new float[]{m11, m12, m13, m21, m22, m23, m31, m32, m33}, 3, 3);
    }
}
