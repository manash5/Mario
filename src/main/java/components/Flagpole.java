package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

// This class represents the characteristics of a pole of the flag in the mario game

public class Flagpole extends Component{
    private boolean isTop = false;

    public Flagpole(boolean isTop){
        this.isTop = isTop;
    }

    //if a object which has the playerController class then it calls the playWinAnimation function of the playerController class
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal){
        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (playerController != null){
            playerController.playWinAnimation(this.gameObject);
        }
    }
}
