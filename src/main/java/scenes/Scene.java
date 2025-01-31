    package scenes;

    import com.google.gson.Gson;
    import com.google.gson.GsonBuilder;
    import components.Component;
    import components.ComponentDeserializer;
    import engine.Camera;
    import engine.GameObject;
    import engine.GameObjectDeserializer;
    import engine.Transform;
    import org.joml.Vector2f;
    import physics2d.Physics2D;
    import renderer.Renderer;

    import java.io.FileWriter;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    // This class is like the backbone of the game engine.
    // It manages the game world or level that the player interacts with.
    // Its primary responsibilities include managing game objects, handling rendering, physics updates,
    // and interacting with LevelEditorSceneInitializer

    public class Scene {

        private Renderer renderer;
        private Camera camera;
        private boolean isRunning;
        private List<GameObject> gameObjects;
        private List<GameObject> pendingObjects;

        private SceneInitializer sceneInitializer;
        private Physics2D physics2D;
        private int coinCounter;

        // Constructor
        public Scene(SceneInitializer sceneInitializer) {
            this.sceneInitializer = sceneInitializer;
            this.physics2D = new Physics2D();
            this.renderer = new Renderer();
            this.gameObjects = new ArrayList<>();
            this.pendingObjects = new ArrayList<>();
            this.isRunning = false;
            this.coinCounter = 0;

        }

        // returns the physics class that we are using
        public Physics2D getPhysics(){
            return this.physics2D;
        }

        // Sets up the camera, loads resources, and initializes the scene.
        public void init() {
            this.camera = new Camera(new Vector2f(0, 0));
            this.sceneInitializer.loadResources(this);
            this.sceneInitializer.init(this);
        }

        // This method initializes all the game objects in the scene
        // Before rendering, game objects need to be prepared (e.g., loading their textures, models, or behaviors).
        public void start() {
            for (int i = 0; i< gameObjects.size(); i++) {
                GameObject go = gameObjects.get(i);
                go.start();
                this.renderer.add(go);
                this.physics2D.add(go);
            }
            isRunning = true;
        }

        // adds game object to the scene
        public void addGameObjectToScene(GameObject go) {
            if (!isRunning) {
                gameObjects.add(go);
            } else {
                // adds the object and initializes them
                pendingObjects.add(go);
            }
        }

        // removes all the gameObject from the screen
        public void destroy(){
            //goes through all the objects present in the scene and destroys them
            for (GameObject go: gameObjects){
                go.destroy();
            }
        }

        public <T extends Component> GameObject getGameObjectWith(Class<T> clazz) {
            for (GameObject go : gameObjects) {
                if (go.getComponent(clazz) != null) {
                    return go;
                }
            }

            return null;
        }

        // returns the list of gameObject
        public List<GameObject> getGameObjects(){
            return this.gameObjects;
        }

        // returns the gameObject with a specific id in the scene
        public GameObject getGameObject(int gameObjectId){
            // This is just like when we use loop to find the gameobject with a specific  id
            // it says streamline all the gameObject and filter does who have the gameObjectId
            // and return the first one
            Optional<GameObject> result = this.gameObjects.stream()
                    .filter(gameObject -> gameObject.getUid() == gameObjectId)
                    .findFirst();

            return result.orElse(null);
        }

        public GameObject getGameObject(String gameObjectName){
            // This is just like when we use loop to find the gameobject with a specific  id
            // it says streamline all the gameObject and filter does who have the gameObjectId
            // and return the first one
            Optional<GameObject> result = this.gameObjects.stream()
                    .filter(gameObject -> gameObject.name.equals(gameObjectName))
                    .findFirst();

            return result.orElse(null);
        }

        // this is the update that we use while editing
        // This is the place where we make real time changes
        public void editorUpdate(float dt){
            // Gives out the projection that we want (orthographic)
            this.camera.adjustProjection();

            for (int i = 0; i< gameObjects.size(); i++) {
                GameObject go = gameObjects.get(i);
                go.editorUpdate(dt);//update all objects in scene but don't call imgui for all of them
                //It is only called for the active object

                // removing gameObject when not needed
                if (go.isDead()){
                    gameObjects.remove(i);
                    this.renderer.destroyGameObject(go);
                    this.physics2D.destroyGameObject(go);
                    i--; // since we are goind forwards and removing object some might get skipped so that's why we use i--
                }

                //All game objects in the scene are updated each frame (e.g., animations, physics, or other logic).
                //However, the ImGui GUI is only invoked for the active game object (managed in the sceneImgui method from Scene).
                //This separation ensures the performance isn't affected by unnecessary GUI updates for non-active objects.
            }
            for (GameObject go: pendingObjects){
                gameObjects.add(go);
                go.start();
                this.renderer.add(go);
                this.physics2D.add(go);
            }
            pendingObjects.clear();
        }

        public void update(float dt){
            this.camera.adjustProjection();
            this.physics2D.update(dt);

            for (int i = 0; i< gameObjects.size(); i++) {
                GameObject go = gameObjects.get(i);
                go.update(dt);//update all objects in scene but don't call imgui for all of them
                //It is only called for the active object

                // removing gameObject when not needed
                if (go.isDead()){
                    gameObjects.remove(i);
                    this.renderer.destroyGameObject(go);
                    this.physics2D.destroyGameObject(go);
                    i--; // since we are goind forwards and removing object some might get skipped so that's why we use i--
                }

                //All game objects in the scene are updated each frame (e.g., animations, physics, or other logic).
                //However, the ImGui GUI is only invoked for the active game object (managed in the sceneImgui method from Scene).
                //This separation ensures the performance isn't affected by unnecessary GUI updates for non-active objects.
            }
            for (GameObject go: pendingObjects){
                gameObjects.add(go);
                go.start();
                this.renderer.add(go);
                this.physics2D.add(go);
            }
            pendingObjects.clear();
        }

        // Renders the current scene using the renderer
        public void render(){
            this.renderer.render();
        }

        // returns the camera used in scene
        public Camera camera() {
            return this.camera;
        }

        // calls the imgui of the sceneInitializer class
        public void imgui() {
            this.sceneInitializer.imgui();
        }

        // creates a new gameObject and add its transform as a componetn
        public GameObject createGameObject(String name){
            GameObject go = new GameObject(name);
            go.addComponent(new Transform());
            go.transform = go.getComponent(Transform.class);
            return go;
        }

        // Serialization and Deserialization
        // Gson doesnot know how to serialize and deserialize complex objects like gameObject and component
        // so we custom make them and add them which helps to serialize and deserialize them
        // serializes the current state of all GameObjects in the scene (including their components) into a JSON file called level.txt
        public void save() {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Component.class, new ComponentDeserializer())
                    .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                    .enableComplexMapKeySerialization()
                    .create();

            try {
                FileWriter writer = new FileWriter("level.txt");
                // Creates an array that adds the objects that needs to be serialized
                List<GameObject> objsToSerialize = new ArrayList<>();
                for(GameObject obj: this.gameObjects){
                    // doSerialization is a function that returns if the object should be serialized or not
                    if (obj.doSerialization()){
                        objsToSerialize.add(obj);
                    }
                }
                // filter game Objects are serialized into JSON string and written in the writer file which is the level.txt
                writer.write(gson.toJson(objsToSerialize));
                writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        // Reads the Json from level.txt and reconstructs the gameObjects and their components back to the memory
        public void load() {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Component.class, new ComponentDeserializer())
                    .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                    .enableComplexMapKeySerialization()
                    .create();

            String inFile = "";
            try {
                // reads all the contents of the level.txt file
                inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Ensures the file is not empty
            if (!inFile.equals("")) {
                int maxGoId = -1;
                int maxCompId = -1;
                // deserializes the JSON string into an array of GameObject instances
                // this means it converts the json string back to the gameObject class
                GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
                // adds the gameObjects to the scene
                for (int i=0; i < objs.length; i++) {
                    addGameObjectToScene(objs[i]);
                    // updatas the maxCompId
                    // checks all the uids of the components present so that the new component which might be
                    // added can have a value greater than this to maintain a unique id for each individual component
                    for(Component c: objs[i].getAllComponents()){
                        if (c.getUid()> maxCompId){
                            maxCompId = c.getUid();
                        }
                    }
                    // updates the maxGoId
                    if (objs[i].getUid() > maxGoId){
                        maxGoId = objs[i].getUid();
                    }
                }
                // updates both the maxGoId and maxCompId
                maxGoId++;
                maxCompId++;
    //            System.out.println(maxGoId);
    //            System.out.println(maxCompId);
                // Assign unique ids
                GameObject.init(maxGoId);
                Component.init(maxCompId);
            }
        }

        public int getCoinCounter(){
            return this.coinCounter;
        }

        public void incrementCoinCounter(){
            this.coinCounter+= 1;
            System.out.println("In scenes the coin counter is " + this.coinCounter);
        }
    }