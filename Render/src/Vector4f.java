/**
 * TODO
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
    public static Vector4f fromEulerAngles(float roll, float pitch, float yaw) // roll (x), pitch (y), yaw (z)
    {
        float cr = (float) Math.cos(roll * 0.5);
        float sr = (float) Math.sin(roll * 0.5);
        float cp = (float) Math.cos(pitch * 0.5);
        float sp = (float) Math.sin(pitch * 0.5);
        float cy = (float) Math.cos(yaw * 0.5);
        float sy = (float) Math.sin(yaw * 0.5);

        return new Vector4f(sr * cp * cy - cr * sp * sy,
                cr * sp * cy + sr * cp * sy,
                cr * cp * sy - sr * sp * cy,
                cr * cp * cy + sr * sp * sy);
    }
}

