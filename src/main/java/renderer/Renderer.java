package renderer;

// This file is responsible for displaying the objects in the screen
//renders and manages groups of batches, batches are an implementation detail, it is the renderer that
//acts as a simpleton manager and cranks out the renders in the end
import components.SpriteRenderer;
import engine.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    // adds the spriteRenderer class (which is the sprite details like vertex cords, texture) of the gameObject in the renderBatch
    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    // This function checks if the there is room for sprite to add and it is of same index, if all this criteria matches it adds to batch
    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            // This makes sure that batch has room and each sprite in the room has same zindex
            if (batch.hasRoom() && batch.zIndex() == sprite.gameObject.transform.zIndex) {
                Texture tex = sprite.getTexture();
                // Decides if a tex can be added on a batch or not with either three condition being true
                // if tex is null or it already has texture in its batch or there is tex space to add in the batch
                if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        // if the sprite doesn't fit into the available sprite batches then it creates a new and sort the batch in the list of batches
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE,
                    sprite.gameObject.transform.zIndex, this);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    // the gameObject has a spriteRenderer component then it goes through all the batches and find the gameObject and destroys
    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;
        for(RenderBatch batch: batches){
            if (batch.destroyIfExists(go)){
                return;
            }
        }
    }

    // assings the shader as the current shader
    public static void bindShader(Shader shader){
        currentShader = shader;
    }

    // returns the current shader
    public static Shader getBoundShader(){
        return currentShader;
    }

    // renders the each and every batch in the batches array
    public void render() {
        currentShader.use();
        for (int i =0; i< batches.size(); i++) {
            RenderBatch batch = batches.get(i); 
            batch.render();
        }
    }


}