package Struture;

import math.Vector;

/**
 * TODO 类描述
 *
 * @author Liuzhenbin
 * @date 2021-04-24 16:40
 **/
public interface IPixel {

    Vector pixelVector();

    default int channelCount() {
        return pixelVector().length();
    }
}
