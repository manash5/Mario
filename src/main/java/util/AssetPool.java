package util;

import components.Spritesheet;
import engine.Sound;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// this class is used to point all the addresses of the resources that we need to run the resources as quickly and
// efficient as possible

public class AssetPool {
    // Creates a hashMap of all the resources that we need
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();

    // gets the shader that we need so that we can load it
        public static Shader getShader(String resourceName) {
            File file = new File(resourceName);
            // checks if we already have shader class stored by using the file path
            if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
                return AssetPool.shaders.get(file.getAbsolutePath());
            } else { // if not we will create a new shader class and store it
                Shader shader = new Shader(resourceName);
                shader.compile();
                AssetPool.shaders.put(file.getAbsolutePath(), shader);
                return shader;
            }
        }

    // This function also searches if the texture class is made from the resourceName that we have given
    // if it is not then it creates and loads the file in the hashMap of the texture HashMap
    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
            return AssetPool.textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    // Checks if the spritesheet already exists in the spirtesheet hashMap if not then it addes it
    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        File file = new File(resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    // asserts false if the spritesheet is not found in the spritesheet HashMap else it returns the spritesheet
    public static Spritesheet getSpritesheet(String resourceName) {
        File file = new File(resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())) {
            assert false : "Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.";
        }
        return AssetPool.spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }

    // returns all the sounds stored in the sound HashMap
    public static Collection<Sound> getAllSounds(){
        return sounds.values();
    }

    // searched the sound key in the sound hashMap and returns the value of that key
    public static Sound getSound(String soundFile){
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())){
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false: "Sound file not added " + soundFile + "'";
        }

        return null;
    }

    // This function checks if the sound already exists in sound hashMap, if not then it loads it into the hashMap
    public static Sound addSound(String soundFile, boolean loops){
        File file = new File(soundFile);
        if (sounds.containsKey(file.getAbsolutePath())){
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            AssetPool.sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }
}