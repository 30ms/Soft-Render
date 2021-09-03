package math.geometry;

import math.*;
import java.lang.Math;

import static java.lang.Math.*;

/**
 * 几何变换，二维和三维向量空间坐标的几何变换矩阵。
 * 因为平移变换不是线性变换，所以要增加一个维度进行非线性变换，
 * 如 二维空间向量列坐标 [ x , y , 1 ]T，其几何变换矩阵为3 X 3矩阵，
 * 三维空间向量列坐标 [ x , y , z ,1 ]T，其几何变换矩阵为4 x 4矩阵。
 * @author Liuzhenbin
 * @date 2021-04-22 14:47
 **/
public class Transformation {

    /**
     * 二维列向量关于笛卡尔坐标系的平移矩阵
     * @date 2021-04-23 12:04
     * @param tx x轴平移量
     * @param ty y轴平移量
     * @return math.Matrix3x3
     **/
    public static Matrix3x3 translation(float tx, float ty) {
        return new Matrix3x3(
                1, 0, tx,
                0, 1, ty,
                0, 0, 1
        );
    }

    public static Matrix3x3 translation(Vector2 point) {
        return translation(point.x(), point.y());
    }

    /**
     * 三维列向量关于笛卡尔坐标系的平移矩阵
     * @date 2021-04-23 12:05
     * @param tx x轴平移量
     * @param ty y轴平移量
     * @param tz z轴平移量
     * @return math.Matrix4x4
     **/
    public static Matrix4x4 translation(float tx, float ty, float tz) {
        return new Matrix4x4(
                1, 0, 0, tx,
                0, 1, 0, ty,
                0, 0, 1, tz,
                0, 0, 0, 1
        );
    }

    public static Matrix4x4 translation(Vector3 t) {
        return translation(t.x(), t.y(), t.z());
    }

    /**
     * 二维列向量关于笛卡尔坐标系的绕原点(即，xy平面)旋转欧拉角的旋转矩阵,
     * 角度大于零为逆时针旋转(右手螺旋定则)，小于零为顺时针旋转
     * [ x' , y' ] T = |  cos(θ)  sin(θ) | [ x , y ] T
     *                 | -sin(θ) cos(θ) |
     *  二维列向量[x,y]T左乘旋转矩阵后得到旋转后的坐标[x',y']T
     * @date 2021-04-22 15:33
     * @param eulerAngle 旋转角度(欧拉角)
     * @return math.Matrix2x2
     **/
    public static Matrix3x3 rotationAroundTheOriginal(float eulerAngle) {
        return new Matrix3x3(
                (float) cos(eulerAngle), (float) -Math.sin(eulerAngle), 0,
                (float) Math.sin(eulerAngle), (float) cos(eulerAngle), 0,
                0, 0, 1
        );
    }

    /**
     * 二维列向量绕笛卡尔坐标系中某点[x,y]旋转欧拉角的旋转矩阵。
     *  旋转矩阵 M = T(P)Rz(θ)T(-P)。
     *  先平移T(-P)到使得P点与坐标原点重合，然后旋转Rz(θ),最后平移T(P)使得P点回到原位
     * @date 2021-04-23 16:33
     * @param eulerAngle 旋转角
     * @param x 旋转参考点坐标x
     * @param y 旋转参考点坐标y
     * @return math.Matrix3x3
     **/
    public static Matrix3x3 rotationAroundPoint(float eulerAngle, float x, float y) {
        return (Matrix3x3)
                Matrix.multiply(
                        Matrix.multiply(
                                Transformation.translation(x, y),
                                Transformation.rotationAroundTheOriginal(eulerAngle)),
                        Transformation.translation(-x, -y)
                );
    }

    public static Matrix3x3 rotationAroundPoint(float eulerAngle, Vector2 point) {
        return rotationAroundPoint(eulerAngle, point.x(), point.y());
    }

    /**
     * 三维列向量关于右手笛卡尔坐标系的绕X轴(即，yz平面)旋转欧拉角的旋转矩阵。
     * 角度大于零为逆时针旋转（X轴指向视角），小于零为顺时针旋转，
     * 和右手螺旋方向相同。
     * @date 2021-04-22 18:07
     * @param eulerAngle 绕X轴旋转的欧拉角
     * @return math.Matrix3x3
     **/
    public static Matrix4x4 rotationAroundXAxis(float eulerAngle) {
        return new Matrix4x4(
                1, 0, 0, 0,
                0, (float) cos(eulerAngle), (float) -Math.sin(eulerAngle), 0,
                0, (float) Math.sin(eulerAngle), (float) cos(eulerAngle), 0,
                0, 0, 0, 1
        );
    }

    public static Matrix4x4 rotationAroundYAxis(float eulerAngle) {
        return new Matrix4x4(
                (float) cos(eulerAngle), 0, (float) Math.sin(eulerAngle), 0,
                0, 1, 0, 0,
                (float) -Math.sin(eulerAngle), 0, (float) cos(eulerAngle), 0,
                0, 0, 0, 1
        );
    }

    public static Matrix4x4 rotationAroundZAxis(float eulerAngle) {
        return new Matrix4x4(
                (float) cos(eulerAngle), (float) -Math.sin(eulerAngle), 0, 0,
                (float) Math.sin(eulerAngle), (float) cos(eulerAngle), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    /**
     * 三维列向量关于右手笛卡尔坐标系的绕某向量旋转欧拉角的旋转矩阵
     * @date 2021-04-23 16:54
     * @param eulerAngle 旋转欧拉角
     * @param axis 旋转轴
     * @return math.Matrix4x4
     **/
    public static Matrix4x4 rotationAroundVectorAxis(float eulerAngle, Vector3 axis) {
        Vector3 normalized = (Vector3) axis.normalized();
        float u = normalized.x();
        float v = normalized.y();
        float w = normalized.z();
        double v1 = u * v * (1 - cos(eulerAngle));
        double v2 = u * w * (1 - cos(eulerAngle));
        double v3 = v * w * (1 - cos(eulerAngle));
        return new Matrix4x4(
                (float) (pow(u, 2) + (1 - pow(u, 2)) * cos(eulerAngle)),
                (float) (v1 - w * sin(eulerAngle)),
                (float) (v2 + v * sin(eulerAngle)),
                0,
                (float) (v1 + w * sin(eulerAngle)),
                (float) (pow(v, 2) + (1 - pow(v, 2)) * cos(eulerAngle)),
                (float) (v3 - u * sin(eulerAngle)),
                0,
                (float) (v2 - v * sin(eulerAngle)),
                (float) (v3 + u * sin(eulerAngle)),
                (float) (pow(w, 2) + (1 - pow(w, 2)) * cos(eulerAngle)),
                0,
                0, 0, 0, 1
        );
    }

    public static Matrix3x3 scale(float sx, float sy) {
        return new Matrix3x3(
                sx, 0, 0,
                0, sy, 0,
                0, 0, 1
        );
    }

    public static Matrix4x4 scale(float sx, float sy, float sz) {
        return new Matrix4x4(
                sx, 0, 0, 0,
                0, sy, 0, 0,
                0, 0, sz, 0,
                0, 0, 0, 1
        );
    }

}
