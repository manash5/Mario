package UIUtil;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String filePath) {
        try {
            ImageIcon bgIcon = new ImageIcon(filePath);
            backgroundImage = bgIcon.getImage();
        } catch (Exception e) {
            System.err.println("Error loading image: " + filePath);
            e.printStackTrace();
            setBackground(Color.LIGHT_GRAY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}