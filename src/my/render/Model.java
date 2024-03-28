package my.render;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/15 19:34
 **/
public class Model {
    /*
     位置
     */
    public Vector3f position = new Vector3f(0,0,0);
    /*
     缩放
     */
    public Vector3f scale = new Vector3f(1, 1, 1);
    /*
     旋转
     */
    public Vector3f rotation = new Vector3f(0, 0, 0);
    public List<Mesh> meshes;
    public Model() {}
    public BiConsumer<Model,SoftwareRender> setupCallback = (model, render)->{};

    public Model(Vector3f position, Vector3f scale, Vector3f rotation, Mesh mesh){
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        this.meshes = Collections.singletonList(mesh);
    }

    public BiConsumer<Model,Long> updateCallback = (model,deltaTime)->{};

    public Matrix4x4f getModelMatrix(){
      return   Matrix4x4f.translation(position)
                .multiply(Matrix4x4f.scale(scale))
                .multiply(Matrix4x4f.rotationX(rotation.X))
                .multiply(Matrix4x4f.rotationY(rotation.Y))
                .multiply(Matrix4x4f.rotationZ(rotation.Z));
    }
}
