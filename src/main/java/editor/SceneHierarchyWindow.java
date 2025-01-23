package editor;
import engine.GameObject;
import engine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

//This class organizes these objects visually in a tree structure, where some objects can be "parents" and others can be "children."
// This lets developers quickly see and manage how game objects are related.

// A tree node is a part of a hierarchical structure, similar to a family tree or a file explorer.
// It represents one item in the hierarchy, which may: be parent , be child or stand alone

public class SceneHierarchyWindow {

    public SceneHierarchyWindow(){
    }

    public void imgui(){
        ImGui.begin("Scene Heirarchy");
        // adds all the gameobject in the scene into a list
        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index =0;
        for(GameObject obj: gameObjects){
            if (!obj.doSerialization()){
                continue;
            }

            //creates a tree node in imgui scene Hierarchy window
            boolean treeNodeOpen = doTreeNode(obj, index);

            if (treeNodeOpen){
                ImGui.treePop(); // this means you are done modifying this node
            }
            index++;
        }

        ImGui.end();


    }

    public boolean doTreeNode(GameObject obj, int index){
        // Pushes a unique ID (index) onto ImGui's stack for this object. This ensures ImGui elements
        // don't conflict, even if multiple objects have the same name.
        // These flags help make the tree node look good and behave in a way that’s intuitive for the user:
        ImGui.pushID(index);
        // These flags help make the tree node look good and behave in a way that’s intuitive for the user:
        boolean treeNodeOpen = ImGui.treeNodeEx(
                obj.name,
                ImGuiTreeNodeFlags.DefaultOpen | // This flag makes the node open by default when the window first shows up.
                        ImGuiTreeNodeFlags.FramePadding | // It adds a little bit of space (padding) around the text inside the node.
                        ImGuiTreeNodeFlags.OpenOnArrow  | // It makes the node open or close only when you click the arrow next to it.
                        ImGuiTreeNodeFlags.SpanAvailWidth, // It makes the node stretch across the entire width of the window.
                obj.name
        );
        ImGui.popID();

        if (ImGui.beginDragDropSource()){
            ImGui.setDragDropPayloadObject("SceneHierarchy", obj);
            ImGui.text(obj.name);
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()){
            Object payloadObj = ImGui.acceptDragDropPayloadObject("SceneHierarchy");
            if (payloadObj != null){
                if (payloadObj.getClass().isAssignableFrom(GameObject.class)){
                    GameObject playerGameObj = (GameObject)payloadObj;
                    System.out.println("Payload accepted " + playerGameObj.name );
                }
            }

            ImGui.endDragDropTarget();
        }

        return treeNodeOpen;
    }



}
