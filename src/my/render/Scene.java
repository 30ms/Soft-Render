package my.render;

import java.util.List;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/16 15:59
 **/
public class Scene {
    private List<Model> modelsInScene;

    public Scene(List<Model> modelsInScene) {
        this.modelsInScene = modelsInScene;
    }

    public void update(long deltaTime) {
        modelsInScene.forEach(model -> model.update(deltaTime));
    }

    public List<Model> getModelsInScene() {
        return modelsInScene;
    }
}
