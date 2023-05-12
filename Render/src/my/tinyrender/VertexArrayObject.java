package my.tinyrender;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Liuzhenbin
 * @date 2023/4/4 17:18
 **/
public class VertexArrayObject {

    private static final Map<Type[], Convert<?, ?>> converts = new HashMap<>();

    private <F,T>  Optional<? extends Convert<F,T>> find(Class<F> formType, Class<T> toType) {
        return converts.entrySet().stream()
                .filter(entry -> entry.getKey()[0].equals(formType) && entry.getKey()[1].equals(toType))
                .map(entry -> (Convert<F, T>) entry.getValue())
                .findFirst();
    }

    private static <F, T> void put(Convert<F, T> convert) {
        ParameterizedType parameterizedType = (ParameterizedType) (convert.getClass().getGenericInterfaces()[0]);
        converts.put(parameterizedType.getActualTypeArguments(), convert);
    }

    static {
        put(new FloatArrayToVector3f());
        put(new FloatArrayToVector2f());
    }

    VertexAttributePointer[] vertexAttributePointers;
    ByteBuffer vertex_buffer;

    public void free() {
        vertex_buffer = null;
    }

    public <T> T getVertexAttribute(int index, int pos, Class<T> javaType) {
        VertexAttributePointer pointer = vertexAttributePointers[pos];
        vertex_buffer.position(pointer.offset + index * pointer.stride);
        switch (pointer.type) {
            case SHORT:
                short[] res = new short[pointer.size];
                for (int i = 0; i < pointer.size; i++) {
                    res[i] = vertex_buffer.getShort();
                }
                Convert<short[], T> convert = find(short[].class, javaType).orElseThrow(() -> new RuntimeException("can not convert"));
                return convert.convert(res);
            case FLOAT:
                float[] floats = new float[pointer.size];
                for (int i = 0; i < pointer.size; i++) {
                    floats[i] = vertex_buffer.getFloat();
                }
                Convert<float[], T> convert2 = find(float[].class, javaType).orElseThrow(() -> new RuntimeException("can not convert"));
                return convert2.convert(floats);
            default:
                return null;
        }
    }
}
