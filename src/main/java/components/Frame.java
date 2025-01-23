package components;

// In this class, we assign each sprite a frame and decide how long should the frame last  
public class Frame {
    public Sprite sprite;
    public float frameTime;

    public Frame(){

    }

    public Frame(Sprite sprite, float time){
        this.sprite = sprite;
        this.frameTime = time;
    }
}
