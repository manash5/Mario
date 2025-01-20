package physics2d.components;

import components.Component;
import org.joml.Vector2f;
import renderer.DebugDraw;

// This class is used to create an invisible box that is used when two object collides. Think of it as a rectangle
// that wraps around things in the game to check if they touch or bump into other objects.
// It's like a safety bubble for objects!

public class Box2DCollider extends Component {
    private Vector2f halfSize  = new Vector2f(1);
    private Vector2f origin = new Vector2f();
    private Vector2f offset = new Vector2f();

    // returns the current offset of the box.
    public  Vector2f getOffset(){
        return this.offset;
    }

    // tells how big the box is
    public Vector2f getHalfSize() {
        return halfSize;
    }

    // sets the size
    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    // returns the origin
    public Vector2f getOrigin(){
        return this.origin;
    }

    // sets the offset
    public void setOffset(Vector2f newOffset ){this.offset.set(newOffset); }

    // this functions shows the box in the editor scene and makes the object is in the center of the box
    @Override
    public void editorUpdate(float dt){
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
    }
}
