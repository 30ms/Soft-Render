package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/14 9:42
 **/
public class Vector3i {
    int X, Y, Z;

    public Vector3i(int x, int y, int z)
    {
        X = x;
        Y = y;
        Z = z;
    }

    public Vector3i multiply(float scalar)
    {
        return new Vector3i((int) (X * scalar), (int) (Y * scalar), (int) (Z * scalar));
    }
}
