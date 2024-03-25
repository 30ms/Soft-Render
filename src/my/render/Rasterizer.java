package my.render;

import java.util.Arrays;
import java.util.Map;

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

    void drawTriangles(Vertex[] vertices, AbstractShader shader, Map<String, Texture> textures, Buffer<Float> zBuffer, Buffer<Vector4f> pixelBuffer) {
        //裁剪空间
        Vector4f[] vertices_clip = Arrays.stream(vertices).map(v -> v.pos).toArray(Vector4f[]::new);
        //投影除法, 标准设备坐标 (NDC)
        Vector3f[] vertices_ndc = toNDC(vertices_clip);
        //视口转换
        Vector2f[] vertices_vp = viewpointTransform(pixelBuffer.width, pixelBuffer.height, vertices_ndc);
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

        Vector4f rgbaColor;

        for (int y = (int) startY; y < endY; y++) {
            for (int x = (int) startX; x < endX; x++) {
                Vector2f p = new Vector2f(x, y);
                //点P到三个顶点的有向面积
                float areaABP = edg(a, b, p);
                float areaBCP = edg(b, c, p);
                float areaCAP = edg(c, a, p);

                //屏幕空间投影点的三角形重心坐标: barycentric (i, j, k)
                Vector3f barycentric = new Vector3f(areaBCP / areaABC, areaCAP / areaABC, areaABP / areaABC);
                //判断点p是否在三角形内
                if (barycentric.X < 0 || barycentric.Y < 0 || barycentric.Z < 0) continue;

                //屏幕空间投影点在观察空间中的Z, 1/Zn = i * 1/Z1 + j * 1/Z2 + k * 1/Z3
                //投影坐标的w = 摄像空间的-Z
                barycentric.X /= -vertices_clip[0].W;
                barycentric.Y /= -vertices_clip[1].W;
                barycentric.Z /= -vertices_clip[2].W;

                float zN = 1f / (barycentric.X + barycentric.Y + barycentric.Z);

                //ZBuffer测试
                if (zBuffer.get(x, y) > zN) continue;
                zBuffer.set(x, y, zN);

                //插值变量矫正, 属性插值公式: In = (i * I1/Z1 + j * I2/Z2 + k * I3/Z3) * zn
                barycentric.X *= zN;   // i * 1/Z1 * Zn
                barycentric.Y *= zN;   // j * 1/Z2 * Zn
                barycentric.Z *= zN;   // k * 1/Z3 * Zn

                Fragment frag = new Fragment();
                //裁剪空间坐标
                frag.pos = new Vector3f(x, y, zN);

                //插值计算片元着色器变量
                for (Map.Entry<String, float[]> varying : vertices[0].shaderVaryings.entrySet()) {
                    float[] varyingValue = new float[varying.getValue().length];
                    for (int i = 0; i < varying.getValue().length; i++) {
                        varyingValue[i] = barycentric.X * vertices[0].shaderVaryings.get(varying.getKey())[i] +
                                barycentric.Y * vertices[1].shaderVaryings.get(varying.getKey())[i] +
                                barycentric.Z * vertices[2].shaderVaryings.get(varying.getKey())[i];
                    }
                    frag.shaderVaryings.put(varying.getKey(), varyingValue);
                }

                //执行片元着色器
                rgbaColor = shader.fragmentShader(textures, frag);

                if(rgbaColor.X > 1f) rgbaColor.X = 1f;
                if(rgbaColor.Y > 1f) rgbaColor.Y = 1f;
                if(rgbaColor.Z > 1f) rgbaColor.Z = 1f;
                if(rgbaColor.W > 1f) rgbaColor.W = 1f;

                Vector4f dstColor = pixelBuffer.get(x, y);
                //透明度混合(alpha blending)
                //混合公式: res = src * (srcAlpha) + dst * (1-srcAlpha)
                pixelBuffer.set(x, y, rgbaColor.scale(rgbaColor.W).add(dstColor.scale(1 - rgbaColor.W)));
            }
        }
    }

    private Vector3f[] toNDC(Vector4f[] vertices) {
      return   Arrays.stream(vertices)
                .map(v -> new Vector3f(v.X / v.W, v.Y / v.W, v.Z / v.W))
                .toArray(Vector3f[]::new);
    }

    private Vector2f[] viewpointTransform(int width, int height, Vector3f[] vertices) {
        return Arrays.stream(vertices)
                .map(vector3f -> new Vector2f((float) ((vector3f.X + 1) * width * 0.5), (float) ((vector3f.Y + 1) * height * 0.5)))
                .toArray(Vector2f[]::new);
    }
}
