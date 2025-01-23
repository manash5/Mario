package components;

import engine.KeyListener;
import engine.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

// This class is used to determine whether to use Gizmo translate or Gizmo scale based on the input that user has provided

public class GizmoSystem extends Component{
    private Spritesheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(Spritesheet gizmoSprites){
        gizmos = gizmoSprites;

    }

    // here at first we take both scale and translate gizmos properties
    @Override
    public void start(){
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1),
                Window.getImguiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2),
                Window.getImguiLayer().getPropertiesWindow()));
    }

    // here we check which state the gizmo is in if it is in 0 then we set translate gizmo to using and scale gizmo to notUsing and vice versa
    @Override
    public void editorUpdate(float dt){
        if (usingGizmo ==0){
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        } else if (usingGizmo ==1 ){
            gameObject.getComponent(TranslateGizmo.class).setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_E)){
            usingGizmo =0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)){
            usingGizmo =1;
        }
    }
}
