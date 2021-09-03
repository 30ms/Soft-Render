package Struture.Buffer;

import Struture.IPixel;

/**
 * TODO 类描述
 *
 * @author Liuzhenbin
 * @date 2021-04-24 17:11
 **/
public interface IRenderBuffer<T> extends IBuffer<T> {
    int width();

    int height();

    Class<? extends IPixel> pixelType();

    <P extends IPixel> void init(int width, int height, Class<P> pixelType);

    void clear();
}
