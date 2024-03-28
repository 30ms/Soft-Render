package my.render;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:50
 **/
public class Face {
    List<Integer> vertexIndices;
    List<Integer> uvIndices;
    List<Integer> normalsIndices;

    public Face() {}

    public Face(int[] vertexIndices, int[] uvIndices, int[] normalsIndices) {
        this.vertexIndices = Arrays.stream(vertexIndices).boxed().collect(Collectors.toList());
        this.uvIndices = Arrays.stream(uvIndices).boxed().collect(Collectors.toList());
        this.normalsIndices = Arrays.stream(normalsIndices).boxed().collect(Collectors.toList());
    }
}
