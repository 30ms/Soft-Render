package Struture.Buffer;

/**
 * 渲染缓冲
 *
 * @author Liuzhenbin
 * @date 2021-04-21 11:34
 **/
public interface IRenderBuffer<T> {
    int width();

    int height();

    T[] GetRenderBuffer();
}
