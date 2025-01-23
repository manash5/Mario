package observers;

import engine.GameObject;
import observers.events.Event;

import java.util.ArrayList;
import java.util.List;

// Manages the list of observers and sends them events

public class EventSystem {
    private static List<Observer> observers = new ArrayList<>();

    // adds a new observer
    public static void addObserver(Observer observer){
        observers.add(observer);
    }

    // Sends out an event to all the observers
    public static void notify(GameObject obj, Event event){
        for (Observer observer: observers){
            observer.onNotify(obj, event );
        }
    }
}
