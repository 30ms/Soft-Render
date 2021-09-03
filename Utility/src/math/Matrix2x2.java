package math;

/**
 * 2 x 2 矩阵
 *
 * @author Liuzhenbin
 * @date 2021-04-22 13:56
 **/
public class Matrix2x2 extends Matrix{
    public static final Matrix2x2 IDENTITY = new Matrix2x2(
            1, 0,
            0, 1);

    public Matrix2x2(float m11,float m12,float m21,float m22) {
        super(new float[]{m11, m12, m21, m22}, 2, 2);
    }
}
