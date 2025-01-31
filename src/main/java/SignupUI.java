import Database.MyJDBC;
import UIUtil.CustomPasswordField;
import UIUtil.RoundedTextField;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class SignupUI extends JFrame implements ActionListener {
    RoundedTextField usernameField;
    CustomPasswordField createPasswordField;
    CustomPasswordField confirmPasswordField;
    JButton signupButton;
    JFrame frame;
    public SignupUI(){
        // Create frame
        frame = new JFrame("SignUp");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Background color
        frame.getContentPane().setBackground(new Color(47,125,21,255));

        // Center panel for signup elements
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(frame.getWidth(), frame.getHeight(), 1500, 850);
        panel.setBackground(new Color(47,125,21,255));
        frame.add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("SignUp");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 44));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(350, 150, 180, 60);
        panel.add(titleLabel);

        // Making the borders round
        Border roundedBorder = new CompoundBorder(
                new LineBorder(Color.WHITE, 2, true),
                new EmptyBorder(10, 15, 10, 15)
        );
        Border roundedBorder1 = new CompoundBorder(
                new LineBorder(Color.BLACK, 2, true),
                new EmptyBorder(10, 15, 10, 15)
        );

        // Username Field
        usernameField = new RoundedTextField("Username");
        usernameField.setBounds(250, 300, 400, 60);
        usernameField.setBorder(null);
        usernameField.setBackground(Color.WHITE);
        usernameField.setFont(new Font("Serif", Font.ITALIC, 16));
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

        // Create Password Field
        createPasswordField = new CustomPasswordField(" create password");
        createPasswordField.setBounds(250, 400, 400, 50);
        createPasswordField.setBorder(null);
        createPasswordField.setBackground(Color.WHITE);
        createPasswordField.setEchoChar((char)0);
        createPasswordField.setFont(new Font("Serif", Font.ITALIC, 16));
        panel.add(createPasswordField);

        // Add focus listener to handle placeholder behavior
        createPasswordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(createPasswordField.getPassword()).equals(" create password")) {
                    createPasswordField.setText("");
                    createPasswordField.setForeground(Color.BLACK); // Change text color when typing
                    createPasswordField.setEchoChar('*'); // Mask password input
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (createPasswordField.getPassword().length == 0) {
                    createPasswordField.setText(" create password");
                    createPasswordField.setForeground(Color.GRAY); // Restore placeholder color
                    createPasswordField.setEchoChar((char) 0); // Show placeholder text as normal
                }
            }
        });

        // Confirm Password Field
        confirmPasswordField = new CustomPasswordField(" confirm password");
        confirmPasswordField.setBounds(250, 500, 400, 50);
        confirmPasswordField.setBorder(null);
        confirmPasswordField.setBackground(Color.WHITE);
        confirmPasswordField.setEchoChar((char)0);
        confirmPasswordField.setFont(new Font("Serif", Font.ITALIC, 16));
        panel.add(confirmPasswordField);

        // Add focus listener to handle placeholder behavior
        confirmPasswordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(confirmPasswordField.getPassword()).equals(" confirm password")) {
                    confirmPasswordField.setText("");
                    confirmPasswordField.setForeground(Color.BLACK); // Change text color when typing
                    confirmPasswordField.setEchoChar('*'); // Mask password input
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (confirmPasswordField.getPassword().length == 0) {
                    confirmPasswordField.setText(" confirm password");
                    confirmPasswordField.setForeground(Color.GRAY); // Restore placeholder color
                    confirmPasswordField.setEchoChar((char) 0); // Show placeholder text as normal
                }
            }
        });


        // Signup Button
        signupButton = new JButton("Signup");
        signupButton.setBounds(260, 600, 380, 50);
        signupButton.setBackground(Color.BLACK);
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);
        signupButton.setContentAreaFilled(false);
        signupButton.setOpaque(true);
        signupButton.setFocusable(false);
        signupButton.addActionListener(this);
        panel.add(signupButton);

        // Load and display image
        ImageIcon icon = new ImageIcon("./assets/images/signup.png");
        Image img = icon.getImage();
        System.out.println(icon.getIconWidth());
        System.out.println(icon.getIconHeight());
        Image scaledImg = img.getScaledInstance(650, 700, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBounds(750, 100, 750, 800);
        panel.add(imageLabel);

        // Show Frame
        frame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==signupButton){
            if ((new String(createPasswordField.getPassword())).equals(new String(confirmPasswordField.getPassword()))){
                MyJDBC jdbc = new MyJDBC();
                if (jdbc.checkLogin(usernameField.getText(), new String(createPasswordField.getPassword())) ==1){
                    JOptionPane.showMessageDialog(null, "The username already exists", "Duplicate username", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("username already exists");
                } else {
                    MyJDBC.AddUsername(usernameField.getText(), new String(confirmPasswordField.getPassword()));
                    frame.setVisible(false);
                    frame.dispose();
                    new LoginUI();
                }

            } else{
                JOptionPane.showMessageDialog(null, "passwords are not same",
                        "Password mismatch", JOptionPane.ERROR_MESSAGE);
            }
        }

    }
}
