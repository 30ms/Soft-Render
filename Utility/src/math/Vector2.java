package math;

/**
 * 2维向量
 *
 * @author Liuzhenbin
 * @date 2021-04-22 14:03
 **/
public class Vector2 extends Vector{
    public static final Vector2 BASIS_X = new Vector2(1, 0);
    public static final Vector2 BASIS_Y = new Vector2(0, 1);

    public Vector2(float x,float y) {
        super(new float[]{x, y});
    }

    public float x() {
        return item(0);
    }

    public float y() {
        return item(1);
    }
}
