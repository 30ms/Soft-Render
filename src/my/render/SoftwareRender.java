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
    private final Rasterizer rasterizer = Rasterizer.INSTINSE;
    public static final SoftwareRender INSTINCE = new SoftwareRender();
    //纹理映射, 片元着色访问
    public Map<String, Texture> TEXTURES = new HashMap<>();

    private SoftwareRender() {
    }

    public void buildBuffer(int width, int height) {
        for (int i = 0; i < pixelBuffers.length; i++) {
            this.pixelBuffers[i] = new Buffer<>(width, height, new Vector4f(0, 0, 0, 1));
        }
        this.zBuffer = new Buffer<>(width, height, -Float.MAX_VALUE);
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
        //图元组装
        for (int i = 0; i < vertices.length - 2; i++) {
            Vertex[] triangle = new Vertex[3];
            triangle[0] = vertices[0];
            triangle[1] = vertices[i + 1];
            triangle[2] = vertices[i + 2];
            //光栅化
            rasterizer.drawTriangles(triangle, shader, TEXTURES, zBuffer, getNextBuffer());;
        }
    }

    /**
     * 裁剪
     */
    private Vertex[] clipping(Vertex[] clipSpaceVertices){
        List<Vertex> out = Arrays.stream(clipSpaceVertices).collect(Collectors.toList());
        // z >= -w
        out = clippingPlane(out, new Vector4f(0, 0, 1, 1));
        // z <= w
//        out = clipping_plane(out, new Vector4f(0, 0, -1, 1));

        return out.toArray(new Vertex[0]);
    }


    private List<Vertex> clippingPlane(List<Vertex> in, Vector4f plane) {
        //v1起点，v2终点
        Vertex v1, v2;
        List<Vertex> out = new ArrayList<>();
        int len = in.size();
        for (int i = 0; i < len; i++) {
            v1 = in.get((i - 1 + len) % len);
            v2 = in.get(i);

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
                        v1.pos.X + t * (v2.pos.X - v1.pos.X),
                        v1.pos.Y + t * (v2.pos.Y - v1.pos.Y),
                        v1.pos.Z + t * (v2.pos.Z - v1.pos.Z),
                        v1.pos.W + t * (v2.pos.W - v1.pos.W));
                //插值计算纹理坐标
                intersection.texCoords = new Vector2f(
                        v1.texCoords.X + t * (v2.texCoords.X - v1.texCoords.X),
                        v1.texCoords.Y + t * (v2.texCoords.Y - v1.texCoords.Y));

                //插值计算法向量坐标
                intersection.normal = new Vector3f(
                        v1.normal.X + t * (v2.normal.X - v1.normal.X),
                        v1.normal.Y + t * (v2.normal.Y - v1.normal.Y),
                        v1.normal.Z + t * (v2.normal.Z - v1.normal.Z)
                );

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
        return out;
    }

}
