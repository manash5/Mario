package scenes;

import components.*;
import util.AssetPool;


// This class implements the SceneInitializer that makes sure certain functions are called
// It defines the setup for a level editor scene—a scene where developers can create and modify game levels.

public class LevelSceneInitializer extends SceneInitializer {

    private boolean isVisible = false;



    public LevelSceneInitializer() {

    }

    // Initializes the level editor scene by setting up game objects, attaching essential components
    // (e.g., MouseControls, GridLines) and preparing the editor camera.
    // This method also ensures the main editor object is not serialized.
    @Override
    public void init(Scene scene) {
        Spritesheet sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        GameObject cameraObject = scene.createGameObject("GameCamera");
        cameraObject.addComponent(new GameCamera(scene.camera()));
        cameraObject.start();
        scene.addGameObjectToScene(cameraObject);


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
        for (GameObject g : scene.getGameObjects()) {
            // if the object has a spriteRenderer component then it save to spr
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                // if the sprite has a texture it sets the texture from the assetpool calling the texture
                // this ensures that your texture is loaded
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }

            // if the object has a StateMachine class then it stores it to stateMachine and refreshes it
            if (g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imgui(){
        if (!isVisible) return;
    }
}