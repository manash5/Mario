package observers;

import engine.GameObject;
import observers.events.Event;

// Observer is the blueprint for observer who notifies an event

//onNotify is like a messenger system.
//It sends a message (Event) from one object (GameObject) to all the listeners (observers).
//Each listener decides what to do when they receive the message.
//This pattern helps make your code modular, meaning different parts of your game can respond to events without being tightly connected.

public interface Observer {
    void onNotify(GameObject object, Event event);

}

// How it works
// Observers (listeners) use EventSystem.addObserver() to register themselves to listen for events.
// When something happens (e.g., saving the game), EventSystem.notify() is called.
// Each observer’s onNotify() method is called, and they react based on the event.
