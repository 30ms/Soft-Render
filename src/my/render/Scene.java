package my.render;

import java.util.List;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/16 15:59
 **/
public class Scene {
    private Camera mainCamera;
    private Vector3f lightPos;
    private Vector3f lightColor;
    private List<Model> modelsInScene;

    public Scene(Camera mainCamera, Vector3f lightPos, Vector3f lightColor, List<Model> modelsInScene) {
        this.mainCamera = mainCamera;
        this.lightPos= lightPos;
        this.lightColor = lightColor;
        this.modelsInScene = modelsInScene;
    }

    public void update(long deltaTime) {
        mainCamera.update(deltaTime);
        modelsInScene.forEach(model -> model.update(deltaTime));
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    public List<Model> getModelsInScene() {
        return modelsInScene;
    }

    public Vector3f getLightPos() {
        return lightPos;
    }

    public Vector3f getLightColor() {
        return lightColor;
    }
}
