package Struture;

import math.Vector;
import math.Vector4;

/**
 * TODO 类描述
 *
 * @author Liuzhenbin
 * @date 2021-04-24 16:43
 **/
public class RGBAPixel implements IPixel {

    private Vector4 pixelData;

    public float r() {
        return pixelVector().x();
    }

    public float g() {
        return pixelVector().y();
    }

    public float b() {
        return pixelVector().z();
    }

    public float a() {
        return pixelVector().w();
    }

    @Override
    public Vector4 pixelVector() {
        return pixelData;
    }
}
