package components;

import engine.GameObject;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.RigidBody2D;

// this class represents the fireball that the fire mario throws

public class Fireball extends Component{
    public transient boolean goingRight = false;
    private transient RigidBody2D rb;
    private transient float fireballSpped = 1.7f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient float lifetime = 4.0f;


    private static int fireballCount = 0;


    public static boolean canSpawn(){
        return fireballCount < 4;
    }


    @Override
    public void start(){
        this.rb = this.gameObject.getComponent(RigidBody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f; // sets the acceleration so that the fireball gets down
        fireballCount++; // counts as the fireball gets created
    }

    @Override
    public void update(float dt){
        lifetime-= dt;

        // if the lifetime of the fireball has been finished we call the disappear funciton
        if (lifetime<=0){
            disappear();
            return;
        }

        // sets the velocity of the fireball based on which direction we are going
        if (goingRight){
            velocity.x = fireballSpped;
        } else {
            velocity.x = -fireballSpped;
        }

        // checks if the fireball is on the ground
        checkOnGround();
        if (onGround){
            this.acceleration.y = 1.5f;
            this.velocity.y = 2.5f;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        // updates and restricts the velocity of the fireball
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -terminalVelocity.y);
        this.rb.setVeloctiy(velocity);
    }

    public void checkOnGround(){
        float innerPlayerWidth = 0.25f * 0.7f;
        float yVal = -0.09f;
        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    // checks if it has collided with any object, if it has then it checks the direction of the fireball
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal){
        if (Math.abs(contactNormal.x) > 0.8f){
            this.goingRight = contactNormal.x < 0;
        }
    }

    // if function makes sure it ignores the collision with mario or another fireball
    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal){
        if (obj.getComponent(PlayerController.class) != null || obj.getComponent(Fireball.class) != null){
            contact.setEnabled(false);
        }
    }

    // reduces the fireCount and destroys the fireball
    public void disappear(){
        fireballCount--;
        this.gameObject.destroy();
    }
}
