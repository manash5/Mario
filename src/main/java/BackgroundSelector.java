import UIUtil.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class BackgroundSelector extends JFrame implements ActionListener {
    private JFrame frame;
    private JLabel imageLabel;
    private JButton selectButton;
    private JButton backButton;
    private int currentIndex = 0;
    private String imageAddress = "./assets/images/background.png";
    private Map<Integer, Boolean> selectionMap = new HashMap<>();

    private final String[] imagePaths = {
            "./assets/images/background.png",
            "./assets/images/background.jpg",
            "./assets/images/mountain_2.jpg",
            "./assets/images/Mountain.jpg"
    };

    public BackgroundSelector()  {
        frame = new JFrame("Background Selector");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(10, 10, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(10, 10, 20));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(100, 30, 30, 20));

        JLabel titleLabel = new JLabel("Select Background", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Select Button
        selectButton = new JButton("Select");
        selectButton.setFont(new Font("Serif", Font.BOLD, 20));
        selectButton.setForeground(Color.BLACK);
        selectButton.setBackground(new Color(200, 80, 50));
        selectButton.setFocusPainted(false);
        selectButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 80, 50), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30) // Increased padding
        ));
        selectButton.addActionListener(e -> toggleSelection());
        selectButton.addActionListener(this);
        titlePanel.add(selectButton, BorderLayout.EAST);

        // Select Button
        backButton = new JButton("");
        ImageIcon icon = new ImageIcon("./assets/images/left.png");
        Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        backButton.setIcon(new ImageIcon(img));
        backButton.setBackground(new Color(200, 80, 50));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 80, 50), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        backButton.addActionListener(this);
        titlePanel.add(backButton, BorderLayout.WEST);

        frame.add(titlePanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(10, 10, 20));
        centerPanel.setPreferredSize(new Dimension(1200, 1200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 100, 10, 100);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 80, 50), 3));
        updateImage();

        centerPanel.add(imageLabel, gbc);

        // Arrow Buttons
        JButton leftButton = new JButton("←");
        JButton rightButton = new JButton("→");
        leftButton.setContentAreaFilled(false);
        rightButton.setContentAreaFilled(false);

        styleButton(leftButton);
        styleButton(rightButton);

        leftButton.addActionListener(e -> prevImage());
        rightButton.addActionListener(e -> nextImage());

        // Left Button
        gbc.gridx = 0;
        centerPanel.add(leftButton, gbc);

        // Right Button
        gbc.gridx = 2;
        centerPanel.add(rightButton, gbc);

        frame.add(centerPanel, BorderLayout.CENTER);

        // Automatically select the first image
        selectionMap.put(0, true);
        updateSelectButton();

        frame.setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Serif", Font.BOLD, 24));
        button.setForeground(new Color(200, 80, 50));
        button.setBackground(new Color(10, 10, 20));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }

    private void updateImage() {
        ImageIcon icon = new ImageIcon(imagePaths[currentIndex]);

        if (icon.getIconWidth() == -1) {
            imageLabel.setText("Image Not Found");
            imageLabel.setIcon(null);
            return;
        }

        Image img = icon.getImage().getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
        imageLabel.setText("");
        updateSelectButton();
    }

    private void toggleSelection() {
        selectionMap.clear(); // Ensure only one selection at a time
        selectionMap.put(currentIndex, true);
        updateSelectButton();
    }

    private void updateSelectButton() {
        if (selectionMap.getOrDefault(currentIndex, false)) {
            selectButton.setText("Selected");
            selectButton.setBackground(new Color(10, 10, 20));
            selectButton.setForeground(new Color(200, 80, 50));
        } else {
            selectButton.setText("Select");
            selectButton.setBackground(new Color(200, 80, 50));
            selectButton.setForeground(Color.BLACK);
        }
    }

    private void nextImage() {
        currentIndex = (currentIndex + 1) % imagePaths.length;
        updateImage();
    }

    private void prevImage() {
        currentIndex = (currentIndex - 1 + imagePaths.length) % imagePaths.length;
        updateImage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BackgroundSelector::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==backButton){
            new MarioAdventureUI();
            frame.setVisible(false);
            frame.dispose();
        }

        if (e.getSource() == selectButton){
            imageAddress = imagePaths[currentIndex];
        }
    }
}
