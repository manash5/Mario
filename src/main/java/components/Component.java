package components;

// component is like a function of gameObjects. for eg. walking, running, jumping, these are all the components of game Object.
// one example of components is transform class where we have the properties of every gameObject like their position,
// scale, rotation and zindex

import editor.EImGui;
import engine.GameObject;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    // we are giving each component unique id so that when every components are called it doesnot change the gameobject constantly
    private static int ID_COUNTER =0;
    private int uid = -1;

    public transient GameObject gameObject = null;

    public void start() {

    }

    public void update(float dt) {

    }

    public void editorUpdate(float dt) {

    }

    // This function is called when two objects first start colliding.
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    // This function is called when two objects stop colliding.
    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    // This function is called before the physics engine resolves the collision.
    // Modify or cancel the collision before it’s resolved.
    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    // This function is called after the physics engine resolves the collision.
    //  React to the results of the collision after it’s resolved.
    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal){

    }

    //Automatically displays and updates the values of integer fields in a class
    // eg. you have a child class called body if in its imgui function it calls the super.imgui() then this function
    // will run and displays the values to be edited based on their datatype
    public void imgui() {
        try{
            // retrieves all the fields(variables) declared in the class that is called
            Field[] fields = this.getClass().getDeclaredFields();
            for(Field field: fields){
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                // if the extracted field is transient we skip it
                if (isTransient){
                    continue;
                }

                // if the extracted field is private set it accesible
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate){
                    field.setAccessible(true);
                }

                Class type = field.getType(); // gets the type of the field (e.g. int, String)
                Object value = field.get(this); // Gets the current value of the field
                String name = field.getName(); // gets the name of the field

                if (type == int.class){
                    int val = (int)value;
                    //Wrap the int value in an array (imInt) because ImGui requires an array for
                    // updating values (ImGui modifies arrays directly).
                    int[] imInt = {val};
                    field.set(this, EImGui.dragInt(name, val));
                    // same way we keep other values
                }  else if (type == float.class) {
                    float val = (float)value;
                    field.set(this, EImGui.dragFloat(name, val));
                } else if (type == boolean.class) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f)value;
                    EImGui.drawVec2Control(name, val);
                }else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f)value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f)value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                } else if(type.isEnum()){
                    String[]  enumValues = getEnumValues(type);
                    String enumType = ((Enum)value).name(); // gets the current enum value being used
                    ImInt index = new ImInt(indexOf(enumType, enumValues)); // Finds the index at which the current enum value is at
                    // ImGui.combo creates the dropdown of the enum values
                    // when clicked on it shows the selected enum and will return true if another enum type is clicked otherwise it returns false
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)){
                        field.set(this, type.getEnumConstants()[index.get()]);  
                    }
                } else if (type == String.class){
                    field.set(this, EImGui.inputText(field.getName() + ": ",
                            (String)value));
                }

                // setting the accesibility of private field to false after it is used
                if (isPrivate){
                    field.setAccessible(false);
                }
            }
        } catch(IllegalAccessException e){
            e.printStackTrace();
        }

    }

    // generates new ids to each component based on the ID_COUNTER
    public void generateId(){
        if (this.uid == -1){
            this.uid = ID_COUNTER++;
        }
    }

    // This function makes sure that the object passed is of class Enum otherwise it shows error
    // Basically it goes through a file and get all the names of enum it stores
    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType){
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i =0;
        // iterates through all constants of the enum
        for (T enumIntegerValue: enumType.getEnumConstants()){
            //  Stores the name of the current constant in the enumValues array.
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }

    private int indexOf(String str, String[] arr){
        for(int i=0; i< arr.length; i++){
            if (str.equals(arr[i])){
                return i;
            }
        }

        return -1;
    }

    public void destroy() {
    }

    public int getUid(){
        return this.uid;
    }

    // Prevents duplicate id during serialization
    public static void init(int maxId){
        ID_COUNTER = maxId;
    }



}