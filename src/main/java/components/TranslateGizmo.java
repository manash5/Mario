package components;

import engine.MouseListener;

// This class is used to change the x and y position of the object when clicked on gizmos either on x axis or y axis
// gizmos are the arrows that appears when clicked on the object

public class TranslateGizmo extends Gizmo{

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow){
        super(arrowSprite, propertiesWindow);

    }

    // checks which arrows is active if it is x then converts it into the world space
    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null ){
            if (xAxisActive && !yAxisActive){
                activeGameObject.transform.position.x -= MouseListener.getScreenX();
            } else if (yAxisActive){
                activeGameObject.transform.position.y -= MouseListener.getScreenY();
            }
        }
        super.editorUpdate(dt);
    }
}
