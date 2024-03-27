package my.render;

import java.util.function.Consumer;

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
    public Vector3f position;
    /*
     缩放
     */
    public Vector3f scale;
    /*
     旋转
     */
    public Vector3f rotation;
    public Mesh mesh;
    public AbstractShader shader;
    public Consumer<AbstractShader> setupShader = shader -> {};

    public Model(Vector3f position, Vector3f scale, Vector3f rotation, Mesh mesh, AbstractShader shader){
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        this.mesh = mesh;
        this.shader = shader;
    }

    public void update(long deltaTime) {
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Matrix4x4f getModelMatrix(){
      return   Matrix4x4f.translation(position)
                .multiply(Matrix4x4f.scale(scale))
                .multiply(Matrix4x4f.rotationX(rotation.X))
                .multiply(Matrix4x4f.rotationY(rotation.Y))
                .multiply(Matrix4x4f.rotationZ(rotation.Z));
    }
}
