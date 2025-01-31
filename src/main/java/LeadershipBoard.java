import Database.MyJDBC;
import UIUtil.BackgroundPanel;
import UIUtil.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;

public class LeadershipBoard extends JFrame implements ActionListener {
    RoundedButton button;
    private BackgroundPanel backgroundPanel;

    public LeadershipBoard() {
        backgroundPanel = new BackgroundPanel("./assets/images/LeadershipBoard.png");
        setContentPane(backgroundPanel); // Set background panel as the content pane
        backgroundPanel.setLayout(new BorderLayout());

        ImageIcon icon = new ImageIcon("./assets/images/left.png");
        Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

        button = new RoundedButton("");
        button.setPreferredSize(new Dimension(100, 40));
        button.setIcon(new ImageIcon(img));
        button.addActionListener(this);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // **Main Layered Panel to hold everything**
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false); // Make sure the background image is visible

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // **Top Panel (Contains Only Back Button)**
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(180, 31, 34));
        topPanel.setPreferredSize(new Dimension(getWidth(), 70));

        // **Panel for Button**
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 0));
        buttonPanel.add(button);

        topPanel.add(buttonPanel, BorderLayout.WEST);

        mainPanel.add(topPanel, gbc);

        // **Title Panel**
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(229,71,31));
        titlePanel.setPreferredSize(new Dimension(400, 50));
        titlePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("LEADERSHIP BOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(70, 500, 0, 500);
        mainPanel.add(titlePanel, gbc);

        // **Leaderboard Panel**
        JPanel leaderboardPanel = new JPanel(new GridBagLayout());
        leaderboardPanel.setBackground(new Color(144,23,33));
        leaderboardPanel.setBorder(BorderFactory.createLineBorder(new Color(144,23,33), 6));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 100, 10, 100);
        mainPanel.add(leaderboardPanel, gbc);

        GridBagConstraints lbGbc = new GridBagConstraints();
        lbGbc.gridx = 0;
        lbGbc.fill = GridBagConstraints.HORIZONTAL;
        lbGbc.weightx = 1.0;

        // **Column Headers**
        JPanel headerPanel = new JPanel(new GridLayout(1, 4));
        headerPanel.setBackground(new Color(113,16,32));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        String[] headers = {"Position", "Username", "Coins Collected", "Time Taken"};
        for (String text : headers) {
            JLabel label = new JLabel(text, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setForeground(Color.WHITE);
            headerPanel.add(label);
        }

        lbGbc.gridy = 0;
        lbGbc.insets = new Insets(5, 0, 5, 0);
        leaderboardPanel.add(headerPanel, lbGbc);

        // **Fetching Data from Database**
        List<String[]> leaderboardData = MyJDBC.getLeadershipBoardData();

        leaderboardData.sort(Comparator.comparingInt((String[] data) ->
                        Integer.parseInt(data[2].split("/")[0]))
                .reversed()
                .thenComparingInt(data -> Integer.parseInt(data[3])));

        for (int i = 0; i < leaderboardData.size(); i++) {
            lbGbc.gridy = i + 1;
            String[] rowData = new String[]{String.valueOf(i + 1), leaderboardData.get(i)[1], leaderboardData.get(i)[2], leaderboardData.get(i)[3]};
            JPanel row = createLeaderboardRow(rowData);
            row.setPreferredSize(new Dimension(600, 50));
            leaderboardPanel.add(row, lbGbc);
        }

        // **Spacer Panel**
        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        mainPanel.add(spacerPanel, gbc);

        backgroundPanel.add(mainPanel);
        setVisible(true);
    }

    private JPanel createLeaderboardRow(String[] rowData) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4));
        rowPanel.setBackground(new Color(180,31,34));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        for (String data : rowData) {
            JLabel label = new JLabel(data, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));
            label.setForeground(Color.WHITE);
            rowPanel.add(label);
        }
        return rowPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            new MarioAdventureUI();
            this.dispose();
        }
    }

    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Serif", Font.BOLD, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(113,16,32));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g);
        }
    }
}
