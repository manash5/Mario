package components;

import engine.Camera;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

// This is used to create the lines that we see in the levelEditor class

public class GridLines extends Component{

    @Override


    public void editorUpdate(float dt){
        Camera camera = Window.getScene().camera();

        // get the camera position to draw line from that position
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        // First line
        float firstX = ((int)(cameraPos.x/ Settings.GRID_WIDTH)) * Settings.GRID_WIDTH;
        float firstY = ((int)(cameraPos.y/ Settings.GRID_HEIGHT)) * Settings.GRID_HEIGHT;

        // no. of lines that can fit
        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) +2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        // total height and width of the projection screen
        float height = (int)(projectionSize.y * camera.getZoom()) + (5*Settings.GRID_HEIGHT);
        float width = (int)(projectionSize.x * camera.getZoom()) + ( 5 *Settings.GRID_WIDTH);
        int maxLines = Math.max(numVtLines,numHzLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        // loops through and create each line
        for (int i=0; i< maxLines; i++){
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            // checks if all vertical line is drawn or not
            if (i< numVtLines){
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY+height), color);
            }

            // checks if all horizontal line is drawn or not
            if (i< numHzLines){
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX+width, y), color);
            }
        }
    }
}
