package components;

import editor.PropertiesWindow;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

// Gizmos are the arrow that appears when we click the gameobject in the levelEditorScene. They are GameObjects in itself
// which appears in front of the activeGameobject

public class Gizmo extends Component{
    private Vector4f xAxisColor = new Vector4f(1,0.3f,0.3f,1);
    private Vector4f xAxisColorHover = new Vector4f(1,0,0,1);
    private Vector4f yAxisColor = new Vector4f(0.3f,1,0.3f, 1);
    private Vector4f yAxisColorHover = new Veseews90e9090e3--3vfcfcctor4f(0,1,0,1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    protected GameObject activeGameObject = null;

    private PropertiesWindow propertiesWindow;

    //These offsets are the positions where the gizmo arrows appear relative to the active game object.
    private Vector2f xAxisOffset = new Vector2f(24f/80f, -6f/80f);
    private Vector2f yAxisOffset = new Vector2f(-7f/80f,21f/80f);

    private float gizmoWidth = 16f/80f;
    private float gizmoHeight = 48f / 80f;

    // This means if we are dragging or not
    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;

    // constructor
    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        // creates the arrows using the funciton in the prefabs file
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth,gizmoHeight);
        // gets the spriteRenderer class of that object
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        // This is done so that we don't take this object as activeGameObject
        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);

    }

    @Override
    public void start(){
        // rotates the gizmos shape from the picture that we have used
        this.xAxisObject.transform.rotation = 90;
        this.yAxisObject.transform.rotation = 180;
        // Making sure that gizmos are front of everything we add
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        // This is done so that our gizmos won't get saved everytime we click them
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt){
        if (using){
            this.setInactive();
        }
        xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
        yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
    }

    @Override
    public void editorUpdate(float dt){
        if (!using) return;

        // gets the currently selected gameObject
        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if (this.activeGameObject != null){
            this.setActive();
        } else {
            this.setInactive();
            return;
        }

        // this means if we are hovering or not
        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        // Checks which gizmo is being hovered or clicked (x or y)
        if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)){
            yAxisActive = true;
            xAxisActive = false;
        } else {
            yAxisActive = false;
            xAxisActive = false;
        }

        // Aligns the gizmo's position with the active game object, plus the offsets.
        if (this.activeGameObject != null){
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
    }

    // if active set this color
    private void setActive(){
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    // if inactive set this color
    private void setInactive(){
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));

    }

    // checks if the x gizmos is being hovered or not
    private boolean checkXHoverState() {
        // gets the current mouse position
        Vector2f mousePos = MouseListener.getWorld();
        // if the cursor lies in the bounds of the gizmos arrow then set it's color to the hover color
        if (mousePos.x<=xAxisObject.transform.position.x + (gizmoHeight/2.0f) &&
                mousePos.x>=xAxisObject.transform.position.x-(gizmoWidth/2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight/2.0f) &&
                mousePos.y<= xAxisObject.transform.position.y + (gizmoWidth/2.0f)){
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }
        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    // checks if the y gizmos is being hovered or not
    private boolean checkYHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        // if the cursor lies in the bounds of the gizmos arrow then set it's color to the hover color
        if (mousePos.x<=yAxisObject.transform.position.x + (gizmoWidth/2.0f)&&
                mousePos.x>=yAxisObject.transform.position.x-(gizmoWidth/2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight/2.0f)&&
                mousePos.y>= yAxisObject.transform.position.y - (gizmoHeight/2.0f)){
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }
        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing(){
        this.using = true;
    }

    public void setNotUsing(){
        this.using = false;
        this.setInactive();
    }
}
