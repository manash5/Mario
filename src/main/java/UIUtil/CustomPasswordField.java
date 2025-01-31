package UIUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Custom Password Field that starts as Text, then hides input
public class CustomPasswordField extends JPasswordField {
    private boolean showingText = true;
    private String placeholder;

    public CustomPasswordField(String text) {
        super(text);
        placeholder = text;
        setOpaque(false);
        setBorder(new EmptyBorder(15, 20, 15, 20)); // Adjusted padding for larger screen
        setFont(new Font("Serif", Font.ITALIC, 18)); // Adjusted font size
        setEchoChar((char) 0); // Initially text is visible

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (showingText) {
                    setText("");
                    setEchoChar('*'); // Hide text when user starts typing
                    showingText = false;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Increased roundness for larger elements
        super.paintComponent(g);
    }
}