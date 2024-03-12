package my.tinyrender;

import my.tinyrender.model.Texture;
import my.render.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/4/7 17:01
 **/
public class Main {
    public static void main(String[] args) throws IOException {
        Render render = new Render();
        Camera camera = new Camera(new Vector3f(0, 0, 3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), 45f, 1f, 0.1f, 10f);

        float[] vertex_pos = {
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                // Z-
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                // Y+
                0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                // Y-
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                // X+
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                // X-
                -0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
        };

        float[] uvs = new float[]{
                // Z+
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // Z-
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // Y+
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // Y-
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // X+
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,

                // X-
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
        };

        Texture<Vector3f> texture = new Texture<>(new Vector3f[]
                {
                        new Vector3f(255, 0, 0),
                        new Vector3f(0, 255, 0),
                        new Vector3f(0, 0, 255),
                        new Vector3f(0, 255, 255),
                }, 2);

        VertexArrayObject vao = new VertexArrayObject();
        render.vao = vao;
        //设置Vertex buffer数据
        int vertexCnt = vertex_pos.length / 3;
        int vertexAttributeBytes = 3 * GL_TYPE.FLOAT.byteSize + 2 * GL_TYPE.FLOAT.byteSize;
        vao.vertex_buffer = ByteBuffer.allocate(vertexCnt * vertexAttributeBytes);
        for (int i = 0; i < vertexCnt; i++) {
            //pos
            vao.vertex_buffer.putFloat(vertex_pos[i * 3]);     //x
            vao.vertex_buffer.putFloat(vertex_pos[i * 3 + 1]); //y
            vao.vertex_buffer.putFloat(vertex_pos[i * 3 + 2]); //z
            //uv
            vao.vertex_buffer.putFloat(uvs[i * 2]);            //u
            vao.vertex_buffer.putFloat(uvs[i * 2 + 1]);        //v
        }
        vao.vertexAttributePointers = new VertexAttributePointer[2];
        //设置顶点位置属性
        int vertex_pos_layout = 0;
        vao.vertexAttributePointers[vertex_pos_layout] = new VertexAttributePointer();
        vao.vertexAttributePointers[vertex_pos_layout].size = 3;
        vao.vertexAttributePointers[vertex_pos_layout].stride = vertexAttributeBytes;
        vao.vertexAttributePointers[vertex_pos_layout].offset = 0;
        vao.vertexAttributePointers[vertex_pos_layout].type = GL_TYPE.FLOAT;
        //设置顶点uv属性
        int vertex_uv_layout = 1;
        vao.vertexAttributePointers[vertex_uv_layout] = new VertexAttributePointer();
        vao.vertexAttributePointers[vertex_uv_layout].size = 2;
        vao.vertexAttributePointers[vertex_uv_layout].stride = vertexAttributeBytes;
        vao.vertexAttributePointers[vertex_uv_layout].offset = 3 * GL_TYPE.FLOAT.byteSize;
        vao.vertexAttributePointers[vertex_uv_layout].type = GL_TYPE.FLOAT;

        //设置图元索引缓冲数据
        render.indexBuffer = new int[]{
                0, 3, 1,
                1, 3, 2,
                4, 5, 7,
                5, 6, 7,
                8, 11, 9,
                9, 11, 10,
                12, 13, 15,
                13, 14, 15,
                16, 19, 17,
                17, 19, 18,
                20, 21, 23,
                21, 22, 23};

        Matrix4x4f mMat = Matrix4x4f.identify();
        Matrix4x4f vMat = Matrix4x4f.identify();
        Matrix4x4f pMat = Matrix4x4f.identify();

        //设置着色器全局变量
        render.uniforms = new HashMap<>();
        Matrix4x4f modelMat = Matrix4x4f.rotationY(30);
        render.uniforms.put("modelMat", new float[]{
                modelMat.A11, modelMat.A12, modelMat.A13, modelMat.A14,
                modelMat.A21, modelMat.A22, modelMat.A23, modelMat.A24,
                modelMat.A31, modelMat.A32, modelMat.A33, modelMat.A34,
                modelMat.A41, modelMat.A42, modelMat.A43, modelMat.A44
        });
        render.uniforms.put("viewMat", new float[]{
                camera.viewMat.A11, camera.viewMat.A12, camera.viewMat.A13, camera.viewMat.A14,
                camera.viewMat.A21, camera.viewMat.A22, camera.viewMat.A23, camera.viewMat.A24,
                camera.viewMat.A31, camera.viewMat.A32, camera.viewMat.A33, camera.viewMat.A34,
                camera.viewMat.A41, camera.viewMat.A42, camera.viewMat.A43, camera.viewMat.A44
        });
        render.uniforms.put("projMat", new float[]{
                camera.projectionMat.A11, camera.projectionMat.A12, camera.projectionMat.A13, camera.projectionMat.A14,
                camera.projectionMat.A21, camera.projectionMat.A22, camera.projectionMat.A23, camera.projectionMat.A24,
                camera.projectionMat.A31, camera.projectionMat.A32, camera.projectionMat.A33, camera.projectionMat.A34,
                camera.projectionMat.A41, camera.projectionMat.A42, camera.projectionMat.A43, camera.projectionMat.A44
        });

        //顶点着色器
        render.vertexShader = (vao1, vertexIndex, varyings, uniforms) -> {
            //获取矩阵全局变量
            float[] mMat_f = uniforms.get("modelMat");
            float[] vMat_f = uniforms.get("viewMat");
            float[] pMat_f = uniforms.get("projMat");
            //顶点模型空间xyz坐标
            Vector3f vp = vao1.getVertexAttribute(vertexIndex, vertex_pos_layout, Vector3f.class);
            //顶点纹理uv坐标
            Vector2f uv = vao1.getVertexAttribute(vertexIndex, vertex_uv_layout, Vector2f.class);
            //设置uv坐标变量
            varyings.put("uv", new float[]{uv.X, uv.Y});
            //模型矩阵
            mMat.A11 = mMat_f[0]; mMat.A12 = mMat_f[1]; mMat.A13 = mMat_f[2]; mMat.A14 = mMat_f[3];
            mMat.A21 = mMat_f[4]; mMat.A22 = mMat_f[5]; mMat.A23 = mMat_f[6]; mMat.A24 = mMat_f[7];
            mMat.A31 = mMat_f[8]; mMat.A32 = mMat_f[9]; mMat.A33 = mMat_f[10]; mMat.A34 = mMat_f[11];
            mMat.A41 = mMat_f[12]; mMat.A42 = mMat_f[13]; mMat.A43 = mMat_f[14]; mMat.A44 = mMat_f[15];
            //设置视图矩阵
            vMat.A11 = vMat_f[0]; vMat.A12 = vMat_f[1]; vMat.A13 = vMat_f[2]; vMat.A14 = vMat_f[3];
            vMat.A21 = vMat_f[4]; vMat.A22 = vMat_f[5]; vMat.A23 = vMat_f[6]; vMat.A24 = vMat_f[7];
            vMat.A31 = vMat_f[8]; vMat.A32 = vMat_f[9]; vMat.A33 = vMat_f[10]; vMat.A34 = vMat_f[11];
            vMat.A41 = vMat_f[12]; vMat.A42 = vMat_f[13]; vMat.A43 = vMat_f[14]; vMat.A44 = vMat_f[15];
            //设置投影矩阵
            pMat.A11 = pMat_f[0]; pMat.A12 = pMat_f[1]; pMat.A13 = pMat_f[2]; pMat.A14 = pMat_f[3];
            pMat.A21 = pMat_f[4]; pMat.A22 = pMat_f[5]; pMat.A23 = pMat_f[6]; pMat.A24 = pMat_f[7];
            pMat.A31 = pMat_f[8]; pMat.A32 = pMat_f[9]; pMat.A33 = pMat_f[10]; pMat.A34 = pMat_f[11];
            pMat.A41 = pMat_f[12]; pMat.A42 = pMat_f[13]; pMat.A43 = pMat_f[14]; pMat.A44 = pMat_f[15];
            // mvp 矩阵
            Matrix4x4f mvp = pMat.multiply(vMat).multiply(mMat);
            return mvp.multiply(new Vector4f(vp));
        };
        //片元着色器
        render.fragmentShader = (varyings, uniforms) -> {
            float[] uv = varyings.get("uv");
            //纹理采样
            return new Vector4f(texture.getPixel((int) (uv[0] * texture.width), (int) (uv[1] * texture.height)));
        };

        int width = 480, height = 480;
        render.renderState.depthTest = true;
        render.frameBuffer = new FrameBuffer(width, height);
        render.clear();
        render.draw(PrimitiveType.PRIMITIVE_TYPE_TRIANGLE);

        BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < render.frameBuffer.width; x++) {
            for (int y = 0; y < render.frameBuffer.height; y++) {
                Vector4f color = render.frameBuffer.color(x, y);
                renderedImage.setRGB(x, y, (int) (65536 * color.Z + 256 * color.Y + color.X));
            }
        }
        ImageIO.write(renderedImage, "png", new File("out.png"));
    }
}
