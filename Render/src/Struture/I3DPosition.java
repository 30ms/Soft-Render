package Struture;

import math.Vector;
import math.Vector3;

/**
 * TODO 类描述
 *
 * @author Liuzhenbin
 * @date 2021-04-24 16:38
 **/
public interface I3DPosition extends IPosition {

    @Override
    Vector3 positionVector();

    default float x() {
        return positionVector().x();
    }

    default float y() {
        return positionVector().y();
    }

    default float z() {
        return positionVector().z();
    }
}
