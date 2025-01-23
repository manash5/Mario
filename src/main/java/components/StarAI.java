package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.RigidBody2D;

public class StarAI extends Component{
    private transient boolean goingRight = true;
    private transient RigidBody2D rb;
    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
    private transient float maxSpeed = 0.8f;
    private transient boolean hitPlayer = false;

    // initializes the mushroom
    @Override
    public void start(){
        this.rb = gameObject.getComponent(RigidBody2D.class);
    }

    @Override
    public void update(float dt){
        // if the mushroom is going right and has a speed less than max speed then add the speed to the rigid body
        if (goingRight && Math.abs(rb.getVeloctiy().x)< maxSpeed){
            rb.addVelocity(speed);
            // if it not going right then change the direction of the mushroom speed
        } else if (!goingRight && Math.abs(rb.getVeloctiy().x)< maxSpeed){
            rb.addVelocity(new Vector2f(-speed.x, speed.y));
        }
    }

    // This preSolve is used to make the mushroom bounce back when it collides with other
    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal){
        // here we are fixing the mario when it collides with mushroom, we will make it pass through us
        PlayerController playerController = obj.getComponent(PlayerController.class);
        // making the player pass through the mushroom
        if (playerController != null){
            contact.setEnabled(false);
            if (!hitPlayer){
                playerController.hurtInvincibilityTimeLeft =5.0f;
                this.gameObject.destroy(); // destroys the mushroom
                hitPlayer = true; // make sure that mushroom has hit it only once
            }
        }

        // checks if the collision is mostly horizontal
        if (Math.abs(contactNormal.y)< 0.1f){
            // assigns true if the the condition is met
            goingRight = contactNormal.x < 0;
        }
    }
}
