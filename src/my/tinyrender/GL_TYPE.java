package my.tinyrender;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2023/4/6 9:51
 **/
public enum GL_TYPE {
    SHORT(Short.BYTES),
    FLOAT(Float.BYTES);
    final int byteSize;

    GL_TYPE(int byteSize) {
        this.byteSize = byteSize;
    }
}
