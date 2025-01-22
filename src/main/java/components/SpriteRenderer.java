package components;

import editor.EImGui;
import engine.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;
//sprites are like digital stickers or flat images that represent characters, objects, or even parts of the background in a game.
// Think of them as the visual building blocks used to create what you see on the screen during gameplay.
//A pixel is the smallest dot of color that makes up a digital image or screen.
// A sprite is a collection of pixels arranged to form an image used in a game.
// Rendering is the process of drawing what you see in the game.

// The SpriteRenderer class is responsible for managing and rendering sprites in the game.
// It acts as the visual component that handles how a Sprite is displayed on the screen.

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean isDirty = true;


    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imgui() {
        float[] imColor = {color.x, color.y, color.z, color.w};
        //ImGui calls below must take place in context of an imgui window,
        // i.e sandwiched by ImGui.begin() and ImGui.end() calls or program crashes
        if (EImGui.colorPicker4("Color Picker", this.color)) {
            this.isDirty = true;
        }
    }

    public Vector4f getColor() {
        return this.color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.isDirty = true;
            this.color.set(color);
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }

    public void setTexture(Texture texture){
        this.sprite.setTexture(texture);
    }

    public void setDirty() {
        this.isDirty = true;
    }
}