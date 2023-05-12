package my.tinyrender;

/**
 *         | Vertex 1              | Vertex 2             | Vertex 3              |
 *         | X | Y | Z | R | G | B | X | Y | Z | R | G |B | X | Y | Z | R | G | B |
 * byte:   0          12          24          36
 *                     |           |           |
 * position:----stride:24---------->           |
 *         -offset:0   |                       |
 *                     |                       |
 * color:              --------stride:24------->
 *         -offset:12-->
 *
 *
 * @author Liuzhenbin
 * @date 2023/4/6 9:48
 **/
public class VertexAttributePointer {

    //顶点属性的元组类型
    GL_TYPE type;

    //顶点属性的元组个数
    int size;

    //顶点属性的步长，即下个属性的字节间隔
    int stride;

    //顶点属性组内的间隔
    int offset;

}
