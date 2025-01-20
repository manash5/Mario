package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.components.RigidBody2D;
import util.AssetPool;

// this class has the properties and characteristics of the flower in the mario game

public class Flower extends Component{
    private transient RigidBody2D rb;

    // initializes the necessary components needed to spawn a flower
    @Override
    public void start(){
        this.rb = gameObject.getComponent(RigidBody2D.class);
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        this.rb.setIsSensor();
    }

    // checks if any object has collided, if the collided object has playerControlller class (which means it is a character like mario)
    // then we set the powerup of that character and destroy the flower
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal){
        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (playerController != null){
            playerController.powerup();
            this.gameObject.destroy();
        }
    }


}
