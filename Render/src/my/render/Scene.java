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
    private List<Model> modelsInScene;
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

}
