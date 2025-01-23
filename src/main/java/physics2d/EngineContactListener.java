package physics2d;

import components.Component;
import engine.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

// This class is use to manage and respond to collisions (or "contacts") between objects in a 2D physics simulation
// Here we have two different object since collision happens between two object so we need to get detail of both obj
// Contact is a class from the JBox2D library that provides information about a collision between two fixtures.
// Every time two fixtures collide or interact in the physics world, a Contact object is created or updated to
// represent the interaction.

public class EngineContactListener implements ContactListener {
    // The physics engine automatically calls this method when collisions occur and provides
    // the Contact object to describe the collision.
    @Override
    public void beginContact(Contact contact) {
        // Accesses the user-defined data for the first fixture (a shape attached to a physics body).
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        // worldMainfold stores information about the points of collision (direction where the collision is pushing)
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        // collects collision detail for both objects
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        // calls all the component of both object and calls tha begin collision method for all components sending
        // info about other object, collision detail and collision normal for object
        for (Component c: objA.getAllComponents()){
            c.beginCollision(objB, contact, aNormal);
        }

        for (Component c: objB.getAllComponents()){
            c.beginCollision(objA, contact, bNormal);
        }
    }

    // This method is used after the collision between two object ends
    @Override
    public void endContact(Contact contact) {
        // this function also does the same as the beginContact but here we call all the endContact method of
        // all component
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c: objA.getAllComponents()){
            c.endCollision(objB, contact, aNormal);
        }

        for (Component c: objB.getAllComponents()){
            c.endCollision(objA, contact, bNormal);
        }
    }

    // This method gets called before a collision happens with two object
    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        // this function also does the same as the beginContact but here we call all the preSolve method of
        // all component
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c: objA.getAllComponents()){
            c.preSolve(objB, contact, aNormal);
        }

        for (Component c: objB.getAllComponents()){
            c.preSolve(objA, contact, bNormal);
        }
    }

    // this method is called right after they collide with each other
    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        // this function also does the same as the beginContact but here we call all the postSolve method of
        // all component
        GameObject objA = (GameObject)contact.getFixtureA().getUserData();
        GameObject objB = (GameObject)contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        for (Component c: objA.getAllComponents()){
            c.postSolve(objB, contact, aNormal);
        }

        for (Component c: objB.getAllComponents()){
            c.postSolve(objA, contact, bNormal);
        }
    }

}
