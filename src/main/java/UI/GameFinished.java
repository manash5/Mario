package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;

import engine.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

public class GameFinished extends JFrame implements ActionListener {
    JFrame frame;
    JButton scoreButton;
    JButton editButton;
    JButton restartButton;
    JButton ExitButton;



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
        Color transparentGreen = new Color(170,187,86, 80);
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

    private void setButtonStyle(JButton button, Color transparentGreen) {
        button.setOpaque(true);
        button.setFocusable(false);
        button.setBackground(transparentGreen);
        button.setForeground(Color.WHITE);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);

        // Remove default blue selection effect
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

//        // Ensure background doesn't change when pressed
        button.addChangeListener(e -> {
            if (!button.getModel().isPressed() && !button.getModel().isRollover()) {
                button.setBackground(new Color(170,187,86, 120));

            }
        });

        // Fully override default painting behavior to prevent background flashing
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(button.getBackground()); // Ensures background color consistency
                g2.fillRect(0, 0, c.getWidth(), c.getHeight()); // Paints the button's background
                super.update(g, c);
            }

            @Override
            protected void paintButtonPressed(Graphics g, AbstractButton b) {
                // Do nothing to prevent default highlight effect
                button.setContentAreaFilled(true);
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == scoreButton) {
            new LeadershipBoard(); // Assuming UI.LeadershipBoard is another class you created
            frame.setVisible(false);
            frame.dispose();
        }

        if (e.getSource() == editButton){
            frame.setVisible(false);
            frame.dispose();
        }

        if (e.getSource() == restartButton) {
            Window.restartGame();
            frame.setVisible(false);
            frame.dispose();
        }



        if (e.getSource() == ExitButton){
            new LoginUI();
            frame.setVisible(false);
            frame.dispose();
        }
    }

}
