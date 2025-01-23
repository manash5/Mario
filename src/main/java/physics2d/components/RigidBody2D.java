package physics2d.components;

import components.Component;
import engine.Window;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import physics2d.enums.BodyType;

// This class is a rigid body class that contains all the properties of the physics body like collider, mass, velocity

public class RigidBody2D extends Component {
    private Vector2f veloctiy = new Vector2f();
    private float angularDamping = 0.8f; // Reduces rotation over time.
    private float linearDamping = 0.9f; //  Reduces linear velocity over time.
    private float mass = 0;
    private BodyType bodyType = BodyType.Dynamic; // Specifies the type of body (default is dynamic)
    private float friction = 0.1f;
    public float angularVelocity = 0.0f;
    public float gravityScale = 1.0f;
    private boolean isSensor = false;

    private boolean fixedRotation = false; // Prevents the body from rotating if true.
    private boolean continuousCollision  = true; // Improves collision detection.

    private transient Body rawBody = null; // Represents the physical body in the physics world.

    @Override
    // Updates the game object's position and rotation based on the physics simulation.
    public void update(float dt){
        if (rawBody != null){
            if (this.bodyType == BodyType.Dynamic || this.bodyType == BodyType.Kinematic) {
                this.gameObject.transform.position.set(
                        rawBody.getPosition().x, rawBody.getPosition().y
                );
                this.gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
                Vec2 vel = rawBody.getLinearVelocity();
                this.veloctiy.set(vel.x, vel.y);
            } else if (this.bodyType == BodyType.Static){
                this.rawBody.setTransform(
                        new Vec2(this.gameObject.transform.position.x, this.gameObject.transform.position.y),
                        this.gameObject.transform.rotation);
            }
        }
    }

    public void addVelocity(Vector2f forceToAdd){
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(forceToAdd.x, forceToAdd.y));
        }
    }

    public void addImpulse(Vector2f impulse){
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(veloctiy.x, veloctiy.y), rawBody.getWorldCenter());
        }

    }

    public Vector2f getVeloctiy() {
        return veloctiy;
    }

    public void setVeloctiy(Vector2f veloctiy) {
        this.veloctiy.set(veloctiy);
        if (rawBody != null){
            this.rawBody.setLinearVelocity(new Vec2(veloctiy.x, veloctiy.y));
        }
    }

    public void setPosition(Vector2f newPos){
        if (rawBody != null){
            rawBody.setTransform(new Vec2(newPos.x, newPos.y), gameObject.transform.rotation);
        }
    }

    public void setAngularVelocity(float angularVelocity){
        this.angularVelocity = angularVelocity;
        if (rawBody != null){
            this.rawBody.setAngularVelocity(angularVelocity);
        }

    }


    public void setGravityScale(float gravityScale){
        this.gravityScale = gravityScale;
        if (rawBody != null){
            this.rawBody.setGravityScale(gravityScale);
        }

    }

    public void setIsSensor(){
        isSensor = true;
        if (rawBody != null){
            Window.getPhysics().setIsSensor(this);
        }
    }

    public void setNotSensor(){
        isSensor = false;
        if (rawBody!= null){
            Window.getPhysics().setNotSensor(this);
        }
    }

    public float getFriction(){
        return this.friction; 
    }

    public boolean isSensor(){
        return this.isSensor;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }
}
