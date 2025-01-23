package physics2d.components;

import components.Component;
import org.joml.Vector2f;
import renderer.DebugDraw;

// this class is also same as box collider used for object who will have have a rounder shape like the fire ball
// and the turtle when he goes inside his shell.

public class CircleCollider extends Component {
    private float radius = 1f;
    protected Vector2f offset = new Vector2f();

    public  Vector2f getOffset(){
        return this.offset;
    }

    public float getRadius() {
        return radius;
    }

    public void setOffset(Vector2f newOffset ){this.offset.set(newOffset); }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void editorUpdate(float dt){
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addCircle2D(center, this.radius);

    }
}
