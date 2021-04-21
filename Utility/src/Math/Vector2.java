package Math;

import java.util.Objects;

/**
 * 二维向量
 *
 * @author Liuzhenbin
 * @date 2021-04-21 11:42
 **/
public class Vector2 {

    public static final Vector2 ZERO = new Vector2(0, 0);
    public static final Vector2 ONE = new Vector2(1, 1);

    private float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2)) return false;
        Vector2 vector2 = (Vector2) o;
        return Float.compare(vector2.getX(), getX()) == 0 && Float.compare(vector2.getY(), getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + ']';
    }

    public static Vector2 add(Vector2 left, Vector2 right) {
        return new Vector2(left.x + right.x, left.y + right.y);
    }

    public static Vector2 subtract(Vector2 left, Vector2 right) {
        return new Vector2(left.x - right.x, left.y - right.y);
    }

    /**
     * 点积，数量积
     * 若 a (x1,y1),b(x2,y2) 则 a · b = x1 * x2 + y1 * y2
     * @date 2021-04-21 14:27
     * @param left 左向量
     * @param right 右向量
     * @return float
     **/
    public static float dotProduct(Vector2 left, Vector2 right) {
        return left.x * right.x + left.y * right.y;
    }

    public static Vector2 scale(Vector2 vector, float scale) {
        return new Vector2(vector.x * scale, vector.y * scale);
    }

    public static Vector2 divide(Vector2 vector, float scale) {
        return new Vector2(vector.x / scale, vector.y / scale);
    }

}
