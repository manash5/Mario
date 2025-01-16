package physics2d;

import engine.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

// This class uses a 2D physics simulation. It uses JBox2D, a physics library, and
// connects physics events to a game engine.
// A ray is a concept or a representation of a line or path that is cast in the game world for specific purposes,
// like detecting collisions or interactions. A ray is essentially an imaginary line that has a starting point
// and extends in a direction, often used in simulations, physics, and games to detect where it intersects objects.

public class RaycastInfo implements RayCastCallback {
    // Fixture represents a shape in the physics engine. It is essentially the physical body of an object
    // (like a rectangle, circle, etc.) that can participate in collisions and physics interactions.
    public Fixture fixture;
    public Vector2f point; // point where it intersects/cross the fixture
    public Vector2f normal; // It is a vector that is perpendicular to the surface that was hit.
    public float fraction; // represents the distance traveled by the ray relative to its total length
    public boolean hit; //  indicates whether the ray actually hit something or not
    public GameObject hitObject; // represents a game object, likely an entity in your game that has properties like position, health, etc.
    private GameObject requestingObject; // it ensures the raycast doesnot accidentally hit the object that cast the ray

    public RaycastInfo(GameObject obj){
        fixture = null;
        point = new Vector2f();
        normal = new Vector2f();
        fraction = 0.0f;
        hit = false;
        hitObject = null;
        this.requestingObject = obj;
    }


    // This method is the callback provided by the JBox2D physics engine whenever a raycast hits a fixture.
    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        // checks if the fixture hits the raycast to the one that casted ray, if it is that condition then
        // it returns 1 which means to stop the processing
        if(fixture.m_userData == requestingObject){
            return 1;
        }
        this.fixture = fixture;
        this.point = new Vector2f(point.x, point.y);
        this.normal = new Vector2f(normal.x, normal.y);
        this.fraction = fraction;
        this.hit = fraction != 0;
        this.hitObject = (GameObject)fixture.m_userData;

        return fraction;
    }



}
