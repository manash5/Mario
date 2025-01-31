package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import engine.Window;

public class GameFinished extends JFrame implements ActionListener {
    JFrame frame;
    JButton scoreButton;
    JButton editButton;
    JButton restartButton;
    JButton ExitButton;
    Window window = Window.get();


    // Constructor for setting up the JFrame
    public GameFinished() {
        // Create a new JFrame
        frame = new JFrame("Mario Dai");

        // Set the JFrame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the frame fullscreen
        frame.setLayout(null); // Use absolute positioning

        // Load the background image
        ImageIcon backgroundImage = new ImageIcon("./assets/images/gameFinished.png");
        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        )));
        backgroundLabel.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);

        // Add the heading title at the top-right corner
        JLabel titleLabel = new JLabel("Mario Dai");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(169,11,12));
        titleLabel.setBounds(400, 220, 600, 50); // Adjust position and size as needed

        // Add buttons
        restartButton = new JButton("RESTART");
        editButton = new JButton("EDIT LEVEL");
        scoreButton = new JButton("SCORE");
        ExitButton = new JButton("EXIT");

        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        restartButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        scoreButton.setFont(buttonFont);
        ExitButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Set button bounds
        restartButton.setBounds(300, 300, 400, 80);
        editButton.setBounds(300, 400, 400, 80);
        scoreButton.setBounds(300, 500, 400, 80);
        ExitButton.setBounds(1450, 930, 200, 40);

        // Set button colors and transparency
        Color transparentGreen = new Color(170,187,86); // Transparent gray
        setButtonStyle(restartButton, transparentGreen);
        setButtonStyle(editButton, transparentGreen);
        setButtonStyle(scoreButton, transparentGreen);

        scoreButton.addActionListener(this);
        editButton.addActionListener(this);
        restartButton.addActionListener(this);
        ExitButton.addActionListener(this);

        ExitButton.setForeground(Color.WHITE);
        ExitButton.setBackground(new Color(130,67,47));

        // Add components to the frame
        frame.add(titleLabel);
        frame.add(restartButton);
        frame.add(editButton);
        frame.add(scoreButton);
        frame.add(ExitButton);
        frame.add(backgroundLabel);

        // Make the frame visible
        frame.setVisible(true);
    }

    private void setButtonStyle(JButton button, Color transparentGray) {
        button.setOpaque(true);
        button.setBackground(transparentGray);
        button.setForeground(Color.WHITE);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        button.addChangeListener(e -> button.setBackground(transparentGray)); // Prevent color changes
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == scoreButton) {
            new LeadershipBoard(); // Assuming UI.LeadershipBoard is another class you created
            frame.setVisible(false);
            frame.dispose();
        }

        if (e.getSource() == editButton){
            Window.setDirectGame(false);
            window.run();
            frame.setVisible(false);
            frame.dispose();
        }

        if (e.getSource() == restartButton) {
            Window.setDirectGame(true);
            window.run();
            frame.setVisible(false);
            frame.dispose();
//            window.init();
//            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));

        }

        if (e.getSource() == ExitButton){
            new LoginUI();
            frame.setVisible(false);
            frame.dispose();
        }
    }

}
