package my.render;

import java.util.Arrays;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:24
 **/
public class Rasterizer {

    public static final Rasterizer INSTINSE = new Rasterizer();
    private Rasterizer() {
    }

    /**
     *        c
     *      /  \
     *    a ——— b
     *    逆时针顺序a -> b -> c    向量ab,ac的平行四边形的有向面积
     */
    private float edg(Vector2f a, Vector2f b, Vector2f c) {
        return (b.X - a.X) * (c.Y - a.Y) - (b.Y - a.Y) * (c.X - a.X);
    }

    void drawTriangles(Vector4f[] vertices, Matrix4x4f projectionMat, AbstractShader shader, Buffer<Float> zBuffer, Buffer<Vector3i> pixelBuffer) {
        //投影裁剪空间坐标
        Vector4f[] vertices_clip = Arrays.stream(vertices).map(projectionMat::multiply).toArray(Vector4f[]::new);
        //裁剪
        if (clipping(vertices_clip)) return;
        //投影除法, 标准设备坐标 (NDC)
        Vector3f[] vertices_ndc = toNDC(vertices_clip);
        //视口转换
        Vector2f[] vertices_vp = viewpointTransform(pixelBuffer, vertices_ndc);
        //投影平面的3个点
        Vector2f a = vertices_vp[0], b = vertices_vp[1], c = vertices_vp[2];

        float startX = Math.min(Math.min(a.X, b.X), c.X);
        float endX = Math.max(Math.max(a.X, b.X), c.X);
        float startY = Math.min(Math.min(a.Y, b.Y), c.Y);
        float endY = Math.max(Math.max(a.Y, b.Y), c.Y);
        startX = Math.max(0, startX);
        endX = Math.min(endX, pixelBuffer.width);
        startY = Math.max(0, startY);
        endY = Math.min(endY, pixelBuffer.height);

        float areaABC = edg(a, b, c);

        Vector3i rgbColor;

        for (int y = (int) startY; y < endY; y++) {
            for (int x = (int) startX; x < endX; x++) {
                Vector2f p = new Vector2f(x, y);
                //点P到三个顶点的有向面积
                float areaABP = edg(a, b, p);
                float areaBCP = edg(b, c, p);
                float areaCAP = edg(c, a, p);

                //投影点的三角形重心坐标
                Vector3f barycentric = new Vector3f(areaBCP / areaABC, areaCAP / areaABC, areaABP / areaABC);
                //判断点p是否在三角形内
                if (barycentric.X < 0 || barycentric.Y < 0 || barycentric.Z < 0) continue;

                //TODO 此处矫正有问题
                barycentric.X = barycentric.X / vertices_ndc[0].Z;
                barycentric.Y = barycentric.Y / vertices_ndc[1].Z;
                barycentric.Z = barycentric.Z / vertices_ndc[2].Z;
                //透视矫正后点在空间中的Z
                float revise_z = 1f / (barycentric.X + barycentric.Y + barycentric.Z);
                //插值矫正
                barycentric.X *= revise_z;
                barycentric.Y *= revise_z;
                barycentric.Z *= revise_z;
                //ZBuffer测试
                if (zBuffer.get(x, y) < revise_z) continue;
                zBuffer.set(x, y, revise_z);

                //执行片元着色器
                rgbColor = shader.fragment(barycentric);
                pixelBuffer.set(x, y, rgbColor);
            }
        }
    }

    private boolean clipping(Vector4f[] clipSpaceVertices) {
        int count = 0;
        for (Vector4f vertex : clipSpaceVertices) {
            //判断点是否在外面
            if((vertex.X < -vertex.W || vertex.X > vertex.W)  && (vertex.Y < -vertex.W || vertex.Y > vertex.W) && (vertex.Z < -vertex.W || vertex.Z > vertex.W))
                count++;
        }
        //是否全部点都在外面
        return count == clipSpaceVertices.length;
    }

    private Vector3f[] toNDC(Vector4f[] vertices) {
      return   Arrays.stream(vertices)
                .map(v -> new Vector3f(v.X / v.W, v.Y / v.W, v.Z / v.W))
                .toArray(Vector3f[]::new);
    }

    private Vector2f[] viewpointTransform(Buffer<Vector3i> pixelBuffer, Vector3f[] vertices) {
        return Arrays.stream(vertices)
                .map(vector3f -> new Vector2f((float) ((vector3f.X + 1) * pixelBuffer.width * 0.5), (float) ((vector3f.Y + 1) * pixelBuffer.height * 0.5)))
                .toArray(Vector2f[]::new);
    }
}
