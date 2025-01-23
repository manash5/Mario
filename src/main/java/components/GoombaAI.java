package components;

import engine.Camera;
import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

// this class represents the characteristic of the goomba in mario

public class GoombaAI extends Component {

    private transient boolean goingRight = false;
    private transient RigidBody2D rb;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient boolean isDead = false;
    private transient float timeToKill = 0.5f;
    private transient StateMachine stateMachine;
    private transient boolean onPause = false;
    private transient int counter = 0;
    private boolean wasEscPressed = false;

    // Initializes the necessary components
    @Override
    public void start() {
        this.stateMachine = this.gameObject.getComponent(StateMachine.class);
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt) {
        // assigns onPause to true if esc button is pressed
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) && !wasEscPressed) {
            onPause = !onPause;  // Toggle the pause state
            wasEscPressed = true;  // Mark that ESC was pressed
        } else if (!KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            wasEscPressed = false;  // Reset the state when ESC is released
        }

        // stops the movement of the goomba
        if (onPause) {
            // Stop all movement by setting velocity to zero
            this.velocity.zero();
            this.rb.setVeloctiy(new Vector2f());
            return;
        }

        // if the goomba is not visible in the camera then we don't have to update
        Camera camera = Window.getScene().camera();
        if (this.gameObject.transform.position.x >
                camera.position.x + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        // if the goomba is dead then we wait to few secs and after that we destroy the object
        if (isDead) {
            timeToKill -= dt;
            if (timeToKill <= 0) {
                this.gameObject.destroy();
            }
            this.rb.setVeloctiy(new Vector2f());
            return;
        }

        // changes the direction if it is going left
        if (goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }

        // checks to see if it is on ground, if it is then it sets the vertical acceleration to zero
        checkOnGround();
        if (onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        // increases velocity as the time increases and also makes sure it doesnot cross a certain amount of velocity on both left and right side
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rb.setVeloctiy(velocity);
    }

    // This function checks if the character is in the ground or not
    public void checkOnGround() {
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.14f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    // this function determines what to do when an object collides with the goomba character
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (isDead) {
            return;
        }

        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (playerController != null) {
            // if the mario is not dead and the goomba has a contact force more than 0.58 then it means that mario has stomp over the goomba
            // so we activate the stomp funciton
            if (!playerController.isDead() && !playerController.isHurtInvincible() &&
                    contactNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
            // if the player is not dead or invincible and there is no contactNormal.y then we make the mario die
            } else if (!playerController.isDead() && !playerController.isInvincible()) {
                playerController.die();
            }
        // here we check if there is any contactNormal.y force or not if it is less significant then we check the which direction the
        // goomba is going
        } else if (Math.abs(contactNormal.y) < 0.1f) {
            goingRight = contactNormal.x < 0;
        }

        // check if the goomba has collided with fireball or not if it is then call the stomp effect and also make the fireball disappear
        if (obj.getComponent(Fireball.class) != null) {
            stomp();
            obj.getComponent(Fireball.class).disappear();
        }
    }

    public void stomp() {
        stomp(true);
    }

    // here we reset the value of the goomba making it stop and play the sound of the stomp
    public void stomp(boolean playSound) {
        this.isDead = true;
        this.velocity.zero();
        this.rb.setVeloctiy(new Vector2f());
        this.rb.setAngularVelocity(0.0f);
        this.rb.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        this.rb.setIsSensor();
        if (playSound) {
            AssetPool.getSound("assets/sounds/bump.ogg").play();
        }
    }
}