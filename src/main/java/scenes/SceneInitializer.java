package scenes;

// This is the abstract class that acts as a blueprint for specific scene types (like LevelEditorSceneInitializer and levelSceneInitializer)

public abstract class SceneInitializer {
    public abstract void init(Scene scene);
    public abstract void loadResources(Scene scene);
    public abstract void imgui();
}
