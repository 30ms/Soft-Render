package my.render;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Liuzhenbin
 * @date 2022/12/16 16:05
 **/
public class SceneManager {

    private Map<String, Scene> sceneMap = new HashMap<>();
    private Scene currentScene;

    public boolean switchScene(String sceneId) {
        Scene scene = sceneMap.get(sceneId);
        if (scene != null) {
            currentScene = scene;
            return true;
        }
        return false;
    }

    public void updateScene(long deltaTime) {
        currentScene.update(deltaTime);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public boolean addScene(String sceneId, Scene scene) {
        return sceneMap.putIfAbsent(sceneId, scene) == null;
    }
}
