package components;

import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

// This class basically is responsible to show frames in an animation process. Think of it like a copy that has all the drawing needed to show a
// certain animation. it refreshes all the page that has drawing and decides if to run all the drawing or not (setLoop)

public class AnimationState {

    public String title;
    public List<Frame> animationFrames = new ArrayList<>();

    private static Sprite defaultSprite = new Sprite();
    private float time = 0.0f;
    private transient int currentSprite = 0;
    private boolean doesLoop = false;

    // reloads the same texture in the sprite
    // this just refreshes the sprite
    public void refreshTextures() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }
    // adds new frame to the animationFrames list
    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }

    // adds multiple frames in the animation frames list
    public void addFrames(List<Sprite> sprites, float frameTime) {
        for (Sprite sprite : sprites) {
            this.animationFrames.add(new Frame(sprite, frameTime));
        }
    }

    // sets the loop to true or false
    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    // Updates the current frame based on the elapsed time (dt). If the animation is set to loop,
    // it cycles through the frames; otherwise, it stops at the last frame.
    public void update(float dt) {
        if (currentSprite < animationFrames.size()) {
            time -= dt;
            if (time <= 0) {
                if (!(currentSprite == animationFrames.size() - 1 && !doesLoop)) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                time = animationFrames.get(currentSprite).frameTime;
            }
        }
    }

    // returns the current sprite used in a frame
    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }

        return defaultSprite;
    }
}