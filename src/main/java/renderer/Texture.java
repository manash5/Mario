package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

// This class is responsible for generating and configuring textures (image) and loading it

public class Texture {
    // The width and height represent the dimensions (in pixels) of the texture (image) being loaded or generated.
    private String filepath;
    private transient int texID;
    private int width, height;

    public Texture(){
        texID = -1;
        width = -1;
        height = -1;
    }

    // Constructor
    public Texture(int width, int height){
        this.filepath = "Generated";

        // Generate texture on GPU
        texID = glGenTextures(); // generates the text ID
        glBindTexture(GL_TEXTURE_2D, texID);

        // sets the parameter for minimum and maximum value
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    // initialization
    public void init(String filepath) {
        this.filepath = filepath;

        // Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);


        //set texture parameters
        //repeat image in both directions
        // S is height and T is width (maybe opposite)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking an image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Creates buffers to store the width, height, and number of color channels of the image.
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        //channels refers to the number of color channels in the image file.
        // This tells us how the image stores color information for each pixel:
        // if channel has 3 values then it is r,g,b if has 4 then it is r, g, b, a
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            // When data are in buffer we use get(0)
            this.width = width.get(0);
            this.height = height.get(0);

            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channesl '" + channels.get(0) + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }

        // remove image after it is uploaded
        stbi_image_free(image);
    }

    // starts the texture binding
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    // ends the texture binding
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    // gets the width of the texture
    public int getWidth() {
        return this.width;
    }

    // gets the height of the texture
    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public  void setWidth(int width){
        this.width = width;
    }

    // returns the file path
    public String getFilepath(){
        return this.filepath;
    }

    // returns the id
    public int getId() {
        return texID;
    }

    // Checks if the two objects are equal or not and returns boolean value
    @Override
    public boolean equals(Object o){
        if (o== null) return false;
        if (!(o instanceof Texture)) return false;
        Texture oTex = (Texture)o;
        return oTex.getWidth() == this.width && oTex.getHeight() == this.height && oTex.getId() == this.texID &&
                oTex.getFilepath().equals(this.filepath);
    }

}