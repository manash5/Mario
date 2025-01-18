package components;

import engine.Camera;
import engine.KeyListener;
import engine.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

// This class is for controlling camera editor. It allows you to Drag the camera around the level, Zoom in and out using the
// mouse scroll wheel and reset the camera to its starting position and correct zoom level using a specific key
// Lerp makes the movement from one direction to another as smoothly as possile. Instead of moving from one poin to another directly
// it shows transition of movement in a certain amount of time


public class EditorCamera extends Component{

    private float dragDebounce = 0.032f;

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;
    private boolean reset = false;
    private float lerpTime = 0.0f;

    public EditorCamera(Camera levelEditorCamera){
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt){
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT) && dragDebounce> 0){
            // when the right button is clicked and there is delay at the start,
            // we get the mouse world coords and store it in clickOrigin
            this.clickOrigin = MouseListener.getWorld();
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)){
            // gets the current position of the mouse
            Vector2f mousePos = MouseListener.getWorld();
            // gets the difference between the current and origin position of the mouse
            Vector2f delta = new Vector2f(mousePos.x, mousePos.y).sub(this.clickOrigin);
            // Slowy(dt) moves the movement by the opposite of the drag distance (sub) based on your mouse sense(dragSensitivity)
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            // Smoothly updates the click origin towards the mouse
            this.clickOrigin.lerp(mousePos, dt);
        }

        // if Dragging has stopped, reset the debounce timer
        if (dragDebounce <=0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            dragDebounce = 0.1f;
        }

        // check the position of the scroll and decides to zoom in or out
        if (MouseListener.getScrollY() != 0.0f){
            // gets the value of scroll and also changes your scroll speed based on how much you have scrolled in or out
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1/ levelEditorCamera.getZoom());
            // gives the value in opposite direction as if we scroll up we zoom in
            addValue  *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);

        }

        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)){
            reset = true;
        }

        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            // set the zoom to it's origin position 1 slowly using lerp
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            // gradually increases the lerp time
            this.lerpTime += 0.1f * dt;
            // Checks if the camera is close enough to the origin (0, 0) (within a small margin of 5.0f for both x and y).
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f &&
                    Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                this.lerpTime = 0.0f;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }

    }
}
