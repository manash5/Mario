package engine;

import components.*;
import components.Pipe;
import org.joml.Vector2f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.PillboxCollider;
import physics2d.components.RigidBody2D;
import physics2d.enums.BodyType;
import util.AssetPool;


// Utility class for creating pre-defined game objects with specific components and animations.
// Creates a gameObject when an sprite is clicked on

public class Prefabs {

    // Creates a gameObject and adds some component to it (transform, sprite) and renders it
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    // creates a gameObject called mario and adds animation to it
    public static GameObject generateMario() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png");
        Spritesheet bigPlayerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/bigSpritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        // Little mario animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.2f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big mario animations
        AnimationState bigRun = new AnimationState();
        bigRun.title = "BigRun";
        bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState();
        bigJump.title = "BigJump";
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = new AnimationState();
        fireRun.title = "FireRun";
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState();
        fireJump.title = "FireJump";
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addState(run.title, switchDirection.title, "switchDirection");
        stateMachine.addState(run.title, idle.title, "stopRunning");
        stateMachine.addState(run.title, jump.title, "jump");
        stateMachine.addState(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addState(switchDirection.title, run.title, "startRunning");
        stateMachine.addState(switchDirection.title, jump.title, "jump");
        stateMachine.addState(idle.title, run.title, "startRunning");
        stateMachine.addState(idle.title, jump.title, "jump");
        stateMachine.addState(jump.title, idle.title, "stopJumping");

        stateMachine.addState(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addState(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigRun.title, bigJump.title, "jump");
        stateMachine.addState(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addState(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addState(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addState(bigIdle.title, bigJump.title, "jump");
        stateMachine.addState(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addState(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addState(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireRun.title, fireJump.title, "jump");
        stateMachine.addState(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addState(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addState(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addState(fireIdle.title, fireJump.title, "jump");
        stateMachine.addState(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addState(run.title, bigRun.title, "powerup");
        stateMachine.addState(idle.title, bigIdle.title, "powerup");
        stateMachine.addState(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addState(jump.title, bigJump.title, "powerup");
        stateMachine.addState(bigRun.title, fireRun.title, "powerup");
        stateMachine.addState(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addState(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addState(bigJump.title, fireJump.title, "powerup");

        stateMachine.addState(bigRun.title, run.title, "damage");
        stateMachine.addState(bigIdle.title, idle.title, "damage");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addState(bigJump.title, jump.title, "damage");
        stateMachine.addState(fireRun.title, bigRun.title, "damage");
        stateMachine.addState(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addState(fireJump.title, bigJump.title, "damage");

        stateMachine.addState(run.title, die.title, "die");
        stateMachine.addState(switchDirection.title, die.title, "die");
        stateMachine.addState(idle.title, die.title, "die");
        stateMachine.addState(jump.title, die.title, "die");
        stateMachine.addState(bigRun.title, run.title, "die");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addState(bigIdle.title, idle.title, "die");
        stateMachine.addState(bigJump.title, jump.title, "die");
        stateMachine.addState(fireRun.title, bigRun.title, "die");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addState(fireIdle.title, bigIdle.title, "die");
        stateMachine.addState(fireJump.title, bigJump.title, "die");
        mario.addComponent(stateMachine);

        PillboxCollider pb = new PillboxCollider();
        pb.width = 0.39f;
        pb.height = 0.31f;
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        mario.addComponent(rb);
        mario.addComponent(pb);
        mario.addComponent(new PlayerController());

        return mario;
    }

    public static GameObject generateLuigi() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/Luigii.png");
        Spritesheet BigPlayerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/Big.png");
        Spritesheet fire = AssetPool.getSpritesheet("assets/images/spritesheets/bigSpritesheet.png");
        GameObject luigi = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        // Little mario animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.2f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big mario animations
        AnimationState bigRun = new AnimationState();
        bigRun.title = "BigRun";
        bigRun.addFrame(BigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(BigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(BigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState();
        bigJump.title = "BigJump";
        bigJump.addFrame(BigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = new AnimationState();
        fireRun.title = "FireRun";
        fireRun.addFrame(fire.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(fire.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(fire.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState();
        fireJump.title = "FireJump";
        fireJump.addFrame(fire.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addState(run.title, switchDirection.title, "switchDirection");
        stateMachine.addState(run.title, idle.title, "stopRunning");
        stateMachine.addState(run.title, jump.title, "jump");
        stateMachine.addState(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addState(switchDirection.title, run.title, "startRunning");
        stateMachine.addState(switchDirection.title, jump.title, "jump");
        stateMachine.addState(idle.title, run.title, "startRunning");
        stateMachine.addState(idle.title, jump.title, "jump");
        stateMachine.addState(jump.title, idle.title, "stopJumping");

        stateMachine.addState(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addState(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigRun.title, bigJump.title, "jump");
        stateMachine.addState(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addState(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addState(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addState(bigIdle.title, bigJump.title, "jump");
        stateMachine.addState(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addState(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addState(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireRun.title, fireJump.title, "jump");
        stateMachine.addState(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addState(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addState(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addState(fireIdle.title, fireJump.title, "jump");
        stateMachine.addState(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addState(run.title, bigRun.title, "powerup");
        stateMachine.addState(idle.title, bigIdle.title, "powerup");
        stateMachine.addState(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addState(jump.title, bigJump.title, "powerup");
        stateMachine.addState(bigRun.title, fireRun.title, "powerup");
        stateMachine.addState(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addState(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addState(bigJump.title, fireJump.title, "powerup");

        stateMachine.addState(bigRun.title, run.title, "damage");
        stateMachine.addState(bigIdle.title, idle.title, "damage");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addState(bigJump.title, jump.title, "damage");
        stateMachine.addState(fireRun.title, bigRun.title, "damage");
        stateMachine.addState(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addState(fireJump.title, bigJump.title, "damage");

        stateMachine.addState(run.title, die.title, "die");
        stateMachine.addState(switchDirection.title, die.title, "die");
        stateMachine.addState(idle.title, die.title, "die");
        stateMachine.addState(jump.title, die.title, "die");
        stateMachine.addState(bigRun.title, run.title, "die");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addState(bigIdle.title, idle.title, "die");
        stateMachine.addState(bigJump.title, jump.title, "die");
        stateMachine.addState(fireRun.title, bigRun.title, "die");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addState(fireIdle.title, bigIdle.title, "die");
        stateMachine.addState(fireJump.title, bigJump.title, "die");
        luigi.addComponent(stateMachine);

        PillboxCollider pb = new PillboxCollider();
        pb.width = 0.39f;
        pb.height = 0.31f;
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        luigi.addComponent(pb);
        luigi.addComponent(rb);
        luigi.addComponent(new PlayerController());

        return luigi;
    }


    public static GameObject generateNepaliMario() {
        Spritesheet playerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/Nepali.png");
        Spritesheet BigPlayerSprites = AssetPool.getSpritesheet("assets/images/spritesheets/bigNepali.png");
        Spritesheet fire = AssetPool.getSpritesheet("assets/images/spritesheets/bigNepali.png");
        GameObject NepaliMario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        // Little mario animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.2f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big mario animations
        AnimationState bigRun = new AnimationState();
        bigRun.title = "BigRun";
        bigRun.addFrame(BigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(BigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(BigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(BigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState();
        bigJump.title = "BigJump";
        bigJump.addFrame(BigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = new AnimationState();
        fireRun.title = "FireRun";
        fireRun.addFrame(fire.getSprite(fireOffset + 0), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(fire.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(fire.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(fire.getSprite(fireOffset + 0), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState();
        fireJump.title = "FireJump";
        fireJump.addFrame(fire.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addState(run.title, switchDirection.title, "switchDirection");
        stateMachine.addState(run.title, idle.title, "stopRunning");
        stateMachine.addState(run.title, jump.title, "jump");
        stateMachine.addState(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addState(switchDirection.title, run.title, "startRunning");
        stateMachine.addState(switchDirection.title, jump.title, "jump");
        stateMachine.addState(idle.title, run.title, "startRunning");
        stateMachine.addState(idle.title, jump.title, "jump");
        stateMachine.addState(jump.title, idle.title, "stopJumping");

        stateMachine.addState(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addState(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigRun.title, bigJump.title, "jump");
        stateMachine.addState(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addState(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addState(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addState(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addState(bigIdle.title, bigJump.title, "jump");
        stateMachine.addState(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addState(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addState(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireRun.title, fireJump.title, "jump");
        stateMachine.addState(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addState(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addState(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addState(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addState(fireIdle.title, fireJump.title, "jump");
        stateMachine.addState(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addState(run.title, bigRun.title, "powerup");
        stateMachine.addState(idle.title, bigIdle.title, "powerup");
        stateMachine.addState(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addState(jump.title, bigJump.title, "powerup");
        stateMachine.addState(bigRun.title, fireRun.title, "powerup");
        stateMachine.addState(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addState(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addState(bigJump.title, fireJump.title, "powerup");

        stateMachine.addState(bigRun.title, run.title, "damage");
        stateMachine.addState(bigIdle.title, idle.title, "damage");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addState(bigJump.title, jump.title, "damage");
        stateMachine.addState(fireRun.title, bigRun.title, "damage");
        stateMachine.addState(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addState(fireJump.title, bigJump.title, "damage");

        stateMachine.addState(run.title, die.title, "die");
        stateMachine.addState(switchDirection.title, die.title, "die");
        stateMachine.addState(idle.title, die.title, "die");
        stateMachine.addState(jump.title, die.title, "die");
        stateMachine.addState(bigRun.title, run.title, "die");
        stateMachine.addState(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addState(bigIdle.title, idle.title, "die");
        stateMachine.addState(bigJump.title, jump.title, "die");
        stateMachine.addState(fireRun.title, bigRun.title, "die");
        stateMachine.addState(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addState(fireIdle.title, bigIdle.title, "die");
        stateMachine.addState(fireJump.title, bigJump.title, "die");
        NepaliMario.addComponent(stateMachine);

        PillboxCollider pb = new PillboxCollider();
        pb.width = 0.39f;
        pb.height = 0.31f;
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        NepaliMario.addComponent(pb);
        NepaliMario.addComponent(rb);
        NepaliMario.addComponent(new PlayerController());

        return NepaliMario;
    }


    // generates an gameObject called questionblock and adds animation to it
    public static GameObject generateQuestionBlock() {
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        // Creates a run object and add animation to it
        AnimationState flicker = new AnimationState();
        flicker.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(items.getSprite(0), 0.57f);
        flicker.addFrame(items.getSprite(1), defaultFrameTime);
        flicker.addFrame(items.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        AnimationState inactive = new AnimationState();
        inactive.title = "Inactive";
        inactive.addFrame(items.getSprite(3), 0.1f);
        inactive.setLoop(false);

        // Creates stateMachine component and adds run object to it and last the
        // stateMachine component is added to the questionblock gameObject
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(flicker.title);
        stateMachine.addState(flicker.title, inactive.title, "setInactive");
        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Static);
        questionBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        questionBlock.addComponent(b2d);
        questionBlock.addComponent(new Ground());


        return questionBlock;
    }

    public static GameObject generateBlockCoin(){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f);

        // Creates a run object and add animation to it
        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "Flicker";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        // Creates stateMachine component and adds run object to it and last the
        // stateMachine component is added to the questionblock gameObject
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new QuestionBlock());
        coin.addComponent(new BlockCoin());

        return coin;
    }

    public static GameObject generateCoin(){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f);

        // Creates a run object and add animation to it
        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        // Creates stateMachine component and adds run object to it and last the
        // stateMachine component is added to the questionblock gameObject
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new Coin());

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.12f);
        coin.addComponent(circleCollider);
        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Static);
        coin.addComponent(rb);

        return coin;
    }

    public static GameObject generateMushroom(){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject mushroom = generateSpriteObject(items.getSprite(10), 0.25f, 0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        mushroom.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        mushroom.addComponent(circleCollider);
        mushroom.addComponent(new MushroomAI());

        return mushroom;
    }

    public static GameObject generateStar(){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject star = generateSpriteObject(items.getSprite(24), 0.25f, 0.25f);

        AnimationState starFlip = new AnimationState();
        starFlip.title = "StarFlip";
        float defaultFrameTime = 0.23f;
        starFlip.addFrame(items.getSprite(25), 0.57f);
        starFlip.addFrame(items.getSprite(26), defaultFrameTime);
        starFlip.addFrame(items.getSprite(27), defaultFrameTime);
        starFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(starFlip);
        stateMachine.setDefaultState(starFlip.title);
        star.addComponent(stateMachine);
        star.addComponent(new StarAI());

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        star.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        star.addComponent(circleCollider);
        star.addComponent(new StarAI());

        return star;
    }

    public static GameObject generateFlower(){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject flower = generateSpriteObject(items.getSprite(20), 0.25f, 0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        flower.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.14f);
        flower.addComponent(circleCollider);
        flower.addComponent(new Flower());


        return flower;
    }

    public static GameObject generateGoomba(){
        Spritesheet sprites = AssetPool.getSpritesheet("assets/images/spritesheets/spritesheet.png");
        GameObject goomba = generateSpriteObject(sprites.getSprite(14), 0.25f, 0.25f);

        // Creates a run object and add animation to it
        AnimationState walk = new AnimationState();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(sprites.getSprite(14), defaultFrameTime);
        walk.addFrame(sprites.getSprite(15), defaultFrameTime);
        walk.setLoop(true);

        AnimationState squashed = new AnimationState();
        squashed.title = "squased";
        squashed.addFrame(sprites.getSprite(16), 0.1f);
        squashed.setLoop(false);

        // Creates stateMachine component and adds run object to it and last the
        // stateMachine component is added to the questionblock gameObject
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(squashed);
        stateMachine.setDefaultState(walk.title);
        stateMachine.addState(walk.title, squashed.title, "squashMe");
        goomba.addComponent(stateMachine);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setMass(0.1f);
        rb.setFixedRotation(true);
        goomba.addComponent(rb);
        CircleCollider circle = new CircleCollider();
        circle.setRadius(0.12f);
        goomba.addComponent(circle);
        goomba.addComponent(new GoombaAI());

        return goomba;
    }

    public static GameObject generatePipe(Direction direction){
        Spritesheet pipes = AssetPool.getSpritesheet("assets/images/spritesheets/pipes.png");
        int index = direction == Direction.Down? 0:
                    direction == Direction.Up? 1:
                    direction == Direction.Right? 2:
                    direction == Direction.Left ? 3: -1;
        assert index != -1: "Invalid pipe Direction";

        GameObject pipe = generateSpriteObject(pipes.getSprite(index), 0.5f, 0.5f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        pipe.addComponent(rb);

        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.5f, 0.5f));
        pipe.addComponent(b2d);
        pipe.addComponent(new Pipe(direction));
        pipe.addComponent(new Ground());

        return pipe;
    }

    public static GameObject generateTurtle(){
        Spritesheet turtleSprites = AssetPool.getSpritesheet("assets/images/spritesheets/turtle.png");
        GameObject turtle = generateSpriteObject(turtleSprites.getSprite(0), 0.25f, 0.35f);

        // Creates a run object and add animation to it
        AnimationState walk = new AnimationState();
        walk.title = "Walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(turtleSprites.getSprite(0), defaultFrameTime);
        walk.addFrame(turtleSprites.getSprite(1), defaultFrameTime);
        walk.setLoop(true);

        AnimationState turtleShell = new AnimationState();
        turtleShell.title = "TurtleShellSpin";
        turtleShell.addFrame(turtleSprites.getSprite(2), 0.1f);
        turtleShell.setLoop(false);

        // Creates stateMachine component and adds run object to it and last the
        // stateMachine component is added to the questionblock gameObject
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(turtleShell);
        stateMachine.setDefaultState(walk.title);
        stateMachine.addState(walk.title, turtleShell.title, "squashMe");
        turtle.addComponent(stateMachine);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setMass(0.1f);
        rb.setFixedRotation(true);
        turtle.addComponent(rb);
        CircleCollider circle = new CircleCollider();
        circle.setRadius(0.13f);
        circle.setOffset(new Vector2f(0, -0.05f));
        turtle.addComponent(circle);
        turtle.addComponent(new TurtleAI());

        return turtle;
    }

    public static GameObject generateFireball(Vector2f position){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject fireball = generateSpriteObject(items.getSprite(32), 0.18f, 0.18f);
        fireball.transform.position = position;

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        fireball.addComponent(rb);

        CircleCollider circleCollider = new CircleCollider();
        circleCollider.setRadius(0.08f);
        fireball.addComponent(circleCollider);
        fireball.addComponent(new Fireball());

        return fireball;
    }

    public static GameObject generateFlagTop(){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject flagtop = generateSpriteObject(items.getSprite(6), 0.25f, 0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        flagtop.addComponent(rb);

        Box2DCollider box2DCollider = new Box2DCollider();
        box2DCollider.setHalfSize(new Vector2f(0.1f, 0.25f));
        box2DCollider.setOffset(new Vector2f(-0.075f, 0.0f));
        flagtop.addComponent(box2DCollider);
        flagtop.addComponent(new Flagpole(true));

        return flagtop;
    }

    public static GameObject generateFlagPole(){
        Spritesheet items = AssetPool.getSpritesheet("assets/images/spritesheets/items.png");
        GameObject flagtop = generateSpriteObject(items.getSprite(33), 0.25f, 0.25f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        flagtop.addComponent(rb);

        Box2DCollider box2DCollider = new Box2DCollider();
        box2DCollider.setHalfSize(new Vector2f(0.1f, 0.25f));
        box2DCollider.setOffset(new Vector2f(-0.075f, 0.0f));
        flagtop.addComponent(box2DCollider);
        flagtop.addComponent(new Flagpole(false));

        return flagtop;
    }

}