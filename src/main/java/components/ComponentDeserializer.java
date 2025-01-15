package components;

import com.google.gson.*;

import java.lang.reflect.Type;

// ComponentDeserializer is used to let computer handle abstract class since gson cannot handle it automatically.
// Here we provide custom logic required to properly reconstruct each Component from its Json representation.
// for eg spriteRedenderer is also a component where it inherits from abstract class component
// Here we will let the computer know that instaed of serializing the component we serialize it's children
// This then is used at gameObject deserializer where it handles components.
// Here the type means the name of the class
// and properties means attributes. for eg. in spriteRenderer class we have properties: texture, color

public class ComponentDeserializer implements JsonSerializer<Component>,
        JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        //Retrieves the fully qualified class name of the specific Component subclass (e.g., engine.ComponentA).
        String type = jsonObject.get("type").getAsString();
        // Retrieves the properties specific to the Component type.
        JsonElement element = jsonObject.get("properties");

        try {
            //Uses Class.forName(type) to load the class dynamically and then deserializes
            // the properties field into an instance of that class.
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        // stores the class name of the component
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        // Serializes the specific properties of the Component subclass.
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}