package editor;

import components.SpriteRenderer;
import engine.GameObject;
import imgui.ImGui;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.RigidBody2D;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

// This class displays the properties of the gameObject when clicked on them

public class PropertiesWindow {
    private List<GameObject> activeGameObjects;
    private List<Vector4f> activeGameObjectOgColor;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    // Constructor
    // initializes the essential components in the class
    public PropertiesWindow(PickingTexture pickingTexture){
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectOgColor = new ArrayList<>();
    }

    // UI part of the activeGameObject
    public void imgui(){
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            // this won't be visible unless you right click on it
            if (ImGui.beginPopupContextWindow("ComponentAdder")){
                if (ImGui.menuItem("Add RigidBody")){
                    if (activeGameObject.getComponent(RigidBody2D.class)== null){
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")){
                    if (activeGameObject.getComponent(Box2DCollider.class)== null &&
                            activeGameObject.getComponent(CircleCollider.class)== null){
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")){
                    if (activeGameObject.getComponent(CircleCollider.class)== null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null ){
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }
            // This function will call the function of active object which will call the imgui function of the component
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    // returns the active gameObject if it there is only one active GameObject
    public GameObject getActiveGameObject(){

        return activeGameObjects.size() == 1? this.activeGameObjects.get(0): null;
    }

    // returns the list of all active gameObject
    public List<GameObject> getActiveGameObjects(){
        return this.activeGameObjects;
    }


    // This method clears the currently selected game objects and resets their colors.
    // when a object is selected but not added in the viewport it's color is not the original color of the sprite
    // it has blending in it so when clear selected used it resets it's color
    public void clearSelected(){
        if (activeGameObjectOgColor.size()>0){
            int i =0;
            for (GameObject go: activeGameObjects){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null){
                    spr.setColor(activeGameObjectOgColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeGameObjectOgColor.clear();
    }

    // This method sets a single game object as the active (selected) one.
    public void setActiveGameObject(GameObject go) {
        if (go != null){
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }

    // This method adds a game object to the list of selected objects and modifies its appearance.
    public void addActiveGameObject(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null){
            this.activeGameObjectOgColor.add(new Vector4f(spr.getColor()));
            spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeGameObjectOgColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }

    // returns the pickingTexture used 
    public PickingTexture getPickingTexture(){
        return this.pickingTexture;
    }
}
