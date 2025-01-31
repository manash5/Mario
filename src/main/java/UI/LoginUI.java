package UI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Database.MyJDBC;
import UIUtil.CustomPasswordField;
import UIUtil.RoundedButton;
import UIUtil.RoundedTextField;

public class LoginUI extends JFrame implements ActionListener {
    JFrame frame;
    RoundedButton enterButton;
    JButton createAccountButton, forgotPassword;
    RoundedTextField usernameField;
    CustomPasswordField passwordField;
    public LoginUI() {
        // Create frame
        frame = new JFrame("Login");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Background color
        frame.getContentPane().setBackground(new Color(10, 15, 25));

        // Center panel for login elements
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(frame.getWidth(), frame.getHeight(), 1500, 850);
        panel.setBackground(new Color(10, 15, 25));
        System.out.println(frame.getWidth());
        System.out.println(frame.getHeight());
        frame.add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 44));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(350, 250, 180, 60);
        panel.add(titleLabel);

        // making the borders round
        Border roundedBorder = new CompoundBorder(
                new LineBorder(Color.WHITE, 2, true),
                new EmptyBorder(10, 15, 10, 15)
        );
        Border roundedBorder1 = new CompoundBorder(
                new LineBorder(new Color(25, 50, 100), 2, true),
                new EmptyBorder(10, 15, 10, 15)
        );

        // Username Field
        usernameField = new RoundedTextField("Username");
        usernameField.setBounds(250, 400, 400, 50);
        usernameField.setBorder(null);
        usernameField.setBackground(Color.WHITE);
        panel.add(usernameField);

        // Add focus listener to handle placeholder behavior
        usernameField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(usernameField.getText()).equals("Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK); // Change text color when typing
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Username");
                    usernameField.setForeground(Color.GRAY); // Restore placeholder colors
                }
            }
        });

        // Password Field
        passwordField = new CustomPasswordField("Password");
        passwordField.setBounds(250, 500, 400, 50);
        passwordField.setBorder(null);
        passwordField.setBackground(Color.WHITE);
        passwordField.setEchoChar((char) 0);
        panel.add(passwordField);

        // Add focus listener to handle placeholder behavior
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals("Password")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK); // Change text color when typing
                    passwordField.setEchoChar('*'); // Mask password input
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText("Password");
                    passwordField.setForeground(Color.GRAY); // Restore placeholder color
                    passwordField.setEchoChar((char) 0); // Show placeholder text as normal
                }
            }
        });

        // Enter Button
        enterButton = new RoundedButton("Enter");
        enterButton.setBounds(250, 600, 400, 50);
        enterButton.setBackground(new Color(25, 50, 100));
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.setBorderPainted(false);
        enterButton.setContentAreaFilled(false);
        enterButton.setOpaque(true);
        enterButton.setFocusable(false);
        enterButton.addActionListener(this);
        panel.add(enterButton);

        // Create forgot password button
        forgotPassword = new JButton("Forgot Password?");
        forgotPassword.setForeground(Color.GRAY);
        forgotPassword.setBounds(500, 560, 200, 20);
        forgotPassword.setBackground(new Color(10, 15, 25));
        forgotPassword.setBorder(null);
        forgotPassword.setFocusable(false);
        forgotPassword.addActionListener(this);
        forgotPassword.setContentAreaFilled(false);
        panel.add(forgotPassword);

        createAccountButton = new JButton("Create Account");
        createAccountButton.setForeground(Color.ORANGE);
        createAccountButton.setBackground(new Color(10, 15, 25));
        createAccountButton.setFont(new Font("Serif", Font.BOLD, 14));
        createAccountButton.setBounds(350, 680, 200, 20);
        createAccountButton.setBorder(null);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setContentAreaFilled(false);
        createAccountButton.addActionListener(this);
        panel.add(createAccountButton);

        // Load and display image
        ImageIcon icon = new ImageIcon("./assets/images/login.png");

        // Scale the image to fit the JLabel size
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(650, 700, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        // Create JLabel with the resized image
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBounds(850, 100, 700, 800);

        panel.add(imageLabel);

        // Show Frame
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==enterButton){
            System.out.println("Welcome " + usernameField.getText());
            System.out.println("Your password is " + new String(passwordField.getPassword()));
            MyJDBC jdbc = new MyJDBC();
            if (jdbc.checkLogin(usernameField.getText(), new String(passwordField.getPassword())) ==3){
                new MarioAdventureUI();
                frame.setVisible(false);
                frame.dispose();


            } else if (jdbc.checkLogin(usernameField.getText(), new String(passwordField.getPassword())) ==2){
                System.out.println("Invalid Username");
                JOptionPane.showMessageDialog(null, "you have entered an invalid username",
                        "Invalid Username",JOptionPane.ERROR_MESSAGE);
            } else if (jdbc.checkLogin(usernameField.getText(), new String(passwordField.getPassword())) ==1){
                System.out.println("Invalid password");
                JOptionPane.showMessageDialog(null, "you have entered an invalid password",
                        "Invalid Password",JOptionPane.ERROR_MESSAGE);
            }

        }

        if (e.getSource() == createAccountButton){
            System.out.println("the button has been clicked");
            frame.setVisible(false);
            frame.dispose();

            new SignupUI();
        }

        if (e.getSource() == forgotPassword){
            new ForgotPassword();
        }
    }
}
