package my.render;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:22
 **/
public class SoftwareRender {
    public AbstractShader shader;
    private Buffer<Vector4f>[] pixelBuffers = new Buffer[2];
    private int currentBufferIndex = 0;
    private Buffer<Float> zBuffer;
    public int viewpointX = 0;
    public int viewpointY = 0;
    public int viewpointWidth;
    public int viewpointHeight;
    public boolean earlyZ = true;
    public boolean depthTest = true;
    public float minDepth = 0;
    public float maxDepth = 1;
    public DepthTestFunc depthTestFunc = DepthTestFunc.GREATER;
    public boolean blend = true;
    public BlendFactor srcFactor = BlendFactor.ONE;
    public BlendFactor dstFactor = BlendFactor.ZERO;
    public BlendFunc blendFunc = BlendFunc.ADD;
    public static final SoftwareRender INSTINCE = new SoftwareRender();
    //纹理映射, 片元着色访问
    public List<Texture<Vector4f>> TEXTURES = new ArrayList<>();

    private SoftwareRender() {
    }

    public void buildBuffer(int width, int height) {
        for (int i = 0; i < pixelBuffers.length; i++) {
            this.pixelBuffers[i] = new Buffer<>(width, height, new Vector4f(0, 0, 0, 1));
        }
        this.zBuffer = new Buffer<>(width, height, -Float.MAX_VALUE);
        viewpointWidth = width;
        viewpointHeight = height;
    }

    public void clearBuffers() {
        zBuffer.clear();
        getNextBuffer().clear();
    }

    public void setClearColor(Vector4f color) {
        for (Buffer buffer : pixelBuffers) {
            buffer.defaultValue = color;
        }
    }

    public Buffer<Vector4f> getCurrentBuffer() {
        return pixelBuffers[currentBufferIndex];
    }

    private Buffer<Vector4f> getNextBuffer() {
        return pixelBuffers[1 - currentBufferIndex];
    }

    public void swapBuffers() {
        currentBufferIndex = 1 - currentBufferIndex;
    }

    public void drawTriangular(Vertex[] vertices) {
        for (Vertex value : vertices) {
            //顶点着色
            value.pos = shader.vertexShader(value);
        }
        //裁剪
        vertices= clipping(vertices);
        //投影除法, 标准设备坐标 (NDC) x,y,z->[-1, 1]
        for (Vertex vertex : vertices) {
            perspectiveDivide(vertex.pos);
        }
        //视口转换 ndc坐标转换为视口坐标.  x,y->[viewpoint.X, viewpointWidth] [viewpoint.Y, viewpointHeight] z->[minDepth, maxDepth]
        for (Vertex vertex : vertices) {
            viewpointTransform(vertex.pos, viewpointX, viewpointY, viewpointWidth, viewpointHeight, minDepth, maxDepth);
        }
        //图元组装
        for (int i = 0; i < vertices.length - 2; i++) {
            Vertex[] triangle = new Vertex[3];
            triangle[0] = vertices[0];
            triangle[1] = vertices[i + 1];
            triangle[2] = vertices[i + 2];
            //光栅化
            rasterizationTriangle(triangle);
        }
    }

    /**
     * 裁剪
     */
    private Vertex[] clipping(Vertex[] clipSpaceVertices){
        List<Vertex> out = Arrays.stream(clipSpaceVertices).collect(Collectors.toList());
        // z >= -w
        out = clippingTriangle(out, new Vector4f(0, 0, 1, 1));
        // z <= w
//        out = clipping_plane(out, new Vector4f(0, 0, -1, 1));

        return out.toArray(new Vertex[0]);
    }


    /**
     * 裁剪三角形
     * @param in    三角形顶点
     * @param plane 裁剪平面
     * @return      输出
     */
    private List<Vertex> clippingTriangle(List<Vertex> in, Vector4f plane) {
        //v1起点，v2终点
        Vertex v1, v2;
        List<Vertex> out = new ArrayList<>();
        int len = in.size();
        for (int i = 0; i < len; i++) {
            v1 = in.get((i - 1 + len) % len);
            v2 = in.get(i);
            clippingLine(v1, v2, plane, out);
        }
        return out;
    }

    /**
     * 裁剪线段
     * @param v1    线段起始顶点
     * @param v2    线段结束顶点
     * @param plane 裁剪平面
     * @param out   输出顶点
     */
    private void clippingLine(Vertex v1, Vertex v2, Vector4f plane, List<Vertex> out) {
        float f1 = plane.dotProduct(v1.pos), f2 = plane.dotProduct(v2.pos);
        float t;
        Vertex intersection;
        //存在交点
        if (f1 * f2 < 0) {
            //插值系数
            t = f1 / (f1- f2);
            //交点
            intersection = new Vertex();
            //插值计算顶点坐标
            intersection.pos = new Vector4f(
                    interpolateLinear(v1.pos.X, v2.pos.X, t),
                    interpolateLinear(v1.pos.Y, v2.pos.Y, t),
                    interpolateLinear(v1.pos.Z, v2.pos.Z, t),
                    interpolateLinear(v1.pos.W, v2.pos.W, t));
            //插值计算纹理坐标
            intersection.texCoords = new Vector2f(
                    interpolateLinear(v1.texCoords.X, v2.texCoords.X, t),
                    interpolateLinear(v1.texCoords.Y, v2.texCoords.Y, t));

            //插值计算法向量坐标
            intersection.normal = new Vector3f(
                    interpolateLinear(v1.normal.X, v2.normal.X, t),
                    interpolateLinear(v1.normal.Y, v2.normal.Y, t),
                    interpolateLinear(v1.normal.Z, v2.normal.Z, t));

            //执行顶点着色
            shader.vertexShader(intersection);

            //交点放到输出
            out.add(intersection);
        }
        //终点在内侧
        if (f2 > 0) {
            out.add(v2);
        }
    }

    /**
     * 透视除法
     */
    private void perspectiveDivide(Vector4f vertex) {
        vertex.X /= vertex.W;
        vertex.Y /= vertex.W;
        vertex.Z /= vertex.W;
    }

    private void viewpointTransform(Vector4f vertex, int viewpointX, int viewpointY, int width, int height, float minDepth, float maxDepth) {
        vertex.X = vertex.X * width * 0.5f + width * 0.5f + viewpointX;
        vertex.Y = vertex.Y * height * 0.5f + height * 0.5f + viewpointY;
        vertex.Z = 0.5f * (maxDepth - minDepth) * (vertex.Z + 1);
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

    /**
     * 计算点p在三角形abc中的重心坐标
     */
    private Vector3f barycentricCoordinates(Vector2f a, Vector2f b, Vector2f c, Vector2f p) {
//        //三角形abc的有向面积
//        float areaABC = edg(a, b, c); //ab*ac
//        //点P到三个顶点的有向面积
//        float areaABP = edg(a, b, p); //ab*ap
//        float areaBCP = edg(b, c, p); //bc*bp
//        float areaCAP = edg(c, a, p); //ca*cp
//        return new Vector3f(areaBCP/areaABC, areaCAP/areaABC, areaABP/areaABC); // bc*bp, ca*cp, ab*ap
        Vector3f x = new Vector3f(b.X - a.X, c.X - a.X, a.X - p.X);
        Vector3f y = new Vector3f(b.Y - a.Y, c.Y - a.Y, a.Y - p.Y);
        Vector3f u = x.cross(y); //ac*pa, pa*ab, ab*ac
        if (u.Z == 0)
            return new Vector3f(1.0f, 0.0f, 0.0f); // 点在ab上
        return new Vector3f(1.0f - (u.X + u.Y) / u.Z, u.X / u.Z, u.Y/ u.Z); // ( , ac*pa, pa*ab)
    }

    /**
     * 线性插值
     * res = a + w * (b-a)
     */
    public float interpolateLinear(float a, float b, float w) {
        return a + w * (b - a);
    }


    /**
     * 重心坐标线性插值
     * res = f1 * bc.x + f2 * bc.y + f3 * bc.z
     */
    public float interpolateBarycentric(float f1, float f2, float f3, Vector3f bc) {
        return f1 * bc.X + f2 * bc.Y + f3 * bc.Z;
    }

    /**
     * 光栅化三角形
     */
    void rasterizationTriangle(Vertex[] vertices) {
        //渲染缓冲区
        Buffer<Vector4f> pixelBuffer = getNextBuffer();
        //投影平面的3个点
        Vector2f a = vertices[0].pos.toVector2f(), b = vertices[1].pos.toVector2f(), c = vertices[2].pos.toVector2f();

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
                //采样像素的中点
                Vector2f p = new Vector2f(x + 0.5f, y + 0.5f);
                //点P到三个顶点的有向面积
                float areaABP = edg(a, b, p);
                float areaBCP = edg(b, c, p);
                float areaCAP = edg(c, a, p);

                //屏幕空间投影点的三角形重心坐标: barycentric (i, j, k)
                Vector3f barycentric = new Vector3f(areaBCP / areaABC, areaCAP / areaABC, areaABP / areaABC);
                //判断点p是否在三角形内
                if (barycentric.X < 0 || barycentric.Y < 0 || barycentric.Z < 0) continue;

                //不能使用屏幕空间的计算的重心坐标插值计算观察空间坐标的Z
                //因为经过透视除法后的屏幕空间Z是非线性的，所以插值出来的深度值和观察空间的Z是非线性相关的
                //所以在靠经近平面的时深度值精度高，而远离近平面时精度低
//                float z = interpolateBarycentric(vertices[0].pos.Z, vertices[1].pos.Z, vertices[2].pos.Z, barycentric);

                //屏幕空间投影点在观察空间中的Z, 1/Zn = i * 1/Z1 + j * 1/Z2 + k * 1/Z3
                //投影坐标的w = 摄像空间的-Z
                barycentric.X /= -vertices[0].pos.W;
                barycentric.Y /= -vertices[1].pos.W;
                barycentric.Z /= -vertices[2].pos.W;

                //观察空间下的坐标z
                float z = 1f / (barycentric.X + barycentric.Y + barycentric.Z);

                //early Z test
                if (earlyZ && !depthTest(x, y, z)) {
                    continue;
                }

                //重心坐标正, 属性插值公式: In = (i * I1/Z1 + j * I2/Z2 + k * I3/Z3) * zn
                barycentric.X *= z;   // i * 1/Z1 * Zn
                barycentric.Y *= z;   // j * 1/Z2 * Zn
                barycentric.Z *= z;   // k * 1/Z3 * Zn

                Fragment frag = new Fragment();
                //裁剪空间坐标
                frag.pos = new Vector3f(p.X, p.Y, z);

                //插值计算片元着色器变量
                for (Map.Entry<String, float[]> varying : vertices[0].shaderVaryings.entrySet()) {
                    float[] varyingValue = new float[varying.getValue().length];
                    for (int i = 0; i < varying.getValue().length; i++) {
                        //插值计算变量
                        varyingValue[i] = interpolateBarycentric(
                                vertices[0].shaderVaryings.get(varying.getKey())[i],
                                vertices[1].shaderVaryings.get(varying.getKey())[i],
                                vertices[2].shaderVaryings.get(varying.getKey())[i],
                                barycentric);
                    }
                    frag.shaderVaryings.put(varying.getKey(), varyingValue);
                }

                //执行片元着色器
                rgbaColor = shader.fragmentShader(TEXTURES, frag);

                if(rgbaColor.X > 1f) rgbaColor.X = 1f;
                if(rgbaColor.Y > 1f) rgbaColor.Y = 1f;
                if(rgbaColor.Z > 1f) rgbaColor.Z = 1f;
                if(rgbaColor.W > 1f) rgbaColor.W = 1f;

                //深度测试
                if (depthTest && !depthTest(x, y, z)) {
                    continue;
                }
                zBuffer.set(x, y, z);

                //颜色混合
                if (blend) {
                    rgbaColor = colorBlending(rgbaColor, pixelBuffer.get(x, y), srcFactor, dstFactor, blendFunc);
                }

                pixelBuffer.set(x, y, rgbaColor);
            }
        }
    }

    /**
     * 颜色混合
     * @param src       源颜色
     * @param dst       目标颜色
     * @param srcFactor 源颜色混合因子
     * @param dstFactor 目标颜色混合因子
     * @param blendFunc 混合函数
     */
    private Vector4f colorBlending(Vector4f src, Vector4f dst, BlendFactor srcFactor, BlendFactor dstFactor, BlendFunc blendFunc) {
       return blendFunc.blend(src.multiply(srcFactor.factor(src, dst)), dst.multiply(dstFactor.factor(src, dst)));
    }

    private boolean depthTest(int x, int y, float depth) {
        return depthTestFunc.test(zBuffer.get(x, y), depth);
    }

}
