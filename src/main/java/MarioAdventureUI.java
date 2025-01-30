import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Database.MyJDBC;
import engine.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import scenes.LevelEditorSceneInitializer;
import scenes.LevelSceneInitializer;

public class MarioAdventureUI extends JFrame implements ActionListener {
    JFrame frame;
    JButton leaderboardButton;
    JButton editButton;
    JButton playButton;
    JButton deleteButton;
    Window window = Window.get();


    // Constructor for setting up the JFrame
    public MarioAdventureUI() {
        // Create a new JFrame
        frame = new JFrame("Super Mario World Adventure");

        // Set the JFrame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Make the frame fullscreen
        frame.setLayout(null); // Use absolute positioning

        // Load the background image
        ImageIcon backgroundImage = new ImageIcon("./assets/images/homepage.png");
        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImage.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        )));
        backgroundLabel.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);

        // Add the heading title at the top-right corner
        JLabel titleLabel = new JLabel("Super Mario World Adventure");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(169,11,12));
        titleLabel.setBounds(600, 220, 600, 50); // Adjust position and size as needed

        // Add buttons
        playButton = new JButton("PLAY GAME");
        editButton = new JButton("EDIT LEVEL");
        leaderboardButton = new JButton("LEADERBOARD");
        deleteButton = new JButton("Delete your account.");

        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        playButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        leaderboardButton.setFont(buttonFont);
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set button bounds
        playButton.setBounds(650, 400, 400, 60);
        editButton.setBounds(650, 480, 400, 60);
        leaderboardButton.setBounds(650, 560, 400, 60);
        deleteButton.setBounds(1500, 930, 200, 40);

        // Set button colors and transparency
        Color transparentGray = new Color(59, 56, 56, 120); // Transparent gray
        setButtonStyle(playButton, transparentGray);
        setButtonStyle(editButton, transparentGray);
        setButtonStyle(leaderboardButton, transparentGray);

        leaderboardButton.addActionListener(this);
        editButton.addActionListener(this);
        playButton.addActionListener(this);
        deleteButton.addActionListener(this);


        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(null);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setForeground(Color.RED);

        // Add components to the frame
        frame.add(titleLabel);
        frame.add(playButton);
        frame.add(editButton);
        frame.add(leaderboardButton);
        frame.add(deleteButton);
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
        if (e.getSource() == leaderboardButton) {
            new LeadershipBoard(); // Assuming LeadershipBoard is another class you created
        }

        if (e.getSource() == editButton){
            window.setDirectGame(false);
            window.run();
            frame.setVisible(false);
            frame.dispose();
        }

        if (e.getSource() == playButton) {
            window.setDirectGame(true);
            window.run();
            frame.setVisible(false);
            frame.dispose();
//            window.init();
//            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));

        }

        if (e.getSource() == deleteButton){
            MyJDBC jdbc = new MyJDBC();
            jdbc.deleteUser(Integer.parseInt(MyJDBC.getUserID()));
            JOptionPane.showMessageDialog(null, "Your Account has been deleted",
                    "Account Deleted",JOptionPane.INFORMATION_MESSAGE);
            new LoginUI();
            frame.setVisible(false);
            frame.dispose();
        }
    }

}
