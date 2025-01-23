package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

// The MouseListener class is a utility to handle mouse interactions in a game.
// It tracks mouse movements, button clicks, scroll events, and provides methods to convert screen coordinates
// to world coordinates and vice versa.

public class MouseListener {
    private static MouseListener instance; // Singleton instance to ensure only one MouseListener exists.
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX, worldX, worldY, lastWorldX, lastWorldY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;


    //Stores the position and size of the game viewport (the visible area of the game).
    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f GameViewportSize = new Vector2f();

    // Counter for how many buttons are currently pressed
    private int mouseButtonDown = 0;

    // Constructor
    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    //Resets certain fields at the end of each frame.
    // This function is used to reset to the default screen position (i.e. zoom, drag)
    public static void endFrame() {
        get().scrollY = 0.0;
        get().scrollX = 0.0;
    }

    // resets all the value
    public static void clear() {
        get().scrollX = 0.0;
        get().scrollY = 0.0;
        get().xPos = 0.0;
        get().yPos = 0.0;
        get().lastX = 0.0;
        get().lastY = 0.0;
        get().mouseButtonDown = 0;
        get().isDragging = false;
        Arrays.fill(get().mouseButtonPressed, false);
    }

    // This get function is used to call the whole MouseListener class from main or other
    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }

        return MouseListener.instance;
    }

    // these are all the callbacks we have to do
    // callbacks means the function/ event that we have to handle while using mouse
    // these are events such as mouse clicked, mouse pressed, mouse scroll, etc.
    // the functions which are registered as callbacks gets automatically called whenever a mouse is used

    // this method is designed to update the mouse's position and check whether the user is dragging the mouse
    public static void mousePosCallback(long window, double xpos, double ypos) {
        if (!Window.getImguiLayer().getGameViewWindow().getWantCaptureMouse()){
            clear();
        }
        if (get().mouseButtonDown > 0){
            get().isDragging = true;
        }

        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        get().xPos = xpos;
        get().yPos = ypos;
    }

    // this method is designed to update the state of mouse button and check if the button is being pressed
    // this function gets updated every time a button on a mouse is clicked
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().mouseButtonDown++;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonDown--;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    // this method is designed to update the scroll input when the mouse being scrolled or not
    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }



    // These methods are used as utility functions which is designed to expose mouse-related state information,
    // enabling other parts of your game engine or application to interact with the current and past mouse states.
    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }


    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    // this function is used to get the current state of the button present in mouse
    // is it clicked or not?
    // the difference between this and MouseButtonCallBack is that this gets the current state of button when called
    // it doesn't automatically update the button state whenever the button is being used
    // this function is a utility function that can be used in other parts of the program to
    // check if the button is pressed or not.
    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    // This function converts the mouse position from screen to world coordinates
    public static Vector2f screenToWorld(Vector2f screenCoords) {
        // Converting into NDC coords
        // Since NDC coords range from -1 to 1 we are converting it to that but when we just divide it gives us from 0 to 1
        // so we multiply i to *2 -1 so that it can shift to NDC coords
        Vector2f normalizedScreenCords = new Vector2f(
                screenCoords.x / Window.getWidth(),
                screenCoords.y / Window.getHeight()
        );
        // here me have converted the 0 to 1 coords to -1 to 1
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        // calls the camera
        Camera camera = Window.getScene().camera();
        // Here we use the same formula used in default.glsl gl_position = uview * uprojection * vec4(apos, 1)
        // where we now trying to find the position of vec4(aPos, 1) which is the NDC Coords
        // that's why we are multiplying with inverse
        // since they are shifted to the gl_postion side
        Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y,
                0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    // This function converts the mouse world coords back to screen coords
    public static Vector2f worldToScreen(Vector2f worldCoords) {
        Camera camera = Window.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        // Here we are using the normal formula that we use in default.glsl that is
        // gl_position = view *  projection * vec4(aPos, 1)
        // which will give us the clip coords which means the necessary area needed to be displayed
        ndcSpacePos.mul(projection.mul(view));
        // we now then convert it into NDC coords
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        // after that we have now converted the -1 to 1 range back to 0 to 1 range
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        // and here we multiply with the dimensions of the screen to get the actual screen pixels coords
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));

        return windowSpace;
    }

    public static float getScreenX(){
        return getScreen().x;
    }

    public static float getScreenY(){
        return getScreen().y;
    }

    public static float getWorldDx() {
        return (float)(get().lastWorldX - get().worldX);
    }

    public static float getWorldDy() {
        return (float)(get().lastWorldY - get().worldY);
    }

    // returns the current mouse position in the screen space
    public static Vector2f getScreen(){
        // Subtract the viewport position to get the mouse position relative to the game viewport.
        float currentX = getX() - get().gameViewportPos.x;
        // Normalize by the viewport size and scale to the window dimensions.
        currentX = (currentX / get().GameViewportSize.x) * Window.getWindowX();
        float currentY = getY() - get().gameViewportPos.y;
        currentY = Window.getWindowY() -((currentY / get().GameViewportSize.y) * Window.getWindowY());

        return new Vector2f(currentX, currentY);

    }



    public static float getWorldX(){
        return getWorld().x;
    }


    public static float getWorldY(){
        return getWorld().y;
    }

    // returns the current mouse position in the world space
    public static Vector2f getWorld(){
        // Gets the screen coords
        float currentX = getX() - get().gameViewportPos.x;
        // converts the currentX to NDC position
        currentX = (2.0f * (currentX / get().GameViewportSize.x)) - 1.0f;
        float currentY = getY() - get().gameViewportPos.y;
        currentY = (2.0f * (1.0f - (currentY / get().GameViewportSize.y))) - 1;
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        // converts the NDC position into world coords
        Camera camera = Window.getScene().camera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));

        return new Vector2f(tmp.x, tmp.y);
    }

    // sets the gameViewportSize
    public static void setGameViewportSize(Vector2f getGameViewportSize) {
        get().GameViewportSize.set(getGameViewportSize);
    }

    // sets the gameViewport Position
    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }
}