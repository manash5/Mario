package UI;

import Database.MyJDBC;
import UIUtil.CustomPasswordField;
import UIUtil.RoundedButton;
import UIUtil.RoundedTextField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

public class SignupUI extends JFrame implements ActionListener {
    RoundedTextField usernameField;
    CustomPasswordField createPasswordField, confirmPasswordField;
    RoundedButton signupButton, loginButton;
    JCheckBox showCreatePassword, showConfirmPassword;
    JFrame frame;

    public SignupUI() {
        // Create frame
        frame = new JFrame("SignUp");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Background color
        frame.getContentPane().setBackground(new Color(47, 125, 21));

        // Center panel for signup elements
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(200, 150, 800, 600); // Adjusted size for larger screen
        panel.setBackground(new Color(47, 125, 21));
        frame.add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("SignUp");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60)); // Larger font size
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(200, 30, 250, 70); // Adjusted position
        panel.add(titleLabel);

        // Username Field (Rounded)
        usernameField = new RoundedTextField("Username");
        usernameField.setBounds(50, 150, 500, 60); // Adjusted position and size
        panel.add(usernameField);

        // Create Password Field (Initially Text, then Password)
        createPasswordField = new CustomPasswordField("Create Password");
        createPasswordField.setBounds(50, 240, 500, 60); // Adjusted position and size
        panel.add(createPasswordField);

        // Show Password Checkbox for Create Password
        showCreatePassword = new JCheckBox("Show");
        showCreatePassword.setBounds(570, 240, 100, 40); // Adjusted position
        showCreatePassword.setBackground(new Color(47, 125, 21));
        showCreatePassword.setForeground(Color.WHITE);
        panel.add(showCreatePassword);

        // Confirm Password Field (Initially Text, then Password)
        confirmPasswordField = new CustomPasswordField("Confirm Password");
        confirmPasswordField.setBounds(50, 320, 500, 60); // Adjusted position and size
        panel.add(confirmPasswordField);

        // Show Password Checkbox for Confirm Password
        showConfirmPassword = new JCheckBox("Show");
        showConfirmPassword.setBounds(570, 320, 100, 40); // Adjusted position
        showConfirmPassword.setBackground(new Color(47, 125, 21));
        showConfirmPassword.setForeground(Color.WHITE);
        panel.add(showConfirmPassword);

        // Signup Button (Rounded)
        signupButton = new RoundedButton("Signup");
        signupButton.setBounds(150, 420, 300, 60); // Adjusted position and size
        panel.add(signupButton);
        signupButton.addActionListener(this);

        // Load the original image
        ImageIcon imageIcon = new ImageIcon("./assets/images/signup.png"); // Replace with the actual image path

        // Resize the image
        Image originalImage = imageIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(600, 800, Image.SCALE_SMOOTH); // Set larger size

        // Set the resized image to the JLabel
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JLabel imageLabel = new JLabel(resizedIcon);

        // Set bounds for the resized image
        imageLabel.setBounds(1000, 100, resizedIcon.getIconWidth(), resizedIcon.getIconHeight()); // Adjusted position for large screen
        frame.add(imageLabel);

        // Show Frame
        frame.setVisible(true);

        // Add Show Password functionality
        showCreatePassword.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (showCreatePassword.isSelected()) {
                    createPasswordField.setEchoChar((char) 0);
                } else {
                    createPasswordField.setEchoChar('*');
                }
            }
        });

        showConfirmPassword.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (showConfirmPassword.isSelected()) {
                    confirmPasswordField.setEchoChar((char) 0);
                } else {
                    confirmPasswordField.setEchoChar('*');
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signupButton) {
            if ((new String(createPasswordField.getPassword()))
                    .equals(new String(confirmPasswordField.getPassword()))) {
                MyJDBC jdbc = new MyJDBC();
                if (jdbc.checkLogin(usernameField.getText(), new String(createPasswordField.getPassword())) == 1) {
                    JOptionPane.showMessageDialog(null, "The username already exists", "Duplicate username",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    MyJDBC.AddUsername(usernameField.getText(), new String(confirmPasswordField.getPassword()));
                    frame.setVisible(false);
                    frame.dispose();
                    new LoginUI();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Passwords do not match",
                        "Password mismatch", JOptionPane.ERROR_MESSAGE);
            }
        }
   }
}
