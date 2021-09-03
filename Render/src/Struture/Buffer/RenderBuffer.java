package Struture.Buffer;

import Struture.IPixel;
import Struture.RGBAPixel;
import Struture.RGBPixel;

import java.lang.reflect.Array;

/**
 * TODO 类描述
 *
 * @author Liuzhenbin
 * @date 2021-04-23 17:42
 **/
public class RenderBuffer implements IRenderBuffer<IPixel> {
    private int width;
    private int height;
    private IPixel[] pixels;
    private Class<? extends IPixel> pixelType;

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public Class<? extends IPixel> pixelType() {
        return pixelType;
    }

    @Override
    public <P extends IPixel> void init(int width, int height, Class<P> pixelType) {
        this.width = width;
        this.height = height;
        this.pixelType = pixelType;
        this.pixels = (P[])Array.newInstance(pixelType, width * height);
    }

    @Override
    public IPixel[] getBufferDataArray() {
        return pixels;
    }

    @Override
    public void clear() {
    }

    public RenderBuffer(int width, int height, Class<? extends IPixel> pixelClazz) {
        init(width, height,pixelClazz);
    }

    public static RenderBuffer createRGBBuffer(int width, int height) {
        return new RenderBuffer(width, height, RGBPixel.class);
    }

    public static RenderBuffer createRGBABuffer(int width, int height) {
        return new RenderBuffer(width, height, RGBAPixel.class);
    }
}
