package observers.events;

// This class represents an event with a specific type

public class Event {
    public EventType type;

    public Event(EventType type){
        this.type = type;
    }

    public Event(){
        this.type = EventType.UserEvent;
    }
}


//Observer Registration:
//
//Observers (like the UI, sound manager, or AI system) register themselves using EventSystem.addObserver().
//Event Creation:
//
//When something happens in the game (e.g., player collects a coin), an event is created (new Event(EventType.CoinCollected)).
//Event Notification:
//
//The event is sent to the EventSystem.notify(), which calls onNotify() for all registered observers.
//Observer Reaction:
//
//Each observer reacts independently to the event. For example:
//The UI updates the score.
//The sound manager plays a coin sound.
//The achievement system checks if a milestone is reached.