package my.render;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:50
 **/
public class Face {
    int[] vertexIndices;
    int[] uvIndices;
    int[] normalsIndices;

    public Face(int[] vertexIndices, int[] uvIndices, int[] normalsIndices) {
        this.vertexIndices = vertexIndices;
        this.uvIndices = uvIndices;
        this.normalsIndices = normalsIndices;
    }
}
