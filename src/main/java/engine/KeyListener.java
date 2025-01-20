package engine;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

// The KeyListener class is a utility to handle key interactions in a game.

public class KeyListener {
    private static KeyListener instance;
    // this is an array that contains all the characters used in keyboard
    // and stores true if the character is clicked and false if it is not
    private boolean keyPressed[] = new boolean[350];
    private boolean keyBeginPress[] = new boolean[350];

    private KeyListener() {

    }

    // resets the values of each key to false
    public static void endFrame(){
        Arrays.fill(get().keyBeginPress, false);
    }

    // this is used to create a keyListener class if it is not created
    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }

        return KeyListener.instance;
    }

    // this is used to update if the boolean state of the button in the array when clicked or realeased
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
            get().keyBeginPress[key] = true;
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
            get().keyBeginPress[key] = false;
        }
    }

    // this is used to get the current state of any key when called
    // if it is being pressed or not
    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }


    public static boolean keyBeginPress(int keyCode) {
        return get().keyBeginPress[keyCode];
    }
}