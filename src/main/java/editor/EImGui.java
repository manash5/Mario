package editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector4f;

// This class makes the userInterface of components that are used in imgui
// It has default UI made inside of it so that we don't have to replicate the same basic functions in all other componets
// here we make the basic imgui components to reduce code redundancy like the checkbox area, input area, color picker area

public class EImGui {
    private static float defaultColumnWidth = 220.0f;

    // these two function ultimately calls the third function that takes four arguments
    public static void drawVec2Control(String label, Vector2f values){
        drawVec2Control(label, values, 0.0f, defaultColumnWidth);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue){
        drawVec2Control(label, values, resetValue, defaultColumnWidth);

    }

    // This function will basically be of three column where in the first column is the label and in the second column
    // half the space is occupied by x button and remaining half shows the value/ position in x-axis
    // and in the third colum, we have y button in the half area and remaining area covers the position in y-axis
    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth){
        ImGui.pushID(label);

        // sets up the column info
        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        // keeps the name in the first column
        ImGui.text(label);
        // moves on to the next column
        ImGui.nextColumn();

        // Temporarily removes spacing between UI elements to make the layout compact.
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        //Calculates the button height based on the font size and padding.
        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        //  Determines the size of the buttons (X and Y).
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
        // Calculates how much space to allocate for each slider, ensuring they fit alongside the buttons.
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

        // X Button color when hovered, clicked and default
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);

        // if clicked on x, it resets it's value
        if (ImGui.button( "X", buttonSize.x, buttonSize.y)){
            values.x = resetValue;
        }
        ImGui.popStyleColor(3); // shows the third color that we added previously
        ImGui.sameLine();

        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##x", vecValuesX, 0.1f); // Creates a slider to adjust the x value. Modifies the value with a sensitivity of 0.1.
        ImGui.popItemWidth();
        ImGui.sameLine();

        // Y Button
        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        // if clicked on Y, it resets it's value
        if (ImGui.button( "Y", buttonSize.x, buttonSize.y)){
            values.y = resetValue;
        }
        ImGui.popStyleColor(3);
        ImGui.sameLine();

        // We create an array float[] vecValuesY = {values.y}; because ImGui's drag controls (dragFloat)
        // require a mutable array to modify the value.
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();
        ImGui.nextColumn();

        //Updates the values object with the modified x and y values from the sliders.
        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

        ImGui.columns(1);
        ImGui.popStyleVar();
        ImGui.popID();
    }

    // this function makes two columns where the first column contains the label/name and in the second
    // it contains the value in float
    public static float dragFloat(String label, float value){
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] valArr = {value};
        ImGui.dragFloat("##dragFloat", valArr, 0.1f); // creates a slider to drag the value on 0.1 sensitivity

        ImGui.columns(1);
        ImGui.popID();

        return valArr[0];
    }

    // This function does the same as dragFloat function but it stores the value in int
    public static int dragInt(String label, int value){
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        int[] valArr = {value};
        ImGui.dragInt("##dragFloat", valArr, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valArr[0];
    }

    // this function makes two columns where at the first column is the label or name
    // and in the second is the color picker with four values
    public static boolean colorPicker4(String label, Vector4f color){
        boolean res = false;
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorEdit4("##colorPicker", imColor)){
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            res = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return res;
    }

    // this function makes two column where at the first is the label/name and in the second is the area to input
    // the text
    public static String inputText(String label, String text){
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImString outString = new ImString(text, 256);
        if (ImGui.inputText("##" + label, outString)){
            ImGui.columns(1);
            ImGui.popID();

            return outString.get();
        }

        ImGui.columns(1);
        ImGui.popID();

        return text;
    }




}
