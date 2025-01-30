package components;

import engine.GameObject;
import engine.KeyListener;
import engine.Prefabs;
import engine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2d.Physics2D;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
import scenes.LevelSceneInitializer;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

// this playerController class is responsible for movement and behaviour of characters like mario, luigi

public class PlayerController extends Component {

    public enum PlayerState {
        Small,
        Big,
        Fire,
        Invincible
    }

    public float walkSpeed = 1.9f;
    public float jumpBoost = 1.0f;
    public float jumpImpulse = 3.0f;
    public float slowDownForce = 0.05f;
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private PlayerState playerState = PlayerState.Small;
    public PlayerState previousPlayerState = null;
    public transient boolean onGround = false;
    private transient float groundDebounce = 0.0f;
    // this variable is used when you jump but still touch a edge of a block, then you will have 0.1 sec to jump again
    // from the level
    private transient float groundDebounceTime = 0.1f;
    private transient RigidBody2D rb;
    private transient StateMachine stateMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime = 0; // how long am i holding the jump space button
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead = false;
    private transient int enemyBounce = 0;
    public transient float hurtInvincibilityTimeLeft = 0;
    private transient float hurtInvincibilityTime = 1.4f;
    private transient float deadMaxHeight = 0;
    private transient float deadMinHeight = 0;
    private transient boolean deadGoingUp = true;
    private transient float blinkTime = 0.0f;
    private transient SpriteRenderer spr;


    private transient boolean playWinAnimation = false;
    private transient float timeToCastle = 4.5f;
    private transient float walkTime = 2.2f;
    private transient boolean onPause = false;
    private boolean wasEscPressed = false;
    private transient float airTime = 0.0f;
    private transient float deathTimer = 0.0f; // Timer to track elapsed time after death
    private transient boolean hasPlayedMusic = false; // Flag to ensure music plays only once

    // Initializes all the stuff necessary for the character
    @Override
    public void start(){
        this.spr = gameObject.getComponent(SpriteRenderer.class);
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);
    }

    @Override
    public void update(float dt){
        // Ensure collision behavior matches the current player state
        updateCollisionBehavior();

        // checks if the function is in win state or not
        if (playWinAnimation){
            checkOnGround();
            // if the character is not in ground that means it is in flag as this state is trigger when on flag
            // we will now stop all other functions like running, jumping and slowing move down the character to the ground
            if (!onGround){
                gameObject.transform.scale.x = -0.25f;
                gameObject.transform.position.y -= dt;
                stateMachine.trigger("stopRunning");
                stateMachine.trigger("stopJumping");

            }else {
                // if it is in the ground then it checks the walkTime which allows to mario to walk for a while
                // if it has walk time it moves the x-axis and makes the character look like walking
                if (this.walkTime> 0){
                    gameObject.transform.scale.x  = 0.25f;
                    gameObject.transform.position.x += dt;
                    stateMachine.trigger("startRunning");
                }
                if (!AssetPool.getSound("assets/sounds/stage_clear.ogg").isPlaying()){
                    AssetPool.getSound("assets/sounds/stage_clear.ogg").play();
                }
                timeToCastle -= dt;
                walkTime -= dt;
                // TODO HERE WE CAN ADD THE AFTER STAGE CLEAR FILE
                if (timeToCastle <= 0){
                    AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
                    Window.changeScene(new LevelSceneInitializer());
                }
            }
            return;
        }
        // checks if the character is dead, if it is then
        if (isDead){
            AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
            // The character first rises to a maximum height before falling.
            if (this.gameObject.transform.position.y < deadMaxHeight && deadGoingUp){
                this.gameObject.transform.position.y += dt * walkSpeed / 2.0f;
            } else if(this.gameObject.transform.position.y >= deadMaxHeight && deadGoingUp){
                deadGoingUp = false; // if it is already upto the deadMaxHeight then deeadGoing up is set to false
            // now here if the body is already at a certain height and will not go up then gravity is applied to make it seem like it's falling
            } else if (!deadGoingUp && gameObject.transform.position.y > deadMinHeight){
                this.rb.setBodyType(BodyType.Kinematic);
                this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
                this.velocity.y += this.acceleration.y * dt;
                this.velocity.y = Math.max(Math.min(this.velocity.y , this.terminalVelocity.y), -this.terminalVelocity.y);
                this.rb.setVeloctiy(this.velocity);
                this.rb.setAngularVelocity(0);
            } else if (!deadGoingUp && gameObject.transform.position.y <= deadMinHeight){
                // Start the timer after the player is in the death position
                if (!hasPlayedMusic) {
                    deathTimer += dt; // Increment timer by delta time (dt)
                    // After a 2-second delay, the game restarts or transitions to a new level.
                    if (deathTimer >= 2.0f) {  // If 2 seconds have passed
                        AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").play();
                        Window.changeScene(new LevelSceneInitializer());
                        hasPlayedMusic = true; // Ensure the music is played only once
                    }
                }

            }
            return;
        }

        // checks if the player is still invincible  or not
        // if it is then decreases the time and also makes the character look like it's blinking by constantly changing it's alpha (w) component
        // of the color
        if (hurtInvincibilityTimeLeft > 0){
            hurtInvincibilityTimeLeft -= dt;
            blinkTime -= dt;

            if (blinkTime <= 0){
                blinkTime = 0.2f;
                if (spr.getColor().w ==1 ){
                    spr.setColor(new Vector4f(1,1,1,0));
                } else {
                    spr.setColor(new Vector4f(1,1,1,1));
                }
            } else {
                if (spr.getColor().w ==0){
                    spr.setColor(new Vector4f(1,1,1,1));
                }
            }

        }

        // here it checks which button is clicked and moves the character left or right according to the button clicked
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            // positive velocity means it is moving right but it has negative velocity then it means it is moving left which means
            // we have to change directions
            if (this.velocity.x < 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x += slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0) {
                this.stateMachine.trigger("switchDirection");
                this.velocity.x -= slowDownForce;
            } else {
                this.stateMachine.trigger("startRunning");
            }
        } else {
            // if no left or right button is clicked it sets all the physics value to 0 to make the character stop
            this.acceleration.x = 0;
            if (this.velocity.x > 0) {
                this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);
            }

            if (this.velocity.x == 0) {
                this.stateMachine.trigger("stopRunning");
            }
        }

        // checks if the e button is pressed when mario is in fire mode. if it is then it creates the  fire object
        // it makes sures that there are only 4 fire objects in the game at a time
        if (KeyListener.keyBeginPress(GLFW_KEY_E) && playerState == PlayerState.Fire &&
        Fireball.canSpawn()){
            Vector2f position = new Vector2f(this.gameObject.transform.position)
                    .add(this.gameObject.transform.scale.x > 0 ? new Vector2f(0.26f,0 ):
                            new Vector2f(-0.26f, 0));
            GameObject fireball = Prefabs.generateFireball(position);
            fireball.getComponent(Fireball.class).goingRight =
                    this.gameObject.transform.scale.x > 0;
            Window.getScene().addGameObjectToScene(fireball);

        }
        // checks if the esc button is pressed
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) && !wasEscPressed) {
            onPause = !onPause;  // Toggle the pause state
            wasEscPressed = true;  // Mark that ESC was pressed
        } else if (!KeyListener.isKeyPressed(GLFW_KEY_ESCAPE) ) {
            wasEscPressed = false;  // Reset the state when ESC is released
        }

        // if onPause is true then it resets the velocity value to stop the object
        if (onPause) {
            // Stop all movement by setting velocity to zero
            this.velocity.zero();
            this.rb.setVeloctiy(new Vector2f());
            return;
        }

        checkOnGround();
        if ((KeyListener.isKeyPressed(GLFW_KEY_SPACE) || KeyListener.isKeyPressed(GLFW_KEY_UP)) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            // Checks if a character is on the ground or just recently left the ground and it's not already in a jump
            if ((onGround || groundDebounce > 0) && jumpTime == 0){
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                jumpTime = 58;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0){ // if the jump is ongoing
                jumpTime--; // reduces the jump time
                this.velocity.y = ((jumpTime / 2.2f) * jumpBoost); // decreases the upward force slowly as it goes up
            } else { // if no condition for jumping are met, stop vertical movement
                this.velocity.y = 0;
            }
            groundDebounce = 0;
        // checks if it has bounce on the enemy, if it has then it reduces the enemy bounce time and sets the velocity accordingly
        } else if(enemyBounce> 0) {
            enemyBounce--;
            this.velocity.y = ((enemyBounce/ 2.2f) * jumpBoost);

        }else if(!onGround){ // this statement is when the character is falling when jump key is no longer pressed
            if (this.jumpTime > 0){
                // gradually decreases the velocity
                // here the velocity is not negative as it is going down, it is gradually decreasing and acceleration
                // is also applied so it will eventually be negative
                this.velocity.y *= 0.35f;
                this.jumpTime = 0;
            }
            groundDebounce -= dt;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f; // applies gravity
        } else {// if it is not jumping or is not in ari then we set the y velocity and acceleration to 0
            this.velocity.y = 0;
            this.acceleration.y = 0;
            groundDebounce = groundDebounceTime;
        }
        //Updates the character's velocity and applies constraints to prevent excessive speed.
        // terminal velocity is like the maximum amount of speed a object can go
        this.velocity.x += this.acceleration.x * dt;
        this.velocity.y += this.acceleration.y * dt;
        // here at first we have used math.min to make sure that it doesnot exceed the positive velocity
        //  then we have used man.max to make sure that it doesnot go lower then the negative terminal velocity
        // here positive means going right and negative means going left and now we have made sure that it does not exceeed spped past a certain amount
        // for eg. you don't want to move more than 5 velocity then it makes sure you have velocity upto -5 to 5 in both direction
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y , this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rb.setVeloctiy(this.velocity);
        this.rb.setAngularVelocity(0);

        if (!onGround){
            stateMachine.trigger("jump");
            // Increment airTime by the time delta (dt)
            airTime += dt;

            // If the player has been in the air for more than 5 seconds, trigger death
            if (airTime > 5.0f) {
                die(); // Call the die function to kill the player
            }
        } else{
            // Reset airTime when the player is back on the ground
            airTime = 0.0f;
            stateMachine.trigger("stopJumping");
        }


    }

    // gets the player width and height value of the marios when big or small and sends to checkonGround funciton that returns a boolean value
    // which tells us if it is ground or not
    public void checkOnGround(){
        float innerPlayerWidth = this.playerWidth * 0.6f;
        float yVal = playerState == PlayerState.Small ? -0.14f : -0.24f;

        onGround = Physics2D.checkOnGround(this.gameObject, innerPlayerWidth, yVal);
    }

    public void setPosition(Vector2f newPos){
        this.gameObject.transform.position.set(newPos);
        this.rb.setPosition(newPos);
    }

    // Here we are changing the mario state after it has leveled up
    // i.e. when he eats the mushroom
    public void powerup(){
        if (playerState == PlayerState.Small){
            playerState = PlayerState.Big;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
            gameObject.transform.scale.y = 0.42f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null){
                jumpBoost *= bigJumpBoostFactor;
                walkSpeed *= bigJumpBoostFactor;
                pb.setHeight(0.63f);
            }
        } else if (playerState == PlayerState.Big){
            playerState = PlayerState.Fire;
            AssetPool.getSound("assets/sounds/powerup.ogg").play();
        }
        updateCollisionBehavior(); // Sync collision behavior after state change
        stateMachine.trigger("powerup");
    }

    // this function c sets the playWinAnimation to true and resets the value of all the components in mario and makes it a static body type
    // which means it can't be moved and also plays the win animation song
    public void playWinAnimation(GameObject flagpole){
        AssetPool.getSound("assets/sounds/main-theme-overworld.ogg").stop();
        if (!playWinAnimation){
            playWinAnimation = true;
            velocity.set(0.0f, 0.0f);
            acceleration.set(0.0f, 0.0f);
            rb.setVeloctiy(velocity);
            rb.setIsSensor();
            rb.setBodyType(BodyType.Static);
            gameObject.transform.position.x = flagpole.transform.position.x;
            AssetPool.getSound("assets/sounds/flagpole.ogg").play();
        }
    }

    // this function is used when our character collides another object in the game
    @Override
    public void beginCollision(GameObject collidingObject, Contact contact , Vector2f contactNormal){
        if (isDead) return;

        // checks if the object that the character has collided with has a ground component
        if (collidingObject.getComponent(Ground.class) != null){
            // If the collision happens mostly from the side (horizontal direction), the player’s horizontal speed
            // (velocity.x) is set to 0. This stops the player from sliding into the ground.
            // but the character will still go backward which is due to other mechanism in the game
            // we set it to zero to avoid the character moving forward in the direction of the collision
            if (Math.abs(contactNormal.x)> 0.8f){
                this.velocity.x = 0;
            // if it hits vertically then it stops all the components related to the vertical motion
            } else if (contactNormal.y > 0.8f){
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }

    public boolean isSmall(){
        return this.playerState == PlayerState.Small;
    }

    public boolean isBig(){
        return this.playerState == PlayerState.Big;
    }

    public void enemyBounce(){
        this.enemyBounce= 8;
    }

    public boolean isDead(){
        return this.isDead;
    }

    public boolean isHurtInvincible(){
        return this.hurtInvincibilityTimeLeft > 0 || playWinAnimation;
    }

    public boolean isInvincible(){
        return this.playerState == PlayerState.Invincible || this.hurtInvincibilityTimeLeft > 0 || playWinAnimation;
    }

    // this functions handles what happens when a player character dies or gets hurt in a game.
    public void die(){
        this.stateMachine.trigger("die");
        // if the player is small it resets all the component of the mario and sets it bodyType to static
        if (this.playerState == PlayerState.Small){
            this.velocity.set(0, 0);
            this.acceleration.set(0, 0);
            this.rb.setVeloctiy(new Vector2f());
            this.isDead = true;
            this.rb.setIsSensor();
            AssetPool.getSound("assets/sounds/mario_die.ogg").play();
            deadMaxHeight = this.gameObject.transform.position.y  + 0.3f;
            this.rb.setBodyType(BodyType.Static);
            if (gameObject.transform.position.y > 0){
                deadMinHeight = -0.25f;
            }
        // if the player is big it makes the player small again with a invincibility time
        } else if (this.playerState == PlayerState.Big){
            this.playerState = PlayerState.Small;
            gameObject.transform.scale.y = 0.25f;
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null){
                jumpBoost /= bigJumpBoostFactor;
                walkSpeed /= bigJumpBoostFactor;
                pb.setHeight(0.31f);
            }
            hurtInvincibilityTimeLeft = hurtInvincibilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
        // if the player is in fire mode it makes the player in big mode again with a invincibility time
        } else if (this.playerState == PlayerState.Fire){
            this.playerState = PlayerState.Big;
            hurtInvincibilityTimeLeft = hurtInvincibilityTime;
            AssetPool.getSound("assets/sounds/pipe.ogg").play();
        }

        updateCollisionBehavior(); // Sync collision behavior after state change
    }


    public boolean hasWon(){
        return false;
    }

    public void makeInvinsible() {
        playerState = PlayerState.Invincible;
        this.rb.setIsSensor(); // Set the rigid body as a sensor
        this.hurtInvincibilityTimeLeft = 5.0f; // Set invincibility duration
    }

    public void setPreviousPlayerState(PlayerState playerState){
        previousPlayerState = playerState;
    }

    public PlayerState getPlayerState(){
        return playerState;
    }

    private void updateCollisionBehavior() {
        if (this.hurtInvincibilityTimeLeft> 0 ) {
            this.rb.setIsSensor(); // Pass through objects
        } else if(!playWinAnimation && !isDead && this.hurtInvincibilityTimeLeft<=0) {
            this.rb.setNotSensor(); // Normal collision
        }
    }



}
