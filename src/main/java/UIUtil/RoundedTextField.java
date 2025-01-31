package UIUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RoundedTextField extends JTextField {
    private boolean showingText = true;
    private String placeholder;

    public RoundedTextField(String text) {
        super(text);
        placeholder = text;
        setOpaque(false);
        setBorder(new EmptyBorder(15, 20, 15, 20)); // Adjusted padding for larger screen
        setFont(new Font("Serif", Font.ITALIC, 18)); // Adjusted font size
        setText(placeholder);

        // Add focus listener to hide placeholder on click
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingText) {
                    setText("");
                    showingText = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    showingText = true;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        super.paintComponent(g);
    }
}