package components;

import engine.Direction;
import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

// This class has the characteristic of what a pipe does in the mario game

public class Pipe extends Component{

    private Direction direction;
    private String connectingPipeName = "";
    private boolean isEntrance = false;
    private transient GameObject connectingPipe = null;
    private transient float entranceVectorTolerance = 0.6f;
    private transient PlayerController collidingPlayer = null;

    public Pipe(Direction direction){
        this.direction = direction;
    }

    // here we get the pipe name where it will be connected to
    @Override
    public void start(){
        connectingPipe = Window.getScene().getGameObject(connectingPipeName);
    }

    @Override
    public void update(float dt){
        // if the pipe is not connected to another pipe and there is nothing to check constantly
        if (connectingPipe  == null){
            return;
        }

        // if there is a player colliding at the pipe then we check in which type of pipe is it colliding
        if (collidingPlayer != null){
            boolean playerEntering = false;
            switch (direction){
                case Up:
                    // sets the playerEntering true if the condition is met
                    if ((KeyListener.isKeyPressed(GLFW_KEY_DOWN) ||
                            KeyListener.isKeyPressed(GLFW_KEY_S))
                            && isEntrance
                            && playerAtEntrance()){
                       playerEntering = true;
                    }
                    break;
                case Left:
                    // sets the playerEntering true if the condition is met
                    if ((KeyListener.isKeyPressed(GLFW_KEY_RIGHT)
                            || KeyListener.isKeyPressed(GLFW_KEY_D))
                            && isEntrance) {
                        playerEntering = true;
                    }
                    break;
                case Right:
                    // sets the playerEntering true if the condition is met
                    if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT)
                            || KeyListener.isKeyPressed(GLFW_KEY_A))
                            && isEntrance){
                        playerEntering = true;
                    }
                    break;
                case Down:
                    // sets the playerEntering true if the condition is met
                    if ((KeyListener.isKeyPressed(GLFW_KEY_UP)
                            || KeyListener.isKeyPressed(GLFW_KEY_W))
                            && isEntrance
                            && playerAtEntrance()){
                        playerEntering = true;
                    }
                    break;
            }

            // if the player is entering then we place the change the position of the player
            if (playerEntering){
                collidingPlayer.setPosition(
                        getPlayerPosition(connectingPipe)
                );
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
            }
        }
    }

    // checks if a player is located at the "entrance" of a certain area or object based on specific directions.
    public boolean playerAtEntrance(){
        // returns if there is no colliding object
        if (collidingPlayer == null){
            return false;
        }

        // calculates the boundaries of the pipe
        Vector2f min = new Vector2f(gameObject.transform.position)
                .sub(new Vector2f(gameObject.transform.scale).mul(0.5f));
        Vector2f max = new Vector2f(gameObject.transform.position)
                .add(new Vector2f(gameObject.transform.scale).mul(0.5f));

        // calculates the boundaries of the player
        Vector2f playerMin = new Vector2f(collidingPlayer.gameObject.transform.position)
                .sub(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));
        Vector2f playerMax = new Vector2f(collidingPlayer.gameObject.transform.position)
                .add(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));

        // checks if the player is in the pipe entrance area according to the type of pipe
        switch (direction){
            case Up:
                return playerMin.y >= max.y &&
                        playerMax.x > min.x &&
                        playerMin.x < max.x;
            case Down:
                return playerMax.y <= min.y &&
                        playerMax.x > min.x &&
                        playerMin.x < max.x;
            case Right:
                return playerMax.x <= min.x &&
                        playerMax.y > min.y &&
                        playerMin.y < max.y;
            case Left:
                return playerMin.x >= max.x &&
                        playerMax.y > min.y &&
                        playerMin.y < max.y;
        }

        return false;
    }

    // if any object collides with the pipe it checks if the colliding object has a playerController class which means it's a character like
    // mario or luigi then it captures that class and places it in collidingPlayer
    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal){
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null){
            collidingPlayer = playerController;
        }

    }

    // after the collision, we do some work after that we no longer need to keep the value of the colliding player so we set it to none
    @Override
    public void endCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal){
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null){
            collidingPlayer = null;
        }
    }

    // sets the position of the player according to the pipe it connect's to
    private Vector2f getPlayerPosition(GameObject pipe) {
        Pipe pipeComponent = pipe.getComponent(Pipe.class);
        switch (pipeComponent.direction) {
            case Up:
                return new Vector2f(pipe.transform.position).add(0.0f, 0.5f);
            case Left:
                return new Vector2f(pipe.transform.position).add(-0.5f, 0.0f);
            case Right:
                return new Vector2f(pipe.transform.position).add(0.5f, 0.0f);
            case Down:
                return new Vector2f(pipe.transform.position).add(0.0f, -0.5f);
        }

        return new Vector2f();
    }
}
