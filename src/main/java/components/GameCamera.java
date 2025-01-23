package components;

import engine.Camera;
import engine.GameObject;
import engine.KeyListener;
import engine.Window;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import renderer.BackgroundRender;
import renderer.Texture;
import util.AssetPool;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


// Here we create the camera that we see while playing the game.
// It moves to the right side if the character moves towards the right and moves left if the character moves towards the left.

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE; // Keep track of where our camera is in relation to the world
    private transient float undergroundYLevel = 0.0f; // Used to change the clear color when the player goes underground
    private transient float cameraBuffer = 1.5f; // Used to determine how far underground we should go for the underground level
    private transient float playerBuffer = 0.25f; // Player height to avoid the camera following the player when they die
    private transient int vertexID, fragmentID, shaderProgram;
    private transient int vaoID, vboID, eboID;

    private transient Texture backgroundTexture = AssetPool.getTexture("assets/images/background.png");

    private Vector4f skyColor = new Vector4f(92.0f / 255.0f, 148.0f / 255.0f, 252.0f / 255.0f, 1.0f);
    private Vector4f undergroundColor = new Vector4f(0, 0, 0, 1);
    private BackgroundRender backgroundShader = new BackgroundRender("assets/shaders/background.glsl");

    private int[] elementArray = {
            /*
                    x        x


                    x        x
             */
            2, 1, 0, // Top right triangle
            0, 1, 3 // bottom left triangle
    };

    private float[] vertexArray;

    public GameCamera(Camera gameCamera) {
        this.gameCamera = gameCamera;
    }

    // Gather all the necessary components that we need to make the camera move
    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class); // Player
        this.undergroundYLevel = this.gameCamera.position.y -
                this.gameCamera.getProjectionSize().y - this.cameraBuffer; // Determines where the underground level begins
        this.gameCamera.clearColor.set(skyColor);
    }

    public void BackgroundInit(){
        backgroundShader.compile();
        // ============================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ============================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        // Update the camera position based on the player's movement
        if (player != null && !player.getComponent(PlayerController.class).hasWon()) {
            if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
                gameCamera.position.x = Math.max(player.transform.position.x - 2.5f, highestX);
                highestX = Math.max(highestX, gameCamera.position.x);
            } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
                gameCamera.position.x = Math.min(player.transform.position.x - 2.5f, highestX);
                highestX = Math.min(highestX, gameCamera.position.x);
            }

            if (player.transform.position.y < -playerBuffer) {
                this.gameCamera.position.y = undergroundYLevel;
                this.gameCamera.clearColor.set(undergroundColor);
            } else if (player.transform.position.y >= 0.0f) {
                this.gameCamera.position.y = 0.0f;
                this.gameCamera.clearColor.set(skyColor); // Black for underground
            }
        }

        vertexArray = new float[]{
                // position               // color                  // UV Coordinates
                gameCamera.position.x + 6f, gameCamera.position.y + 3f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1, 1, // Bottom right
                gameCamera.position.x, gameCamera.position.y, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0, 0, // Top left
                gameCamera.position.x + 6f, gameCamera.position.y, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1, 0, // Top right
                gameCamera.position.x, gameCamera.position.y + 3f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0, 1  // Bottom left
        };


        BackgroundInit();

        backgroundShader.use();

        // Upload texture to shader
        backgroundShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        backgroundTexture.bind();

        backgroundShader.uploadMat4f("uProjection", this.gameCamera.getProjectionMatrix());
        backgroundShader.uploadMat4f("uView", this.gameCamera.getViewMatrix());
        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        backgroundShader.detach();



    }




}
