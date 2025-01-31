package components;

import engine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;
import engine.Window;

// this class describes the properties and characteristic of the coin in the mario game

public class Coin extends Component{
    private Vector2f topY;
    private float coinSpeed = 1.4f;
    private transient boolean playAnim = false;
    private boolean called = false;

    // sets the topY a little bit above the y position of the coin
    @Override
    public void start(){
        topY = new Vector2f(this.gameObject.transform.position.y).add(0, 0.5f);
    }


    @Override
    public void update(float dt){
        // If the coin is in animation mode (e.g., after being collected), it moves upward toward topY at a speed controlled by coinSpeed.
        // and once the coin reaches it target height, it is destroyed from the game
        if (playAnim){
            if (this.gameObject.transform.position.y < topY.y){
                this.gameObject.transform.position.y += dt* coinSpeed;
                //Reduces the coin's horizontal scale (scale.x) to make it look like it's shrinking as it moves up.
                this.gameObject.transform.scale.x-= (0.5f* dt)% -1.0f;
            } else{
                gameObject.destroy();
            }
        }
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal){
        // This ensures the coin reacts properly when collected by the player, plays the correct sound, and begins the animation.
        if (obj.getComponent(PlayerController.class) != null){
            AssetPool.getSound("assets/sounds/coin.ogg").play();
            if(!called){
                Window.incrementCoin();
                Window.getScene().incrementCoinCounter();
                called = true;
            }
            playAnim = true;
            contact.setEnabled(false);
        }
    }

}
