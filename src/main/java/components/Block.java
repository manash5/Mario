package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;

// This class is basically made for blocks in mario, here we add some animation to blocks that when in contact they
// go slightly up and come back to original position

public abstract class Block extends Component{
    private transient boolean bopGoingUp = true;
    private transient boolean doBopAnimation = false;
    private transient Vector2f bopStart;
    private transient Vector2f topBopLocation;
    private transient boolean active = true;

    public float bopSpeed = 0.4f;

    // here we initialize the block’s starting position (bopStart) and calculates the top position (topBopLocation)
    // the block reaches during the animation.
    @Override
    public void start(){
        this.bopStart = new Vector2f(this.gameObject.transform.position);
        this.topBopLocation = new Vector2f(bopStart).add(0.0f, 0.02f);
    }

    // here we handle the bop animation
    @Override
    public void update(float dt) {
        if (doBopAnimation) {
            if (bopGoingUp) {
                // Move up until reaching the top position
                if (this.gameObject.transform.position.y < topBopLocation.y) {
                    this.gameObject.transform.position.y += bopSpeed * dt;
                } else {
                    bopGoingUp = false; // Switch direction
                }
            } else {
                // Move down until reaching the starting position
                if (this.gameObject.transform.position.y > bopStart.y) {
                    this.gameObject.transform.position.y -= bopSpeed * dt;
                } else {
                    // Reset animation
                    this.gameObject.transform.position.y = this.bopStart.y;
                    bopGoingUp = true;
                    doBopAnimation = false;
                }
            }
        }
    }

    // here we trigger the bop animation when the player hits the block
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (active && playerController != null && contactNormal.y < -0.8f) {
            doBopAnimation = true; // Start the animation
            AssetPool.getSound("assets/sounds/bump.ogg").play(); // Play sound
            playerHit(playerController); // Trigger custom behavior
        }
    }


    public void setInactive(){
        this.active = false;
    }


    abstract void playerHit(PlayerController playerController);
}
