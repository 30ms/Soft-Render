/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 17:24
 **/
public class Rasterizer {


    // 向量ac, 向量ab的平行四边形的有向面积
    private float edg(Vector3f a, Vector3f b, Vector3f c) {
        return (c.X - a.X) * (b.Y - a.Y) - (b.X - a.X) * (c.Y - a.Y);
    }

    void drawTriangles(Vector3f[] vertices, IShader shader, Buffer<Float> zBuffer, Buffer<Vector3i> pixelBuffer) {

        //视口转换
        viewpointTransform(pixelBuffer, vertices);

        Vector3f a = vertices[0], b = vertices[1], c = vertices[2];

        float startX = Math.min(Math.min(a.X, b.X), c.X);
        float endX = Math.max(Math.max(a.X, b.X), c.X);
        float startY = Math.min(Math.min(a.Y, b.Y), c.Y);
        float endY = Math.max(Math.max(a.Y, b.Y), c.Y);
        startX = Math.max(0, startX);
        endX = Math.min(endX, pixelBuffer.width);
        startY = Math.max(0, startY);
        endY = Math.min(endY, pixelBuffer.height);

        float areaABC = edg(a, b, c);

        Vector3i rgbColor = new Vector3i(255, 255, 255);
        Vector3f bc;

        for (int y = (int) startY; y < endY; y++) {
            for (int x = (int) startX; x < endX; x++) {
                Vector3f p = new Vector3f(x, y, 0);

                float areaABP = edg(a, b, p);
                float areaBCP = edg(b, c, p);
                float areaCAP = edg(c, a, p);

                if (areaABP > 0 & areaBCP > 0 & areaCAP > 0) {

                    float i = areaBCP / areaABC, j = areaCAP / areaABC, k = areaABP / areaABC;
                    //透视矫正
                    i = i * p.Z / a.Z;
                    j = j * p.Z / b.Z;
                    k = k * p.Z / c.Z;
                    //线性插值计算顶点的坐标的Z
                    float z = a.Z + j * b.Z + k * c.Z;
                    //ZBuffer测试
                    if (zBuffer.get(x, y) < z && z <= 1.0) {
                        zBuffer.set(x, y, z);
                        //执行片元着色器
                        rgbColor = shader.fragment(new Vector3f(i, j, k));
                        pixelBuffer.set(x, y, rgbColor);
                    }
                }
            }
        }
    }

    void viewpointTransform(Buffer<Vector3i> pixelBuffer, Vector3f[] vertices) {
        for(int i = 0; i < 3; ++i){
            //Adding half a pixel to avoid gaps on small vertex values
            vertices[i].X = (float) (((vertices[i].X + 1 ) * pixelBuffer.width * 0.5)  + 0.5);
            vertices[i].Y = (float) (((vertices[i].Y + 1 ) * pixelBuffer.height * 0.5) + 0.5);
        }
    }
}
