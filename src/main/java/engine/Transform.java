package engine;

import components.Component;
import editor.EImGui;
import org.joml.Vector2f;

// this class is used to transform the shape of the sprite. it changes the position, scale , index of the object.
// it also rotates the object from its center

public class Transform extends Component {

    public Vector2f position;
    public Vector2f scale;
    public float rotation = 0.0f;
    public int zIndex;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
        this.zIndex = 0; // default index value
    }

    // creates a duplicate of the current transform
    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    // sets the current transform class attributes to the one passed as parameter
    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    // Checks if an object is instance of transform if not converts it to a transform
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Transform)) return false;

        Transform t = (Transform)o;
        return t.position.equals(this.position) && t.scale.equals(this.scale)
                && t.rotation == this.rotation && t.zIndex == this.zIndex;
    }

    // It handles the user input for updating the properties of a gameObject using a method provided by the EImGui class
    @Override
    public void imgui(){
        gameObject.name = EImGui.inputText("Name: ", gameObject.name);
        EImGui.drawVec2Control("Position", this.position);
        EImGui.drawVec2Control("Scale", this.scale, 32.0f);
        this.rotation = EImGui.dragFloat("Rotation", this.rotation);
        this.zIndex = EImGui.dragInt("Z-index", this.zIndex);
    }
}