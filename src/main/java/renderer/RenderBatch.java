package renderer;
// here we render sprites in batches which has similar attribute, this way it makes it faster and easier to create sprites

import components.SpriteRenderer;
import engine.GameObject;
import engine.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

// this class has renders a group of sprite in a batch. this way in less time many sprites having similar properties like zIndex will be
// rendered together which will increase the performance speed of the game

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex
    // ======
    // Pos               Color                         tex coords     tex id
    // float, float,     float, float, float, float    float, float   float
    // these size, offset are for each vertex
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE =10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    private Renderer renderer;

    // Constructor
    public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer) {
        this.renderer = renderer;
        this.zIndex = zIndex;
//        shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    // Initializes all the stuff needed for rendering
    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        // This provides information to the GPU
        // In this array we store vertex cords
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        // here we store rgba colors
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        // here we store text cords
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        // here we have texture id
        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        // here we have entity id
        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    // This function is used for adding sprite to the current batches
    public void addSprite(SpriteRenderer spr) {
        // Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        // if the sprite has texture and the texture is not present in the texture list of the batch then it is added
        if (spr.getTexture() != null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        // sees if the batch has room to add another sprite
        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    // renders all the sprite in the batch
    public void render() {
        boolean rebufferData = false;
        for (int i=0; i < numSprites; i++) {
            SpriteRenderer spr = sprites[i];
            // here we check if the sprite is dirty or not
            // this means if there are any changes made to the sprite or not
            // this usually happens when we change the properties of the sprite like color, zIndex in the levelEditor imGui window
            if (spr.isDirty()) {
                // if the sprite has a texture that is not in the texture list of the batch then it is removed form the batch and adds it on
                // renderer to find another batch to add it in
                if (!hasTexture(spr.getTexture())){
                    this.renderer.destroyGameObject(spr.gameObject);
                    this.renderer.add(spr.gameObject);
                } else {
                    loadVertexProperties(i);
                    spr.setClean();
                    rebufferData = true;
                }
            }
            // if the sprite has a different zIndex than the zIndex in the batch then it removes it from the batch and sends to renderer
            // to add in another batch that has the similar properties
            if(spr.gameObject.transform.zIndex != this.zIndex){
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;

            }
        }
        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        for (int i=0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i=0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detach();
    }

    // checks the sprite then deletes and update the list of sprites
    public boolean destroyIfExists(GameObject go) {
        SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
        for (int i =0; i< numSprites; i++){
            if(sprites[i] == sprite){
                // [1,2,3,4,5,6]
                // [1,2,4,5,6]
                for (int j=i; j<numSprites-1; j++){
                    sprites[j] = sprites[j+1];
                    sprites[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }
        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        // total size of every sprites having pos, color, tex, texID/ Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        //only happens once for each sprite
        // usually texID =0 means no texture to avoid that we have added 1 with i when texture has been found
        int texId = 0;
        // if the sprite has a texture it finds the texture index position in the texture list and sets the id
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(sprite.getTexture())) {
                    texId = i + 1;
                    break;
                }
            }
        }

        // Checks if the object is rotated or not, if it is then it converts the rotated sprite position into the game world cords
        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated){
            transformMatrix.translate(sprite.gameObject.transform.position.x,
                    sprite.gameObject.transform.position.y, 0);
            transformMatrix.rotate((float)Math.toRadians(sprite.gameObject.transform.rotation), 0, 0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x, sprite.gameObject.transform.scale.y, 0);
        }

        // Add vertices with the appropriate properties
        //  Iterates over the 4 corners of the sprite and adjusts xAdd and yAdd to determine their relative positions:
        // (1,1) top right, (1,0) bottom right, (0,0) bottom left, (0,1) top left
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i=0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            Vector4f currentPos = new Vector4f(sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y), 0,1);
            if (isRotated){
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }

            // Load position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texture id
            vertices[offset + 8] = texId;

            // Load the entity id
            vertices[offset+9] = sprite.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    // This function gives the ebo data of every sprite 1,2,3,2,4,4,5,6,5,6,7,6,7,8
    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    //this function provides ebo data for one sprite 1,2,3,2,3,4
    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }


}