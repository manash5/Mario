package renderer;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL30.*;

// this class is used to detect what we touch/ click in the game. it tells which us which game object is clicked
// it gives unique color code to each object (color is not of the object just a random color) to identity every time it's clicked
// we specifically used color so that our GPU can process it quickly as our GPU's are more efficient with colors and images rather than
// numbers and values

// We render stuff in frame because it gives us flexibility of control
// Directly drawing in the screen is like drawing in a paper with pen, you can't undo changes, without frame once you have to make
// changes in the scene you have to redraw everything but in frame you can just change the object that you want while all being the same

public class PickingTexture {
    private int pickingTextureId;
    private int fbo;
    private int depthTexture;

    public PickingTexture(int width, int height){
        if (!init(width, height)){
            assert false: "Error initializing picking texture";
        }
    }

    public boolean init(int width, int height){
        // Generate FrameBuffer
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // Create the texture to render the data to, and attach it to our framebuffer
        pickingTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // This defines the texture's properties and allocates memory for it
        glTexImage2D(GL_TEXTURE_2D, 0 , GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        // Attach texture to framebuffer
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.pickingTextureId, 0);

        // Create the texture object for the depth buffer
        glEnable(GL_TEXTURE_2D);
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);

        // Disable the reading
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false: "Error: Framebuffer is not complete";
            return false;
        }

        // Unbind the texture and framebuffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
         return true;
    }

    // enables the frame buffer
    public void enableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    // disables the frame buffer
    public void disableWriting(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    // Looks at the color at a specific spot (x, y) on the framebuffer and figures out which object it represents.
    public int readPixel(int x, int y){
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        // Reads the color of a single pixel at (x, y) from the picking texture.
        //Stores the RGB values in the pixels array.
        float pixels[] = new float[3];
        glReadPixels(x,y,1,1, GL_RGB, GL_FLOAT, pixels);

        // Converts the red color value to an object ID.
        return (int)(pixels[0]) -1 ;
    }

    public float[] readPixels(Vector2i start, Vector2i end){
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        // finds the size of the rectangle
        Vector2i size = new Vector2i(end).sub(start).absolute();
        int numPixels = size.x * size.y;
        float pixels[] = new float[3 * numPixels];
        //Reads the RGB values of all pixels in the rectangle.
        glReadPixels(start.x,start.y,size.x, size.y, GL_RGB, GL_FLOAT, pixels);
        // Each pixel's red value is adjusted by subtracting 1
        // we subtract because there is usually a offset of 1 since 0 represents the default background color
        for (int i =0; i< pixels.length; i++){
            pixels[i] -= 1;
        }

        return pixels;
    }
}
