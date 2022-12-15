import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/13 9:50
 **/
public class Render {

    //RGB Texture
    Texture<Vector3i> texture;

    int rasterWidth;

    int rasterHeight;

    //顶点数据
    List<Vertx> vertxContext = new ArrayList<>();

    //图元信息
    List<PrimitiveInfo> primitiveInfos = new ArrayList<>();

    FrameBuffer<Vector3i> frameBuffer;

    Matrix4x4f modelMat;

    Matrix4x4f viewMat;

    Matrix4x4f projMat;

     Matrix4x4f rasterMat;


    public Render(Texture<Vector3i> texture, int rasterWidth, int rasterHeight, Vector3f position, Vector3f scale, Vector3f rotation, float fov, float nearPlane, float farPlane) {
        this.texture = texture;
        this.rasterWidth = rasterWidth;
        this.rasterHeight = rasterHeight;
        this.frameBuffer = new FrameBuffer<>(rasterWidth, rasterHeight, new Vector3i(0, 0, 0));
        modelMat = Matrix4x4f.translation(position)
                .multiply(Matrix4x4f.scale(scale))
                .multiply(Matrix4x4f.rotationX(rotation.X))
                .multiply(Matrix4x4f.rotationY(rotation.Y))
                .multiply(Matrix4x4f.rotationZ(rotation.Z));


        viewMat = Matrix4x4f.IDENTITY;
        projMat = Matrix4x4f.projection((float) rasterWidth / rasterHeight, fov, nearPlane, farPlane);
        rasterMat = new Matrix4x4f(
                (float) rasterWidth / 2, 0, 0, (float) rasterWidth / 2,
                0, (float) rasterHeight / 2, 0, (float) rasterHeight / 2,
                0, 0, 1, 0,
                0, 0, 0, 1);

    }

    void add(Vertx... ves) {
        vertxContext.addAll(Arrays.stream(ves).collect(Collectors.toList()));
    }

    void add(PrimitiveInfo... ps) {
        primitiveInfos.addAll(Arrays.stream(ps).collect(Collectors.toList()));
    }

    static class Vertx {
        Vector4f coordinate;
        Vector2f UV;
        Vector3i colorRGB;
        Vector3f norm;

        public Vertx(Vector4f coordinate, Vector2f UV) {
            this.coordinate = coordinate;
            this.UV = UV;
            this.colorRGB = new Vector3i(255, 255, 255);
        }
    }

    //顶点着色， 进行顶点的坐标变换
    void vertxShading(Vertx vertx) {
        //顶点坐标执行一系列空间变换 模型空间 -> 世界空间 -> 视图空间
        vertx.coordinate = modelMat.multiply(vertx.coordinate);
        vertx.coordinate = viewMat.multiply(vertx.coordinate);
        //投影变换: 视图空间 -> 投影裁剪空间
        vertx.coordinate = projMat.multiply(vertx.coordinate);
    }

    //曲面细分
    void tessellationShading() {
    }

    //几何着色
    void geometryShading(PrimitiveInfo primitiveInfo) {
    }

    void perspective(Vector4f vector4f) {
        vector4f.X /= vector4f.W;
        vector4f.Y /= vector4f.W;
        vector4f.Z /= vector4f.W;
    }

    //裁剪
    boolean clipping(Vector4f vector4f) {
        return vector4f.Z > 1 || vector4f.Z < -1 || vector4f.X > 1 || vector4f.X < -1 || vector4f.Y > 1 || vector4f.Y < -1;
    }

    //屏幕映射,
    void screenMapping(Vector4f coordinate) {
        Vector4f tmp = rasterMat.multiply(coordinate);
        coordinate.X = tmp.X;
        coordinate.Y = tmp.Y;
        coordinate.Z = tmp.Z;
        coordinate.W = tmp.W;
    }

    enum PrimitiveType {
        POINT,
        LINE,
        TRIANGLE,
    }

    //图元
    class Primitive {
        Vertx[] vertxes;
        PrimitiveType primitiveType;

        public Primitive(Vertx[] vertxes, PrimitiveType primitiveType) {
            this.vertxes = vertxes;
            this.primitiveType = primitiveType;
        }
    }

    static class PrimitiveInfo {
        int[] vertxIndexes;
        PrimitiveType primitiveType;

        public PrimitiveInfo(int[] vertxIndexes, PrimitiveType primitiveType) {
            this.vertxIndexes = vertxIndexes;
            this.primitiveType = primitiveType;
        }
    }

    //图元组装
    List<Primitive> primitiveAssembly() {
        return primitiveInfos.stream()
                .map(primitiveInfo -> {
                    Vertx[] vs = Arrays.stream(primitiveInfo.vertxIndexes)
                            .mapToObj(index -> vertxContext.get(index))
                            .toArray(Vertx[]::new);
                    return new Primitive(vs, primitiveInfo.primitiveType);
                })
                .collect(Collectors.toList());
    }

    // 向量ac, 向量ab的平行四边形的有向面积
    private float area(Vector2f a, Vector2f b, Vector2f c) {
        return (c.X - a.X) * (b.Y - a.Y) - (b.X - a.X) * (c.Y - a.Y);
    }

    class Fragment {
        Vector2i coord;
        Vector3i color;
        float depth;
        Vector2f uv;

        public Fragment(Vector2i coord) {
            this.coord = coord;
        }
    }

    //图元遍历, 采样得到片元序列
    List<Fragment> primitiveTraversal(Primitive primitive) {
        List<Fragment> fragments = new ArrayList<>();
        //光栅化三角形
        switch (primitive.primitiveType){
            case TRIANGLE : {
                Vertx a = primitive.vertxes[0], b = primitive.vertxes[1], c = primitive.vertxes[2];
                float startX = Math.min(Math.min(a.coordinate.X, b.coordinate.X), Math.min(c.coordinate.X, rasterWidth));
                float endX = Math.max(Math.max(a.coordinate.X, b.coordinate.X), Math.max(c.coordinate.X, 0));
                float startY = Math.min(Math.min(a.coordinate.Y, b.coordinate.Y), Math.min(c.coordinate.Y, rasterHeight));
                float endY = Math.max(Math.max(a.coordinate.Y, b.coordinate.Y), Math.max(c.coordinate.Y, 0));

                Vector2f a_ = new Vector2f(a.coordinate.X, a.coordinate.Y);
                Vector2f b_ = new Vector2f(b.coordinate.X, b.coordinate.Y);
                Vector2f c_ = new Vector2f(c.coordinate.X, c.coordinate.Y);
                float areaABC = area(a_, b_, c_);
                for (int y = (int) Math.max(0, startY); y < Math.min(rasterHeight, endY); y++) {
                    for (int x = (int) Math.max(0, startX); x < Math.min(rasterWidth, endX); x++) {
                        Vector2f p = new Vector2f(x, y);
                        float areaABP = area(a_, b_, p);
                        float areaBCP = area(b_, c_, p);
                        float areaCAP = area(c_, a_, p);

                        if (areaABP > 0 & areaBCP > 0 & areaCAP > 0) {
                            //TODO 此处计算的重心坐标不对，因为使用的是经过透视投影后的非线性的屏幕坐标，需要进行矫正
                            float i = areaBCP / areaABC, j = areaCAP / areaABC, k = areaABP / areaABC;
                            //线性插值计算顶点的坐标的Z
                            float z = i * a.coordinate.Z + j * b.coordinate.Z + k * c.coordinate.Z;
                            //线性插值计算顶点的UV坐标
                            float u = i * a.UV.X + j * b.UV.X + k * c.UV.X;
                            float v = i * a.UV.Y + j * b.UV.Y + k * c.UV.Y;

                            Fragment fragment = new Fragment(new Vector2i(x, y));
                            fragment.uv = new Vector2f(u, v);
                            fragment.depth = z;
                            fragments.add(fragment);
                        }
                    }
                }
                break;
            }
            case POINT : {}
            case LINE : {}
            default: {}
        }
        return fragments;
    }

    //片元着色
    void fragmentShading(Fragment fragment) {
        //纹理采样
        float x = fragment.uv.X * texture.width;
        float y = fragment.uv.Y * texture.height;
        fragment.color = texture.pixels[(int) Math.ceil(x) - 1][(int) Math.ceil(y) - 1];
        //TODO 使用光照、法线计算颜色
    }

    //逐片元操作
    void preFragmentOptions(Fragment fragment) {
        //TODO Alpha测试, 深度测试, Alpha混合, 模板测试 ....
        //设置帧缓冲区像素
        frameBuffer.set(fragment.coord.X + frameBuffer.width * fragment.coord.Y, fragment.color);
    }

    void print() {
        int ansiColorCode = -1;
        int lastColorRgb8 = -1;
        for (int y = 0; y < rasterHeight; y++) {
            for (int x = 0; x < rasterWidth; x++) {
                Vector3i value = frameBuffer.get(x + (y * rasterWidth));
                int r = Math.min((int) ((value.X / 256.0f) * 6), 5) * 36;
                int g = Math.min((int) ((value.Y / 256.0f) * 6), 5) * 6;
                int b = Math.min((int) ((value.Z / 256.0f) * 6), 5);
                //if (0 <= value.X && value.Z <= 5)
                ansiColorCode = 16 + r + g + b;
                if (ansiColorCode != lastColorRgb8)
                {
                    System.out.print("\u001b[38;5;" + ansiColorCode + "m");
                    lastColorRgb8 = ansiColorCode;
                }
                System.out.print('\u2588');
            }
            System.out.print("\n"); // Mo
        }
    }

    void render() {
        frameBuffer.clear();
        //几何阶段
        for (PrimitiveInfo primitiveInfo : primitiveInfos) {
            for (int i = 0; i < primitiveInfo.vertxIndexes.length; i++) {
                Vertx vertx = vertxContext.get(primitiveInfo.vertxIndexes[i]);
                vertxShading(vertx);
                perspective(vertx.coordinate);
                if(clipping(vertx.coordinate)) continue;
                screenMapping(vertx.coordinate);
            }
        }
        //光栅化阶段
        List<Primitive> primitives = primitiveAssembly();
        List<Fragment> fragments = new ArrayList<>();
        for (Primitive primitive : primitives) {
            fragments.addAll(primitiveTraversal(primitive));
        }
        for (Fragment fragment : fragments) {
            fragmentShading(fragment);
            preFragmentOptions(fragment);
        }
    }

    public static void setConsoleCursorPosition(int x, int y)
    {
        System.out.print("\u001b[" + (y + 1) + ";" + (x + 1) + "H");
    }

    public static boolean IsWindows = System.getProperty("os.name").startsWith("Windows");
    public static void main(String[] args) throws Exception {

        if (IsWindows)
        {
            // Non-canonical mode is a console mode that requires Win32 API calls. Pure Java doesn't support native calls like this. But C# does. And with the help of Powershell, C# code can actually be executed inline.
            // These flags are the ones that need to be disabled.
            int consoleModeFlags = WindowsInterop.KERNEL32_ENABLE_LINE_INPUT |
                    WindowsInterop.KERNEL32_ENABLE_PROCESSED_INPUT |
                    WindowsInterop.KERNEL32_ENABLE_ECHO_INPUT;
            // Run Powershell with a predefined script that can change the terminal to non-canonical mode.
            WindowsInterop.runPowershellScript(WindowsInterop.getStdinModeChangePowershellScript(consoleModeFlags, false));
        }

        // Get the terminal buffer size.
        Vector2i terminalSize = new Vector2i(100, 100);
        if (IsWindows)
        {
            // This is the worst way of retrieving the data, but that's just how Windows works.
            // There's no way of retrieving the terminal size without calling native Windows API's, which would require adding dependencies like JNI to this project, which is exactly what this project tries to avoid.
            // The terminal size will be retrieved by Powershell instead. This time, no native API calling is required.
            String[] sizeString = WindowsInterop.runPowershellScript("$Host.Ui.RawUi.WindowSize.ToString()").split(",");

            int width = Integer.parseInt(sizeString[0]);
            int height = Integer.parseInt(sizeString[1]);

            terminalSize = new Vector2i(width, height);
        }
        System.out.printf("Resolution is %s×%s%n", terminalSize.X, terminalSize.Y);

        Texture<Vector3i> defaultTexture = new Texture<>(new Vector3i[]
                {
                        new Vector3i(240, 0, 16),   //red
                        new Vector3i(0, 216, 32),   //green
                        new Vector3i(26, 0, 240),   //blue
                        new Vector3i(255, 255, 0),  //yellow
                }, 2);

        Vector3f[] vertices = new Vector3f[]
                {
                        // Z+
                        new Vector3f(-0.5f,  0.5f, 0.5f),
                        new Vector3f( 0.5f,  0.5f, 0.5f),
                        new Vector3f( 0.5f, -0.5f, 0.5f),
                        new Vector3f(-0.5f, -0.5f, 0.5f),

                        // Z-
                        new Vector3f(-0.5f,  0.5f, -0.5f),
                        new Vector3f( 0.5f,  0.5f, -0.5f),
                        new Vector3f( 0.5f, -0.5f, -0.5f),
                        new Vector3f(-0.5f, -0.5f, -0.5f),

                        // Y+
                        new Vector3f( 0.5f,  0.5f, -0.5f),
                        new Vector3f( 0.5f,  0.5f,  0.5f),
                        new Vector3f(-0.5f,  0.5f,  0.5f),
                        new Vector3f(-0.5f,  0.5f, -0.5f),

                        // Y-
                        new Vector3f( 0.5f, -0.5f, -0.5f),
                        new Vector3f( 0.5f, -0.5f,  0.5f),
                        new Vector3f(-0.5f, -0.5f,  0.5f),
                        new Vector3f(-0.5f, -0.5f, -0.5f),

                        // X+
                        new Vector3f( 0.5f, -0.5f,  0.5f),
                        new Vector3f( 0.5f,  0.5f,  0.5f),
                        new Vector3f( 0.5f,  0.5f, -0.5f),
                        new Vector3f( 0.5f, -0.5f, -0.5f),

                        // X-
                        new Vector3f(-0.5f, -0.5f,  0.5f),
                        new Vector3f(-0.5f,  0.5f,  0.5f),
                        new Vector3f(-0.5f,  0.5f, -0.5f),
                        new Vector3f(-0.5f, -0.5f, -0.5f),
                };
        Vector2f[] textureCoordinates = new Vector2f[]
                {
                        // Z+
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(0.0f, 0.0f),

                        // Z-
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(0.0f, 0.0f),

                        // Y+
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(0.0f, 0.0f),

                        // Y-
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(0.0f, 0.0f),

                        // X+
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(0.0f, 0.0f),

                        // X-
                        new Vector2f(0.0f, 1.0f),
                        new Vector2f(1.0f, 1.0f),
                        new Vector2f(1.0f, 0.0f),
                        new Vector2f(0.0f, 0.0f),
                };
        int[][] faces = new int[][]
                {
                        {0, 3, 1},
                        {1, 3, 2},
                        {4, 5, 7},

                        {5, 6, 7},
                        {8, 11, 9}, //   Y+ 0, 3, 1,
                        {9, 11, 10}, //     1, 3, 2,

                        {12, 13, 15}, // Y- 0, 1, 3,
                        {13, 14, 15}, //    1, 2, 3,
                        {16, 19, 17}, // X+ 0, 3, 1,

                        {17, 19, 18}, //    1, 3, 2,
                        {20, 21, 23}, // X- 0, 1, 3,
                        {21, 22, 23}  //    1, 2, 3,
                };
        Render render = new Render(defaultTexture, terminalSize.X, terminalSize.Y,
                new Vector3f(0, 0, 4), new Vector3f(1, 1, 1), new Vector3f(0, 45, 0),
                30, 0.1f, 10.0f);
        for (int i = 0; i < vertices.length; i++) {
            render.add(new Vertx(new Vector4f(vertices[i].X, vertices[i].Y, vertices[i].Z, 1.0f), textureCoordinates[i]));
        }
        for (int[] face : faces) {
            render.add(new PrimitiveInfo(face, PrimitiveType.TRIANGLE));
        }

        boolean mainLoop = true;
        render.render();
        while (mainLoop) {
            // Set cursor position to x=0, y=0.
            setConsoleCursorPosition(0, 0);
            render.print();
        }
        setConsoleCursorPosition(0, 0);
    }
}
