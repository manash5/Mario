import Database.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class LeadershipBoard extends JFrame {
    public LeadershipBoard() {
        setTitle("Leadership Board");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel (Full screen)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(28, 54, 164));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // **Title Panel (Above the leaderboard)**
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(10, 20, 80));
        titlePanel.setPreferredSize(new Dimension(400, 50)); // Reduce width
        titlePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("LEADERSHIP BOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Positioning the title
        gbc.gridy = 0;
        gbc.insets = new Insets(70, 300, 0, 300); // Reduced top padding
        gbc.weighty = 0.0;
        mainPanel.add(titlePanel, gbc);

        // **Leaderboard Panel (Just Below the Title)**
        JPanel leaderboardPanel = new JPanel(new GridBagLayout());
        leaderboardPanel.setBackground(new Color(28, 54, 164));
        leaderboardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 6)); // Black border

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 100, 10, 100);
        gbc.weighty = 0.0;
        mainPanel.add(leaderboardPanel, gbc);

        GridBagConstraints lbGbc = new GridBagConstraints();
        lbGbc.gridx = 0;
        lbGbc.fill = GridBagConstraints.HORIZONTAL;
        lbGbc.weightx = 1.0;

        // **Column Headers**
        JPanel headerPanel = new JPanel(new GridLayout(1, 4));
        headerPanel.setBackground(new Color(10, 20, 80));
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

        // Sorting the leaderboard
        leaderboardData.sort(Comparator.comparingInt((String[] data) -> {
                    // Extracting the coins collected from "X/50"
                    return Integer.parseInt(data[2].split("/")[0]);
                }).reversed()
                .thenComparingInt(data -> Integer.parseInt(data[3]))); // Sorting by time taken in ascending order

        for (int i = 0; i < leaderboardData.size(); i++) {
            lbGbc.gridy = i + 1;
            String[] rowData = new String[]{String.valueOf(i + 1), leaderboardData.get(i)[1], leaderboardData.get(i)[2], leaderboardData.get(i)[3]};
            JPanel row = createLeaderboardRow(rowData);
            row.setPreferredSize(new Dimension(600, 50));
            leaderboardPanel.add(row, lbGbc);
        }

        // Push everything upwards
        JPanel spacerPanel = new JPanel();
        spacerPanel.setBackground(new Color(28, 54, 164)); // Match the background color of mainPanel
        gbc.gridy = 2;
        gbc.weighty = 1.0; // Pushes everything to the top
        mainPanel.add(spacerPanel, gbc);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createLeaderboardRow(String[] rowData) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4));
        rowPanel.setBackground(new Color(96, 141, 255));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        for (int i = 0; i < rowData.length; i++) {
            JLabel label = new JLabel(rowData[i], SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));
            label.setForeground(Color.WHITE);
            rowPanel.add(label);
        }
        return rowPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LeadershipBoard::new);
    }
}
