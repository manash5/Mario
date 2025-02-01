package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Database.MyJDBC;
import engine.Window;

public class MarioAdventureUI extends JFrame implements ActionListener {
    JFrame frame;
    JButton leaderboardButton;
    JButton editButton;
    JButton playButton;
    JButton backgroundButton;
    JButton deleteButton;
    JButton logoutButton;
    Window window = Window.get();


    // Constructor for setting up the JFrame
    public MarioAdventureUI() {
        // Create a new JFrame
        frame = new JFrame("MarioDai");

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
        JLabel titleLabel = new JLabel("Mario Dai");
        titleLabel.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 66));
        titleLabel.setForeground(new Color(169,11,12));
        titleLabel.setBounds(650, 250, 600, 50); // Adjust position and size as needed

        // Add buttons
        playButton = new JButton("PLAY GAME");
        editButton = new JButton("EDIT LEVEL");
        leaderboardButton = new JButton("LEADERBOARD");
        backgroundButton = new JButton("Change Background");
        logoutButton = new JButton("LogOut");
        deleteButton = new JButton("Delete your account.");

        // Style buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        playButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        leaderboardButton.setFont(buttonFont);
        backgroundButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set button bounds
        playButton.setBounds(650, 400, 400, 60);
        editButton.setBounds(650, 480, 400, 60);
        leaderboardButton.setBounds(650, 560, 400, 60);
        backgroundButton.setBounds(650, 640, 400, 60);
        logoutButton.setBounds(650,720, 400,60);
        deleteButton.setBounds(1500, 930, 200, 40);

        // Set button colors and transparency
        Color transparentGray = new Color(59, 56, 56, 80); // Transparent gray
        setButtonStyle(playButton, transparentGray);
        setButtonStyle(editButton, transparentGray);
        setButtonStyle(leaderboardButton, transparentGray);
        setButtonStyle(backgroundButton, transparentGray);
        setButtonStyle(logoutButton, new Color(144,2,8,80));

        leaderboardButton.addActionListener(this);
        editButton.addActionListener(this);
        playButton.addActionListener(this);
        backgroundButton.addActionListener(this);
        logoutButton.addActionListener(this);
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
        frame.add(backgroundButton);
        frame.add(logoutButton);
        frame.add(deleteButton);
        frame.add(backgroundLabel);

        // Make the frame visible
        frame.setVisible(true);
    }

    private void setButtonStyle(JButton button, Color transparentGray) {
        button.setOpaque(true);
        button.setFocusable(false);
        button.setBackground(transparentGray);
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
                if (button.getText().equalsIgnoreCase("logout")){
                    button.setBackground(new Color(144,2,8,120));
                } else {
                    button.setBackground(new Color(59, 56, 56, 120));
                }

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
        if (e.getSource() == leaderboardButton) {
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

        if (e.getSource() == playButton) {
            Window.setDirectGame(true);
            window.run();
            frame.setVisible(false);
            frame.dispose();
//            window.init();
//            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));

        }

        if (e.getSource()==backgroundButton){
            frame.setVisible(false);
            frame.dispose();
            new BackgroundSelector();
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

        if (e.getSource()==logoutButton){
            new LoginUI();
            frame.setVisible(false);
            frame.dispose();
        }
    }

}
