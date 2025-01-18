package renderer;

import static org.lwjgl.opengl.GL30.*;

// This class generates the frame buffer where you will show your game. we usually render in frames so that we can have flexibility
// and make changes. Think of it like drawing with pencil you can undo your mistakes unlike with pen you can't undo your changes

public class Framebuffer {
    private int fboID = 0;
    private Texture texture = null;

    public Framebuffer(int width, int height){
        // Generate framebuffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        // create the texture to render the data to, and attach it to our framebuffer
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);

        // Create render buffer to store the depth info
        // A render buffer is created to store depth information (e.g., how far away objects are from the camera).
        // this is just used to store information so that our rendering can work faster
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        // Checks for error
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false: "Error: Framebuffer is not complete";
        }

        // unbinds the frame buffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFboID() {
        return fboID;
    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getTextureId() {
        return texture.getId();
    }

}
