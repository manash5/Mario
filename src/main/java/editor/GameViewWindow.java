package editor;

import engine.MouseListener;
import engine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

// This class is responsible for rendering and managing a game viewport
// within a graphical user interface (GUI) using the ImGui library.

public class GameViewWindow {

    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;

    // viewport where we see our game in the level editor scene
    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse |
                        ImGuiWindowFlags.MenuBar);

        // When play is clicked stop is not available and vice versa
        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }

        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)){
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }

        ImGui.endMenuBar();

        // sets the cursor position
        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        // stores the coords
        leftX = windowPos.x + 10;
        bottomY = windowPos.y;
        rightX = windowPos.x + windowSize.x + 10;
        topY = windowPos.y + windowSize.y;

        // gets the textureID of the sprite using framebuffer to render it onto the view port
        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        // updates the MouseListener with the position and size of the viewport
        MouseListener.setGameViewportPos(new Vector2f(windowPos.x+10, windowPos.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    // it determines whether the mouseEvent should be captured by imGui or not
    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

    // Calculates the screensize of the viewport while maintaining the aspect ratio.
    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // Calculates the aspect width and height and checks if the calculated height does not fit the height or not
        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    // Calculates the position to center the view
    // port in the available space.
    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // Calculates the horizontal (viewportX) and vertical (viewportY) offsets needed to center the viewport.
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }
}