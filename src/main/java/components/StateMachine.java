package components;

import imgui.ImGui;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// this class is the controller that decides when to switch between animations like from "walking" to "jumping"

public class StateMachine extends Component{
    // This class is used for making a condition (trigger) that moves the state machine
    // from one animation state to another.
    private class  StateTrigger{
        public String trigger;
        public String state;

        public StateTrigger(){

        }

        // here we will store all the necessary state that can be transition so like if ideal then trigger can be run
        public StateTrigger(String state, String trigger){
            this.state = state;
            this.trigger = trigger;

        }

        // Ensures that StateTrigger object can be compared
        @Override
        public boolean equals(Object o){
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger t2 = (StateTrigger)o;
            return t2.trigger.equals(this.trigger) &&  t2.state.equals(this.state);
        }

        // Ensures that StateTrigger object can be used as keys in HashMap
        @Override
        public int hashCode(){
            return Objects.hash(trigger, state);
        }
    }

    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList<>(); // stores all animation states
    private transient AnimationState currentState = null; // tracks the active animation state
    private String defaultStateTitle = ""; // stores the name of default state

    // we need to refresh them so that they will be display everytime it is serialized and deserialized
    // this tells every animation state to refresh their animation where it goes to animation state and uses refresh textures function
    public void refreshTextures(){
        for (AnimationState state: states){
            state.refreshTextures();
        }
    }

    // adds a transition between states
    // from: currentState, to: the state to transition to , onTrigger: the trigger that initiates the transition
    public void addStateTrigger(String from, String to , String onTrigger){
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);

    }

    // adds an animation state to the machine
    public void addState(AnimationState state){
        this.states.add(state);
    }

    public void addState(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    // searches the default state in the animationState list and sets it as currentState
    public void setDefaultState(String animationTitle){
        for(AnimationState state: states){
            if (state.title.equals(animationTitle)){
                defaultStateTitle= animationTitle;
                if (currentState == null){
                    currentState = state;
                    return;
                }
            }
        }
        System.out.println("Unable to find state " + animationTitle + " in default state");
    }

    public void trigger(String trigger){
        // goes through all the trigger events in the hashmap that we created
        for(StateTrigger state: stateTransfers.keySet()){
            // checks the trigger the currentState title and its trigger event to see if we can change the state or not
            // for eg. if you are idle then you can run and if you want to have a action first you have to be idle
            // so if the currentState title is running and we want to trigger it to action it won't unless there is a
            // StateTrigger that we can create to do so
            if (state.state.equals(currentState.title) && state.trigger.equals(trigger)){
                if (stateTransfers.get(state) != null){ // check if it's null or not
                    int newStateIndex = stateIndexOf(stateTransfers.get(state));
                    // if we find the state that we want to transition to then we assign that to the current state
                    if (newStateIndex> -1){
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }
    }

    // returns the index of the state in animationState list
    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : states) {
            if (state.title.equals(stateTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }


    // Sets the currentState to the state matching defaultStateTitle when the state machine starts.
    @Override
    public void start(){
        for (AnimationState state: states){
            if (state.title.equals(defaultStateTitle)){
                currentState = state;
                break;
            }
        }
    }

    // updates the currentState Animation and assigns its current sprite to spriterenderer component
    @Override
    public void update(float dt){
        if (currentState != null){
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if ( sprite != null){
                sprite.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    // same as update method
    @Override
    public void editorUpdate(float dt){
        if (currentState != null){
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if ( sprite != null){
                sprite.setSprite(currentState.getCurrentSprite());
                sprite.setTexture(currentState.getCurrentSprite().getTexture());
            }
        }
    }

    // UI for editing state
    @Override
    public void imgui() {
        for (AnimationState state : states) {
            ImString title = new ImString(state.title);
            ImGui.inputText("State: ", title);
            state.title = title.get();

            int index = 0;
            for (Frame frame : state.animationFrames) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + index + ") Time: ", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }
}
