package editor;

import imgui.ImGui;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

// this file IS the menu bar that shows at the top left side of the game viewport saying save and load

public class MenuBar {

    public void imgui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Ctrl+S")) {
                // notifies the event of the new event type when clicked on the button or use ctrl+s
                EventSystem.notify(null, new Event(EventType.SaveLevel));
            }

            if (ImGui.menuItem("Load", "Ctrl+O")) {
                // notifies the event of the new event type when clicked on the button or use ctrl+0
                EventSystem.notify(null, new Event(EventType.LoadLevel));
            }

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}