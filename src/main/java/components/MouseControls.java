package components;

// this class is used when picking objects from spritesheet imgui window it helps to pick and place

import editor.PropertiesWindow;
import engine.GameObject;
import engine.KeyListener;
import engine.MouseListener;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import renderer.DebugDraw;
import renderer.PickingTexture;
import scenes.Scene;
import util.Settings;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    GameObject holdingObject = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    public void pickupObject(GameObject go) {
        if (this.holdingObject != null){
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        this.holdingObject.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        GameObject newObj = this.holdingObject.copy();
        if (newObj.getComponent(StateMachine.class) != null){
            newObj.getComponent(StateMachine.class).refreshTextures();
        }
        newObj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
        newObj.removeComponent(NonPickable.class);
        Window.getScene().addGameObjectToScene(newObj);
    }

    @Override
    public void editorUpdate(float dt) {
        // deboune is used to slightly delay the mouse click so that it won't respond too quickly
        debounce -= dt;
        PickingTexture pickingTexture = Window.getImguiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene();

        if (holdingObject != null) {
            // updates the position of the holding object when moved in the world coords
            holdingObject.transform.position.x = MouseListener.getWorldX();
            holdingObject.transform.position.y = MouseListener.getWorldY();

            // automatically fits into the grid vertices
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH /2;
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT /2;

            // if left button clicked leave the block at the current position.
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth = Settings.GRID_WIDTH/2.0f;
                float halfHeight = Settings.GRID_HEIGHT/ 2.0f;
                if (MouseListener.isDragging() &&
                    !blockInSquare(holdingObject.transform.position.x - halfWidth,
                            holdingObject.transform.position.y - halfHeight)) {
                    place();
                } else if (!MouseListener.isDragging() && debounce < 0){
                    place();
                    debounce = debounceTime;
                }
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                holdingObject.destroy();
                holdingObject = null;
            }
        } else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce<0){
            // Retrieves the size of window and convert it into x and y
            int[] size = Window.get().getWindowSize();
            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            // detects the clicked gameobject and assign it to pickedObj
            int gameObjectId =  pickingTexture.readPixel(x,y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class)== null){
                Window.getImguiLayer().getPropertiesWindow().setActiveGameObject(pickedObj);
                //If no object is picked (pickedObj == null) and the mouse isn’t dragging
            } else if (pickedObj == null && !MouseListener.isDragging()){
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
            }

            this.debounce = 0.2f;
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            if (!boxSelectSet){
                Window.getImguiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize =
                    (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D(new Vector2f(boxSelectStartWorld).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f), 0.0f);
        } else if (boxSelectSet){
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX){
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY< screenStartY){
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId: gameObjectIds){
                uniqueGameObjectIds.add((int)objId);
            }

            for(Integer gameObjectId : uniqueGameObjectIds){
                GameObject pickedObj = Window.getScene().getGameObject(gameObjectId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null){
                    Window.getImguiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }

    private boolean blockInSquare(float x, float y){
        PropertiesWindow propertiesWindow = Window.getImguiLayer().getPropertiesWindow();
        Vector2f start= new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x -2, (int)endScreenf.y-2);
        float[] gameObjectIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i =0; i< gameObjectIds.length; i++){
            if (gameObjectIds[i]>=0){
                GameObject pickedObj = Window.getScene().getGameObject((int)gameObjectIds[i]);
                if (pickedObj.getComponent(NonPickable.class) == null){
                    return true;
                }
            }
        }
        return false;
    }
}