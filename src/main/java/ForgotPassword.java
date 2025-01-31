import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import Database.MyJDBC;
import UIUtil.RoundedButton;
import UIUtil.RoundedTextField;
import UIUtil.CustomPasswordField;
import UIUtil.BackgroundPanel;

public class ForgotPassword extends JFrame implements ActionListener{
    private CustomPasswordField passwordField, confirmPasswordField;
    private RoundedTextField usernameField;
    private JCheckBox showPasswordBox, showConfirmBox;
    private RoundedButton cancelButton, resetButton;
    private BackgroundPanel backgroundPanel;
    private MyJDBC myJDBC;

    public ForgotPassword() {
        super("Forgot Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        backgroundPanel = new BackgroundPanel("./assets/images/forgetpw.png");
        add(backgroundPanel);
        backgroundPanel.setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setOpaque(false);
        panel.setBounds(50, 100, 700, 600); // Increased panel size
        backgroundPanel.add(panel);

        // Component positioning with spacing:
        int y = 100;
        int spacing = 10;

        JLabel titleLabel = new JLabel("Forgot Password?");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 45));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(200, y+40, 400, 60);
        panel.add(titleLabel);
        y += titleLabel.getHeight() + spacing;

        JLabel subtitleLabel = new JLabel("To reset your password, enter your Username");
        subtitleLabel.setFont(new Font("Serif", Font.ITALIC, 21));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setBounds(200, y+50, 450, 30);
        panel.add(subtitleLabel);
        y += subtitleLabel.getHeight() + 5 * spacing; // Increased spacing here

        usernameField = new RoundedTextField("Username");
        usernameField.setBounds(200, y+50, 400, 50);
        usernameField.setBorder(null);
        panel.add(usernameField);
        y += usernameField.getHeight() + 6 * spacing;

        passwordField = new CustomPasswordField("Create Password");
        setPasswordPlaceholder(passwordField, "Create Password");
        passwordField.setBounds(200, y+30, 400, 50);
        panel.add(passwordField);
        y += passwordField.getHeight() + spacing;

        showPasswordBox = new JCheckBox("Show");
        showPasswordBox.setBounds(610, y -20, 80, 30); // Adjusted position
        showPasswordBox.setOpaque(false);
        showPasswordBox.setForeground(Color.WHITE);
        panel.add(showPasswordBox);
        y += passwordField.getHeight() + spacing; // Added to maintain consistent spacing


        confirmPasswordField = new CustomPasswordField("confirm password");
        setPasswordPlaceholder(confirmPasswordField, "Confirm Password");
        confirmPasswordField.setBounds(200, y, 400, 50);
        panel.add(confirmPasswordField);
        y += confirmPasswordField.getHeight() + spacing;

        showConfirmBox = new JCheckBox("Show");
        showConfirmBox.setBounds(610, y -50, 80, 30); // Adjusted position
        showConfirmBox.setOpaque(false);
        showConfirmBox.setForeground(Color.WHITE);
        panel.add(showConfirmBox);
        y += confirmPasswordField.getHeight() + spacing; // Added to maintain consistent spacing

        cancelButton = createStyledButton("Cancel");
        cancelButton.setBounds(200, y-50, 140, 40);
        cancelButton.setBackground(new Color(12,40,111));
        cancelButton.addActionListener(this);
        panel.add(cancelButton);

        resetButton = createStyledButton("Reset");
        resetButton.setBounds(450, y-50, 140, 40);
        resetButton.setBackground(new Color(12,40,111));
        resetButton.addActionListener(this);
        panel.add(resetButton);

        showPasswordBox.addActionListener(e -> togglePasswordVisibility(passwordField, showPasswordBox));
        showConfirmBox.addActionListener(e -> togglePasswordVisibility(confirmPasswordField, showConfirmBox));

        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()== resetButton){
            if (MyJDBC.checkLogin(usernameField.getText(), new String(passwordField.getPassword()))==2){
                JOptionPane.showMessageDialog(null, "you have entered an invalid username",
                        "Invalid Username",JOptionPane.ERROR_MESSAGE);
            } else if (new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))){
                int userID = Integer.parseInt(MyJDBC.getUserID(usernameField.getText()));
                MyJDBC.resetPassword(userID, new String(passwordField.getPassword()));
                JOptionPane.showMessageDialog(null, "Your password has been sucessfully reset",
                        "Password reset",JOptionPane.INFORMATION_MESSAGE);
                this.setVisible(false);
                this.dispose();
                new LoginUI();
            } else {
                JOptionPane.showMessageDialog(null, "password and confirm password does not match",
                        "Password Mismatch",JOptionPane.ERROR_MESSAGE);
            }

        }
    }


    private void setPasswordPlaceholder(JPasswordField field, String placeholder) {
        field.setFont(new Font("Serif", Font.ITALIC, 18));
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                createRoundedBorder()
        ));
        field.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('*');
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private RoundedButton createStyledButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 18));
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private Border createRoundedBorder() {
        return BorderFactory.createLineBorder(Color.WHITE, 2, true);
    }

    private void togglePasswordVisibility(JPasswordField field, JCheckBox checkBox) {
        field.setEchoChar(checkBox.isSelected() ? (char) 0 : '*');
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ForgotPassword::new);
    }
}
