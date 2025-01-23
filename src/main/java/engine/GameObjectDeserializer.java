package engine;
// These deserialize files uses gson deserializer where they extract all the information from a json file and converts it's back
// to java objects. To deserialize we have to first serialize which is to convert the java objects into json files so that we can
// make changes from the UI (we don't need to code every changes)
// we use object serializer and deserializer inorder to save the progress of the level in leveleditorScene

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject(); //Converts the generic JSON element into a JsonObject for easier access.
        String name = jsonObject.get("name").getAsString(); //Retrieves the value of the name field from the JSON.
        //Extracts an array of components (which may be objects themselves).
        JsonArray components = jsonObject.getAsJsonArray("components");
        //Used context here since it is an object which makes it nested object
//        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
//        int zIndex = context.deserialize(jsonObject.get("zIndex"), int.class); // this is deserialized into an integer

        // Since components are objects too and are in array,we cannot use context in array so
        // we iterate through each components knowing there subclass and keep it back at their respective parent class(gameObject).
        // Reconstructs the gameObjects without any components yet
        GameObject go = new GameObject(name);
        // Iterates over the array list of components made above
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}