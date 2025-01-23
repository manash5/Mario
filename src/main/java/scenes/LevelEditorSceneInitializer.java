package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
import util.AssetPool;

import java.io.File;
import java.util.Collection;

// This class implements the SceneInitializer that makes sure certain functions are called
// It defines the setup for a level editor scene—a scene where developers can create and modify game levels.

public class LevelEditorSceneInitializer extends SceneInitializer {

    private GameObject obj1;
    private Spritesheet sprites;
    SpriteRenderer obj1Sprite;

    private GameObject levelEditorStuff;

    public LevelEditorSceneInitializer() {

    }

    // Initializes the level editor scene by setting up game objects, attaching essential components
    // (e.g., MouseControls, GridLines) and preparing the editor camera.
    // This method also ensures the main editor object is not serialized.
    @Override
    public void init(Scene scene) {
        // Loads all the necessary graphical assets
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

        // This is the main gameObject that contains all other gameObjects in it. It's basically like a viewport
        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);

    }

    @Override
    // Preloads assets such as shaders, spritesheets, textures, and sounds for the level editor.
    // Ensures textures and state machines of existing game objects are properly refreshed.
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/turtle.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/turtle.png"),
                        16, 24, 4, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/bigSpritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/bigSpritesheet.png"),
                        16, 32, 42, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/pipes.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/pipes.png"),
                        32, 32, 4, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/items.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/items.png"),
                        16, 16, 43, 0));
        AssetPool.addSpritesheet("assets/images/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));

        AssetPool.addSpritesheet("assets/images/spritesheets/Luigii.png",new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/Luigii.png"),
                17, 15, 14, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/Nepali.png",new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/Nepali.png"),
                17, 16, 14, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/bigNepali.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/bigNepali.png"),
                        16, 32, 42, 0));
        AssetPool.addSpritesheet("assets/images/spritesheets/Big.png",new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/Big.png"),
                17, 32, 21, 0));

        AssetPool.getTexture("assets/images/blendImage2.png");

        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);

        // Loops through all the gameObject in the scene
        for(GameObject g: scene.getGameObjects()){
            // if the object has a spriteRenderer compoent then it save to spr
            if (g.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                // if the sprite has a texture it sets the texture from the assetpool calling the texture
                // this ensures that your texture is loaded
                if (spr.getTexture() != null){
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            // if the object has a StateMachine class then it stores it to stateMachine and refreshes it 
            if (g.getComponent(StateMachine.class) != null){
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }


    // Sets up the ImGui-based user interface for the level editor scene.
    // Provides functionality to place sprites, configure prefabs, and manage sounds through interactive tabs.
    @Override
    public void imgui() {
//        System.out.println("X: " + MouseListener.getScreenX(Window.getWindowX()));
//        System.out.println("Y" + MouseListener.getScreenY(Window.getWindowY()));

        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();


        ImGui.begin("Objects");

        if (ImGui.beginTabBar("WindowTabBar")) {
            // Here we also add tab in the window, where each tab will have specific sprites
            // first one will contain the blocks
            // second one contains sprite that has animation like mario, questionBlock
            // third one has sounds

            // we show blocks only when they are active (i.e. clicked) to increase the perfomance
            if (ImGui.beginTabItem("Solid Blocks")) {
                // Retrieves and stores the following information
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos); // top left position of the window in screen space
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize); // width and height of the window
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing); // the space between items

                // gives the x -coords of the right side of the sprisheet window which is the max size of the window
                float windowX2 = windowPos.x + windowSize.x;
                // iterates through all sprites
                for (int i = 0; i < sprites.size(); i++) {
                    if(i==34) continue;
                    if (i>= 38 && i< 61) continue;

                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    // creates the image button of the sprite and creates a new gameObject every time it is clicked.
                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y,
                            texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        RigidBody2D rb = new RigidBody2D();
                        rb.setBodyType(BodyType.Static);
                        object.addComponent(rb);
                        Box2DCollider b2d = new Box2DCollider();
                        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
                        object.addComponent(b2d);
                        object.addComponent(new Ground());

                        if (i==12){
                            object.addComponent(new BreakableBrick());
                        }
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    // check if another button can fit in the same line or not
                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            // Here we have the grass and cloud stuff that we and add in to our game
            if (ImGui.beginTabItem("Decoration Blocks")){
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos); // top left position of the window in screen space
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize); // width and height of the window
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing); // the space between items

                // gives the x -coords of the right side of the sprisheet window which is the max size of the window
                float windowX2 = windowPos.x + windowSize.x;
                // iterates through all sprites
                for (int i = 34; i < 61; i++) {
                    if (i >= 35 && i< 38) continue;
                    if (i >= 42 && i< 45) continue;

                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 4;
                    float spriteHeight = sprite.getHeight() * 4;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    // creates the image button of the sprite and creates a new gameObject every time it is clicked.
                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y,
                            texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    // check if another button can fit in the same line or not
                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if (ImGui.beginTabItem("Prefabs")) {
                int uid = 0;

                // Setting up Mario icon button
                Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 4;
                float spriteHeight = sprite.getHeight() * 4;
                createPrefabButton(uid++, playerSprites, Prefabs.generateMario(), 0, spriteWidth, spriteHeight); // Index 0 for Mario sprite

                Spritesheet luigi = AssetPool.getSpritesheet("assets/images/spritesheets/Luigii.png");
                createPrefabButton(uid++, luigi, Prefabs.generateLuigi(), 0, spriteWidth, spriteHeight);

                Spritesheet NepaliMario = AssetPool.getSpritesheet("assets/images/spritesheets/Nepali.png");
                createPrefabButton(uid++, NepaliMario, Prefabs.generateNepaliMario(), 0, spriteWidth, spriteHeight);


                // Setting up Question Block image button
                Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
                createPrefabButton(uid++, items, Prefabs.generateQuestionBlock(), 0, spriteWidth, spriteHeight); // Index 0 for Question Block sprite

                // Setting up Goomba image button
                createPrefabButton(uid++, playerSprites, Prefabs.generateGoomba(), 14, spriteWidth, spriteHeight); // Index 14 for Goomba sprite

                // Setting up Turtle image button
                Spritesheet turtle = AssetPool.getSpritesheet("assets/images/spritesheets/turtle.png");
                createPrefabButton(uid++, turtle, Prefabs.generateTurtle(), 0, spriteWidth, spriteHeight); // Index 0 for Turtle sprite

                // Setting up top part of the Flag
                createPrefabButton(uid++, items, Prefabs.generateFlagTop(), 6, spriteWidth, spriteHeight); // Index 6 for Flag Top sprite

                // Setting up pole part of the Flag
                createPrefabButton(uid++, items, Prefabs.generateFlagPole(), 33, spriteWidth, spriteHeight); // Index 33 for Flag Pole sprite

                // Setting up Pipe (Downwards)
                Spritesheet pipes = AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png");
                createPrefabButton(uid++, pipes, Prefabs.generatePipe(Direction.Down), 0, spriteWidth, spriteHeight); // Index 0 for Down Pipe sprite

                // Setting up Pipe (Upwards)
                createPrefabButton(uid++, pipes, Prefabs.generatePipe(Direction.Up), 1, spriteWidth, spriteHeight); // Index 1 for Up Pipe sprite

                // Setting up Pipe (Right)
                createPrefabButton(uid++, pipes, Prefabs.generatePipe(Direction.Right), 2, spriteWidth, spriteHeight); // Index 2 for Right Pipe sprite

                // Setting up Pipe (Left)
                createPrefabButton(uid++, pipes, Prefabs.generatePipe(Direction.Left), 3, spriteWidth, spriteHeight); // Index 3 for Left Pipe sprite

                // Setting up Coin image button
                createPrefabButton(uid++, items, Prefabs.generateCoin(), 7, spriteWidth, spriteHeight); // Index 7 for Coin sprite

                ImGui.endTabItem();
            }

            // In this tab we have all songs used in the game in button in the sound tab
            if (ImGui.beginTabItem("Sounds")){
                // loads the sound in the sound array of sound class
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for (Sound sound: sounds){
                    //get every sounds filepath
                    File tmp = new File(sound.getFilepath());
                    // if a sound name in a button is clicked
                    if (ImGui.button(tmp.getName())){
                        // checks if the sound is playing or not
                        // plays if it is not playing, stops if it is playing
                        if (!sound.isPlaying()){
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }

                    if (ImGui.getContentRegionAvailX()> 100){
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        ImGui.end();
    }

    // This funcitons help creates prefabs button which will allow you to add and drag animation objects in
    public void createPrefabButton(int uid, Spritesheet spritesheet, GameObject object, int index, float spriteWidth, float spriteHeight){
        // Retrieves the sprite from the spritesheet based on the provided index
        Sprite sprite = spritesheet.getSprite(index);

        // Retrieves the sprite texture ID and coordinates
        int id = sprite.getTexId();
        Vector2f[] texCoords = sprite.getTexCoords();

        // Increment uid and create the button
        ImGui.pushID(uid);
        if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
            // Pickup the object associated with the button
            // here we allow the game object which is the animation whose button we have made to be able to drag and drog in the
            // viewport editor window
            levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
        }
        ImGui.popID();
        ImGui.sameLine();
    }



}