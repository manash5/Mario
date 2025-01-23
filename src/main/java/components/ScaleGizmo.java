package components;

import editor.PropertiesWindow;
import engine.MouseListener;

// This class is used to scale (make bigger or smaller)  the x and y position of the object when clicked on gizmos either on x axis or y axis
// gizmos are the arrows that appears when clicked on the object

public class ScaleGizmo extends Gizmo {
    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow){
        super(scaleSprite, propertiesWindow);

    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null ){
            if (xAxisActive && !yAxisActive){
                activeGameObject.transform.scale.x -= MouseListener.getWorldX();
            } else if (yAxisActive){
                activeGameObject.transform.scale.y -= MouseListener.getWorldY();
            }
        }
        super.editorUpdate(dt);
    }
}
