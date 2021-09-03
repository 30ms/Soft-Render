package math;

import java.util.Arrays;

/**
 * 向量
 *
 * @author Liuzhenbin
 * @date 2021-04-22 10:22
 **/
public class Vector {

    private float[] value;

    public Vector(float[] value) {
        this.value = value;
    }

    public int length() {
        return value.length;
    }

    public float item(int i) {
        return this.value[i - 1];
    }

    /**
     * 向量的模
     *
     * @return float
     * @date 2021-04-23 17:02
     **/
    public float magnitude() {
        float temp=0;
        for (int i = 0; i < length(); i++) {
            temp = temp + (float) Math.pow(item(i + 1), 2);
        }
        return (float) Math.pow(temp, 0.5f);
    }

    /**
     * 向量归一化
     *
     * @return math.Vector
     * @date 2021-04-23 17:07
     **/
    public Vector normalized() {
        float[] result = new float[length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = 1 / magnitude();
        }
        return new Vector(result);
    }

    @Override
    public String toString() {
        return Arrays.toString(value);
    }

    public static Vector add(Vector l, Vector r) {
        int l_l = l.length();
        int r_l = r.length();
        if (l_l != r_l) throw new IllegalArgumentException("length not equal");
        float[] items = new float[l_l];
        for (int i = 1; i <= items.length; i++) {
            items[i - 1] = l.item(i) + r.item(i);
        }
        return new Vector(items);
    }

    public static Vector subtract(Vector l, Vector r) {
        int l_l = l.length();
        int r_l = r.length();
        if (l_l != r_l) throw new IllegalArgumentException("length not equal");
        float[] items = new float[l_l];
        for (int i = 1; i <= items.length; i++) {
            items[i - 1] = l.item(i) - r.item(i);
        }
        return new Vector(items);
    }

    public static Vector negative(Vector vector) {
        float[] result = new float[vector.length()];
        for (int i = 0; i < vector.length(); i++) {
            result[i] = -vector.item(i);
        }
        return new Vector(result);
    }

    public static Vector scale(Vector vector, float scale) {
        float[] result = new float[vector.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = vector.item(i + 1) * scale;
        }
        return new Vector(result);
    }

    /**
     * 点积, 数量积, 标量积
     * @date 2021-04-22 10:35
     * @param l 左向量
     * @param r 右向量
     * @return float
     **/
    public static float dotProduct(Vector l, Vector r) {
        int l_l = l.length();
        int r_l = r.length();
        if (l_l != r_l) throw new IllegalArgumentException("vector length are not equal");
        float result = 0;
        for (int i = 0; i < l_l; i++) {
            result = result + (l.item(i + 1) * r.item(i + 1));
        }
        return result;
    }
}
