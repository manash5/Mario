package engine;

import Database.MyJDBC;
import components.GameCamera;
import editor.GameViewWindow;
import editor.SceneHierarchyWindow;
import imgui.ImGui;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import physics2d.Physics2D;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.LevelSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Vector;

import static imgui.ImGui.isKeyPressed;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// It serves as the main entry point where the game engine connects all parts such as rendering graphics, handling
// audio, managing scenes and handling user input
// This class is where we initialize the opengl to work towards the game we set up everything needed by openGl to use it on our game

// it inherits the Observer class so that it can track when an event has changed

public class Window implements Observer {
    private int width, height;
    private String title;
    private long glfwWindow;
    private ImGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    private boolean runtimePlaying = false;

    private static Window window = null;

    private static Scene currentScene;
    private static SceneHierarchyWindow sceneHierarchyWindow = new SceneHierarchyWindow();

    public static int[] windowSize;

    private long audioContext;
    private long audioDevice;
    public static boolean editMode = true;

    public Instant startTime;
    public Instant pauseStartTime;
    private long totalElapsedTime = 0;
    public boolean isPaused = false;
    public long pauseTime = 0;
    public static int coinsCollected =0;

    public MyJDBC myJDBC = new MyJDBC();
    private static boolean directGame = false;
    private static Scene prevScene;
    private boolean closed = false; // Flag to track if window is already hidden

    // Constructor
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        EventSystem.addObserver(this);
    }

    // this function is used to change the scene
    public static void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene!= null){
            prevScene = currentScene;
            currentScene.destroy();
        }

        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    // Singleton
    // Makes sure that window gets called only once
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    // makes a function that calls physics class so that we can use it on other packages like Component and Renderer
    public static Physics2D getPhysics(){return currentScene.getPhysics();}

    // Makes a function that returns the current scene which we are on
    public static Scene getScene() {
        return currentScene;
    }

    public static void incrementCoin(){
        coinsCollected+=1;
    }


    // This function is the core where all the work are being done
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();
        System.out.println(Arrays.toString(getActualWindowSize()));

        // Destroys all the audio context so that the audio space is free and can be used
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);


        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    // basic structure of the window file where setups are being done
    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        // Gives us our window size
        windowSize = getActualWindowSize();

        // These are callbacks that works on the background gathering all information and are running real time
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Initialize the audio device

        // this is the function that fetches information about the audio system
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        // opens the audio device
        audioDevice = alcOpenDevice(defaultDeviceName);

        // An audio context is like a workspace where audio operations happen.
        // alcCreateContext creates this workspace, linking it to the audioDevice.
        //The attributes array is used to specify additional settings for the context. Here, it’s {0}, which means "no special settings."
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        // Initializes capabilities so that the audio is made sure to be compatible with your audio system
        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10){
            assert false: "Audio library not supported.";
        }

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // This allows our system to process transparency and other color-mixing effects
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Initializes the frameBuffer and picking texture
        this.framebuffer = new Framebuffer(windowSize[0], windowSize[1]);
        this.pickingTexture = new PickingTexture(windowSize[0], windowSize[1]);
        //glViewport defines the part of the window OpenGL will render to.
        //(0, 0) sets the bottom-left corner of the viewport, and
        // windowSize[0] and windowSize[1] define its width and height.
        glViewport(0,0,windowSize[0], windowSize[1]);

        // Initializes the UI component which is ImGuiLayer so that we can make changes  in the object in the frame
        this.imguiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imguiLayer.initImGui();

        if (directGame){
            window.onNotify(null, new Event(EventType.GameEngineStartPlay));
            startTime = Instant.now();
        } else {
            window.onNotify(null, new Event(EventType.GameEngineStopPlay));
        }
    }

    // This part is done to keep track of things every time while playing game
    // it checks all the things inside it in loop so that if any changes are made during the game it can quickly make those changes
    public void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        // Initializes the time
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        // assigns the defaultShader and pickingshader file so that it can be used
        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        if (isKeyPressed(GLFW_KEY_ESCAPE)){
            pauseTime += Duration.between(pauseStartTime, Instant.now()).toSeconds();
            System.out.println("You paused for " + pauseTime);
        }

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // Render pass 1. Render to picking texture
            // turns off blending since picking texture doesn't need blending
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            // Defines the rendering area where our game will be seen
            glViewport(0, 0, windowSize[0], windowSize[1]);
            glClearColor(0,0,0,0); // sets the background to black and fully transparent
            // resets the color and buffer bit
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // renders the picking shader
            Renderer.bindShader(pickingShader);
            currentScene.render();

            // Disables the writing and again initializes the blending which we need to while rendering the game
            pickingTexture.disableWriting();
            glEnable(GL_BLEND);


            // Render pass 2. Render actual game
            DebugDraw.beginFrame();

            // binds the frame buffer as you might want to do extra things with the image before showing it.
            this.framebuffer.bind();
            Vector4f clearColor = currentScene.camera().clearColor;
            glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT); // clear the frame buffer

            // updates any changes made in the scene
            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    // when we are playing game it checks the update function to see any updates in the game
                    currentScene.update(dt);
                } else {
                    // when we are in the editor sector, it checks the editor update function to make changes
                    currentScene.editorUpdate(dt);
                }

                currentScene.render();
                DebugDraw.draw();
            }
            this.framebuffer.unbind();

            this.imguiLayer.update(dt, currentScene);

            KeyListener.endFrame();
            MouseListener.endFrame();
            glfwSwapBuffers(glfwWindow); //swap the color buffers

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }

    // gives the width of your screen
    public static int getWidth() {

        return windowSize[0]; //get().width;
    }

    // gives the height of your screen
    public static int getHeight() {

        return windowSize[1] - 70; //get().height;
    }

    // sets the width of your screen
    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    // sets the height of your screen
    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    // returns the window size in an array
    public int[] getActualWindowSize() {
        int[] actualWidth = new int[1];
        int[] actualHeight = new int[1];
        glfwGetWindowSize(glfwWindow, actualWidth, actualHeight);
        return new int[]{actualWidth[0], actualHeight[0]};
    }

    // gives out the frameBuffer so that it can used in other packages/ files.
    public static Framebuffer getFramebuffer(){
        return get().framebuffer;
    }

    // sets a specific aspect ratio which will be used in viewport so that we can have a proper screen
    public static float getTargetAspectRatio(){
        return 16.0f/ 9.0f;
    }

    // returns the window size in an array
    public int[] getWindowSize() {
        return windowSize;
    }

    // returns the imGuiLayer object that is currently being used
    public static ImGuiLayer getImguiLayer(){
        return get().imguiLayer;
    }


    // this is the function used in observer class which we have override
    // this is used to observe which state is our game in
    // eg. in viewport when we click start it will come as GameEngineStartPlay and that part of function will work
    // if we clicked stop then the GameEngineStop part will work
    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type){
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                if(currentScene!= null){
                    currentScene.save();
                    System.out.println("You have collected from scenes  " + currentScene.getCoinCounter() + " coins");
                }
                Window.changeScene(new LevelSceneInitializer());
                Window.setEditMode(false);
                AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
//                sceneHierarchyWindow.setFalse();
                // Resume or start the timer
                if (isPaused) {
                    isPaused = false; // Resume the timer
                    startTime = Instant.now(); // Mark resume time
                    System.out.println("Game resumed at: " + startTime);
                } else {
                    startTime = Instant.now(); // First-time start
                    totalElapsedTime = 0; // Reset only when restarting fresh
                    System.out.println("Game started at: " + startTime);
                }
                System.out.println("You have collected " + coinsCollected + " coins");
                System.out.println("You have collected from scenes" + currentScene.getCoinCounter() + " coins");
                System.out.println(MyJDBC.getUserID());
                myJDBC.checkScore(MyJDBC.getUserID(), coinsCollected, (int)totalElapsedTime);
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                if (currentScene != null){
                    System.out.println("You have collected from scenes  " + currentScene.getCoinCounter() + " coins");
                }
//                sceneHierarchyWindow.setTrue();
                Window.changeScene(new LevelEditorSceneInitializer());
                window.setEditMode(true);
                AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();

                // Pause the timer
                if (startTime != null) {
                    totalElapsedTime += Duration.between(startTime, Instant.now()).toSeconds();
                    totalElapsedTime -= pauseTime;
                    isPaused = true;
                    System.out.println("Game paused. Total elapsed time: " + totalElapsedTime + " seconds.");
                }
                System.out.println("You have collected " + coinsCollected + " coins");
                System.out.println(MyJDBC.getUserID());
                myJDBC.checkScore(MyJDBC.getUserID(), coinsCollected, (int)totalElapsedTime);
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
        }

    }

    // returns your window width
    public static int getWindowX(){
        return windowSize[0];
    }

    // returns your window height
    public static int getWindowY(){
        return windowSize[1];
    }

    public static SceneHierarchyWindow getSceneHierarchyWindow() {
        return sceneHierarchyWindow;
    }

    public static void setEditMode(boolean value){
        editMode = value;
    }

    public boolean getEditMode(){
        return editMode;
    }

    public static void setDirectGame(boolean value){
        directGame = value;
    }

    public int getTotalElapsedTime(){
        return (int)totalElapsedTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void close() {
        glfwSetWindowShouldClose(glfwWindow, true);
    }

    public static void restartGame() {

    }




    public void showWindow() {
        glfwShowWindow(glfwWindow);
    }



}