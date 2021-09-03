package Struture;

import math.Vector3;

/**
 * TODO 类描述
 *
 * @author Liuzhenbin
 * @date 2021-04-24 17:04
 **/
public class RGBPixel implements IPixel {

    private Vector3 pixelData;

    public float r() {
        return pixelVector().x();
    }

    public float g() {
        return pixelVector().y();
    }

    public float b() {
        return pixelVector().z();
    }

    @Override
    public Vector3 pixelVector() {
        return pixelData;
    }
}
