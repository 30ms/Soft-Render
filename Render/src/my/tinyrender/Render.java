package my.tinyrender;

import my.render.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/3/28 10:35
 **/
public class Render {
    PrimitiveType primitiveType;
    RenderState renderState = new RenderState();
    FrameBuffer frameBuffer;
    int[] indexBuffer;
    VertexArrayObject vao;
    private final List<VertexHolder> vertexes = new ArrayList<>();
    private final List<PrimitiveHolder> primitives = new ArrayList<>();

    VertexShader vertexShader;
    FragmentShader fragmentShader;
    private Map<String,float[]>[] varyings;
    Map<String,float[]> uniforms;



    public void clear() {
        frameBuffer.clearDepth(Float.MAX_VALUE);
        frameBuffer.clearColor(renderState.clearState.clearColor);
    }

    //draw pipeline
    public void draw(PrimitiveType primitiveType) {
        this.primitiveType = primitiveType;
        //顶点着色
        processVertexShader();
        //图元组装
        processPrimitiveAssembly();
        //裁剪
        processClipping();
        //投影除法(齐次除法)
        processPerspectiveDivide();
        //视口转换
        processViewportTransform();
        //背面剔除
        processFaceCulling();
        //光栅化
        processRasterization();
    }

    private void processVertexShader() {
        int vertexCnt = vao.vertex_buffer.position() / vao.vertexAttributePointers[0].stride;
        vertexes.clear();
        varyings = new Map[vertexCnt];
        for (int i = 0; i < vertexCnt; i++) {
            VertexHolder vertex = new VertexHolder();
            vertexes.add(vertex);
            vertex.discard = false;
            varyings[i] = new HashMap<>();
            vertex.clipPos = vertexShader.shader(vao, i, varyings[i], uniforms);
        }
    }

    private void processPrimitiveAssembly() {
        switch (primitiveType) {
            case PRIMITIVE_TYPE_POINT: break;
            case PRIMITIVE_TYPE_LINE: break;
            case PRIMITIVE_TYPE_TRIANGLE:
                triangleAssembly();
                break;
        }
    }

    private void triangleAssembly() {
        primitives.clear();
        int triangleCnt = indexBuffer.length / 3;
        for (int i = 0; i < triangleCnt; i++) {
            PrimitiveHolder primitiveHolder = new PrimitiveHolder();
            primitiveHolder.indices = new int[3];
            primitiveHolder.indices[0] = indexBuffer[i * 3];
            primitiveHolder.indices[1] = indexBuffer[i * 3 + 1];
            primitiveHolder.indices[2] = indexBuffer[i * 3 + 2];
            primitives.add(primitiveHolder);
        }
    }

    private void processPerspectiveDivide() {
        for (VertexHolder vertex : vertexes) {
            if(vertex.discard) continue;
            perspectiveDivideImpl(vertex);
        }
    }

    private void perspectiveDivideImpl(VertexHolder vertexHolder) {
        float invW = 1.f / vertexHolder.clipPos.W;
        vertexHolder.fragPos = new Vector3f(vertexHolder.clipPos.X * invW, vertexHolder.clipPos.Y * invW, vertexHolder.clipPos.Z * invW);
    }

    private void processClipping() {
        for (PrimitiveHolder primitiveHolder : primitives) {
            if (primitiveHolder.discard) continue;
            switch (primitiveType) {
                case PRIMITIVE_TYPE_POINT:
                    clippingPoint(primitiveHolder);
                    break;
                case PRIMITIVE_TYPE_LINE:
                    clippingLine(primitiveHolder);
                    break;
                case PRIMITIVE_TYPE_TRIANGLE:
                    clippingTriangle(primitiveHolder);
                    break;
            }
        }
    }

    private void clippingPoint(PrimitiveHolder point) {
    }

    private void clippingLine(PrimitiveHolder line) {
    }

    private void clippingTriangle(PrimitiveHolder triangle) {
    }

    private void processViewportTransform() {
        for (VertexHolder vertex : vertexes) {
            if(vertex.discard) continue;
            viewportTransformImpl(vertex);
        }
    }

    private void viewportTransformImpl(VertexHolder holder) {
        holder.fragPos.X = (holder.fragPos.X + 1) * frameBuffer.width * 0.5f;
        holder.fragPos.Y = (holder.fragPos.Y + 1) * frameBuffer.height * 0.5f;
    }

    private void processFaceCulling() {
        if (primitiveType != PrimitiveType.PRIMITIVE_TYPE_TRIANGLE) {
            return;
        }
        for (PrimitiveHolder triangle : primitives) {
            if(triangle.discard) continue;
            Vector3f v0 = vertexes.get(triangle.indices[0]).fragPos;
            Vector3f v1 = vertexes.get(triangle.indices[1]).fragPos;
            Vector3f v2 = vertexes.get(triangle.indices[2]).fragPos;
            //法向量
            Vector3f n = v1.reduce(v0).cross(v2.reduce(v0));
            float area = n.dotProduct(new Vector3f(0, 0, 1));

            if (renderState.cullFace) {
                //背面剔除
                triangle.discard = area > 0;
            }
        }
    }

    private void processRasterization() {
        switch (primitiveType) {
            case PRIMITIVE_TYPE_POINT: break;
            case PRIMITIVE_TYPE_LINE: break;
            case PRIMITIVE_TYPE_TRIANGLE:
                rasterizePolygons(primitives);
                break;
        }

    }

    float interpolateLinear(float v0, float v1, float t) {
        return v0 * (1 - t) + v1 * t;
    }

    float interpolateBarycentric(float src0, float src1, float src2, float i, float j, float k) {
        return src0 * i + src1 * j + src2 * k;
    }

    private void rasterizePolygons(List<PrimitiveHolder> primitives) {
        switch (renderState.polygonMode) {
            case PolygonMode_POINT:
                break;
            case PolygonMode_LINE:
                break;
            case PolygonMode_FILL:
                rasterizePolygonsTriangle(primitives);
                break;
        }
    }

    private void rasterizePolygonsTriangle(List<PrimitiveHolder> primitives) {
        for (PrimitiveHolder primitive : primitives) {
            if (primitive.discard) continue;
            rasterizeTriangle(
                    vertexes.get(primitive.indices[0]),
                    vertexes.get(primitive.indices[1]),
                    vertexes.get(primitive.indices[2]),
                    varyings[primitive.indices[0]],
                    varyings[primitive.indices[1]],
                    varyings[primitive.indices[2]],
                    uniforms);
        }
    }

    private float edg(Vector3f a, Vector3f b, Vector3f c) {
        return (b.X - a.X) * (c.Y - a.Y) - (b.Y - a.Y) * (c.X - a.X);
    }

    private void rasterizeTriangle(VertexHolder v0, VertexHolder v1, VertexHolder v2,
                                   Map<String, float[]> varying_vo, Map<String, float[]> varying_v1, Map<String, float[]> varying_v2,
                                   Map<String, float[]> uniforms) {
        Vector3f a = v0.fragPos, b = v1.fragPos, c = v2.fragPos;

        float startX = Math.min(Math.min(a.X, b.X), c.X);
        float endX = Math.max(Math.max(a.X, b.X), c.X);
        float startY = Math.min(Math.min(a.Y, b.Y), c.Y);
        float endY = Math.max(Math.max(a.Y, b.Y), c.Y);
        startX = Math.max(0, startX);
        endX = Math.min(endX, frameBuffer.width);
        startY = Math.max(0, startY);
        endY = Math.min(endY, frameBuffer.height);

        float areaABC = edg(a, b, c);

        for (int y = (int) startY; y < endY; y++) {
            for (int x = (int) startX; x < endX; x++) {
                //+0.5f 像素格中心对齐
                Vector3f p = new Vector3f(x + 0.5f, y + 0.5f, 0);
                //点P到三个顶点的有向面积
                float areaABP = edg(a, b, p);
                float areaBCP = edg(b, c, p);
                float areaCAP = edg(c, a, p);

                //屏幕空间投影点的三角形重心坐标: barycentric (i, j, k)
                float i = areaBCP / areaABC, j = areaCAP / areaABC, k = areaABP / areaABC;
                //判断点p是否在三角形内
                if (i < 0 || j < 0 || k < 0) continue;

                //屏幕空间投影点在裁剪空间中的Z, 1/Zn = i * 1/Z1 + j * 1/Z2 + k * 1/Z3
                i = i / v0.clipPos.W;
                j = j / v1.clipPos.W;
                k = k / v2.clipPos.W;

                float z = 1f / (i + j + k);
                //深度测试
                if (renderState.depthTest) {
                    switch (renderState.depthFunc) {
                        case DepthFunc_ALWAYS:
                            break;
                        case DepthFunc_NEVER:
                            continue;
                        case DepthFunc_LESS:
                            if(frameBuffer.depth(x,y) <= z) continue;
                            break;
                        case DepthFunc_LEQUAL:
                            if(frameBuffer.depth(x,y) < z) continue;
                            break;
                        case DepthFunc_GREATER:
                            if(frameBuffer.depth(x,y) >= z) continue;
                            break;
                        case DepthFunc_GEQUAL:
                            if(frameBuffer.depth(x,y) > z) continue;
                            break;
                        case DepthFunc_EQUAL:
                            if(frameBuffer.depth(x,y) != z) continue;
                            break;
                        case DepthFunc_NOTEQUAL:
                            if(frameBuffer.depth(x,y) == z) continue;
                            break;
                    }
                }
                frameBuffer.depth(x, y, z);

                //插值变量矫正, 属性插值公式: In = (i * I1/Z1 + j * I2/Z2 + k * I3/Z3) * zn
                i *= z;   // i * 1/Z1 * Zn
                j *= z;   // j * 1/Z2 * Zn
                k *= z;   // k * 1/Z3 * Zn

                float w_i = i, w_j = j, w_k = k;
                //插值着色器变量
                Map<String, float[]> varyings = varying_vo.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                            String key = entry.getKey();
                            float[] value = entry.getValue();
                            float[] res = new float[value.length];
                            for (int idx = 0; idx < res.length; idx++) {
                                res[idx] = interpolateBarycentric(value[idx], varying_v1.get(key)[idx], varying_v2.get(key)[idx], w_i, w_j, w_k);
                            }
                            return res;
                        }));
                //片元着色器
                frameBuffer.color(x, y, fragmentShader.shader(varyings, uniforms));
            }
        }
    }
}
