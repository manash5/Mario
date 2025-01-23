package physics2d.components;

import components.Component;
import engine.Window;
import org.joml.Vector2f;

// A collider for Mario's character, combining circles and a box for smooth and
// efficient movement and collision in the game world.
// pill collider is the combination of box and 2 circle where it used for moving characters to have smooth collisions.

public class PillboxCollider extends Component {
    private transient CircleCollider topCircle = new CircleCollider();
    private transient CircleCollider bottomCircle  = new CircleCollider();
    private transient Box2DCollider box = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false;

    public float width = 0.1f;
    public float height = 0.1f;
    public Vector2f offset = new Vector2f();

    // this function is called when the game begins or object is initialized
    // assigns the gameObject of this class to circle and box gameObject
    @Override
    public void start(){
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.box.gameObject = this.gameObject;
        recalculateColliders();
    }

    // Calls on the editor update of all the objects
    @Override
    public void editorUpdate(float dt){
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);

        //  it calls resetFixture() to rebuild the collider.
        if (resetFixtureNextFrame){
            resetFixture();
        }
    }

    // it calls resetFixture() to rebuild the collider.
    @Override
    public void update(float dt){
        if (resetFixtureNextFrame){
            resetFixture();
        }
    }

    public void setWidth(float newVal){
        this.width = newVal;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float newVal){
        this.height = newVal;
        recalculateColliders();
        resetFixture();
    }

    // This method updates the physical representation of the collider in the physics engine.
    // resets all the values in the PillBox
    public void resetFixture(){
        // If the physics engine is "locked" (not accepting changes), it delays
        // the reset by setting resetFixtureNextFrame to true.
        if (Window.getPhysics().isLocked()){
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        // If the gameObject has a RigidBody2D component, it tells the physics engine to update the pillbox collider.
        if (gameObject != null){
            RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
            if (rb != null){
                Window.getPhysics().resetPillboxCollider(rb, this);
            }
        }
    }

    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox() {
        return box;
    }

    //  dynamically adjusts the sizes and positions of the pillbox's components (top circle, bottom circle and box)
    // based on current dimensions (width, height and offset)
    public void recalculateColliders(){
        float circleRadius = width/ 4.0f;
        float boxHeight = height -2 * circleRadius;
        topCircle.setRadius(circleRadius);
        bottomCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight/ 4.0f));
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));
        box.setHalfSize(new Vector2f(width / 2.0f, boxHeight/ 2.0f));
        box.setOffset(offset);
    }


}

