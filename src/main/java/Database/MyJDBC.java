package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyJDBC {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/gamedatabase";
    private static final String USER = "root";
    private static final String PASSWORD = "admin123";
    private static String username1;
    public static String UserID;

    /**
     * Checks login credentials:
     * - Returns 1 if only the username exists.
     * - Returns 2 if the username does not exist.
     * - Returns 3 if both username and password match.
     */
    public static int checkLogin(String username, String password) {
        String checkUsernameQuery = "SELECT * FROM Login WHERE username = ?";
        String checkPasswordQuery = "SELECT * FROM Login WHERE username = ? AND username_password = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement usernameStatement = connection.prepareStatement(checkUsernameQuery);
             PreparedStatement passwordStatement = connection.prepareStatement(checkPasswordQuery)) {

            // Check if username exists
            usernameStatement.setString(1, username);
            ResultSet usernameResult = usernameStatement.executeQuery();

            if (!usernameResult.next()) {
                return 2; // Username does not exist
            }

            // Check if both username and password match
            passwordStatement.setString(1, username);
            passwordStatement.setString(2, password);
            ResultSet passwordResult = passwordStatement.executeQuery();

            if (passwordResult.next()) {
                UserID = passwordResult.getString("userID");
                username1 = passwordResult.getString("username");
                System.out.println(UserID);
                System.out.println(username1);
                return 3; // Both username and password match
            }

            return 1; // Username exists, but password is incorrect

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Error case
        }
    }

    public static void AddUsername(String username, String password) {
        String checkUserIdQuery = "SELECT userID FROM Login WHERE userID = ?";
        String insertLoginQuery = "INSERT INTO Login (userID, username, username_password) VALUES (?, ?, ?)";
        String insertLeaderboardQuery = "INSERT INTO LeadershipBoard (userID, username, coinsCollected, timeTaken) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement checkUserIdStmt = connection.prepareStatement(checkUserIdQuery);
             PreparedStatement insertLoginStmt = connection.prepareStatement(insertLoginQuery);
             PreparedStatement insertLeaderboardStmt = connection.prepareStatement(insertLeaderboardQuery)) {

            int userID;
            Random random = new Random();

            // Generate a unique userID
            ResultSet resultSet;
            do {
                userID = random.nextInt(1000000); // Generates a number between 0 and 999999
                checkUserIdStmt.setInt(1, userID);
                resultSet = checkUserIdStmt.executeQuery();
            } while (resultSet.next());

            // Insert new user into Login table
            insertLoginStmt.setInt(1, userID);
            insertLoginStmt.setString(2, username);
            insertLoginStmt.setString(3, password);
            insertLoginStmt.executeUpdate();

            // Insert user into LeadershipBoard with initial values (0 coinsCollected, 0 timeTaken)
            insertLeaderboardStmt.setInt(1, userID);
            insertLeaderboardStmt.setString(2, username);
            insertLeaderboardStmt.setInt(3, 0); // coinsCollected = 0
            insertLeaderboardStmt.setInt(4, 0); // timeTaken = 0
            insertLeaderboardStmt.executeUpdate();

            System.out.println("User added successfully with userID: " + userID);
            System.out.println("User added to LeadershipBoard with 0 coins and 0 time taken.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkScore(String userID, int newCoinsCollected, int newTimeTaken) {
        String selectQuery = "SELECT coinsCollected, TIME_TO_SEC(timeTaken) AS timeTaken FROM LeadershipBoard WHERE userID = ?";
        String updateQuery = "UPDATE LeadershipBoard SET coinsCollected = ?, timeTaken = SEC_TO_TIME(?) WHERE userID = ?";
        String updateTimeQuery = "UPDATE LeadershipBoard SET timeTaken = SEC_TO_TIME(?) WHERE userID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
             PreparedStatement updateTimeStmt = connection.prepareStatement(updateTimeQuery)) {

            // Fetch the current score for the user
            selectStmt.setString(1, userID);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                int currentCoinsCollected = resultSet.getInt("coinsCollected");
                int currentTimeTaken = resultSet.getInt("timeTaken"); // Time in seconds

                if (newCoinsCollected > currentCoinsCollected) {
                    // If new coinsCollected is greater, update both coinsCollected and timeTaken
                    updateStmt.setInt(1, newCoinsCollected);
                    updateStmt.setInt(2, newTimeTaken);
                    updateStmt.setString(3, userID);
                    updateStmt.executeUpdate();
                    System.out.println("Leaderboard updated: Higher coins collected.");
                } else if (newCoinsCollected == currentCoinsCollected && newTimeTaken < currentTimeTaken) {
                    // If coinsCollected is the same but new timeTaken is lower, update only timeTaken
                    updateTimeStmt.setInt(1, newTimeTaken);
                    updateTimeStmt.setString(2, userID);
                    updateTimeStmt.executeUpdate();
                    System.out.println("Leaderboard updated: Faster time taken.");
                } else {
                    System.out.println("No update required: Score not better.");
                }
            } else {
                System.out.println("User not found in LeadershipBoard.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getLeadershipBoardData() {
        List<String[]> leaderboardData = new ArrayList<>();
        String query = "SELECT userID, username, coinsCollected, TIME_TO_SEC(timeTaken) AS timeTaken FROM LeadershipBoard";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String userID = resultSet.getString("userID");
                String username = resultSet.getString("username");
                int coinsCollected = resultSet.getInt("coinsCollected");
                int maxCoins = 40; // Assume max coins is 50
                String coins = coinsCollected + "/" + maxCoins;
                String timeTaken = resultSet.getString("timeTaken");

                leaderboardData.add(new String[]{userID, username, coins, timeTaken});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboardData;
    }



    public static String getUserID(){
        System.out.println(UserID);
        return UserID;
    }

    public static String getUserID(String username) {
        String query = "SELECT userID FROM Login WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("userID");
            } else {
                System.out.println("Username not found.");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername(){
        return username1;
    }

    public void deleteUser(int userID) {
        String deleteLeadershipQuery = "DELETE FROM LeadershipBoard WHERE userID = ?";
        String deleteLoginQuery = "DELETE FROM Login WHERE userID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement deleteLeadershipStmt = connection.prepareStatement(deleteLeadershipQuery);
             PreparedStatement deleteLoginStmt = connection.prepareStatement(deleteLoginQuery)) {

            // Delete from LeadershipBoard first (foreign key constraint consideration)
            deleteLeadershipStmt.setInt(1, userID);
            deleteLeadershipStmt.executeUpdate();

            // Delete from Login table
            deleteLoginStmt.setInt(1, userID);
            int rowsAffected = deleteLoginStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User with userID " + userID + " deleted successfully from both tables.");
            } else {
                System.out.println("User with userID " + userID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetPassword(int userID, String newPassword) {
        String checkUserQuery = "SELECT userID FROM Login WHERE userID = ?";
        String updatePasswordQuery = "UPDATE Login SET username_password = ? WHERE userID = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
             PreparedStatement updatePasswordStmt = connection.prepareStatement(updatePasswordQuery)) {

            // Check if userID exists
            checkUserStmt.setInt(1, userID);
            ResultSet resultSet = checkUserStmt.executeQuery();

            if (resultSet.next()) {
                // User exists, update password
                updatePasswordStmt.setString(1, newPassword);
                updatePasswordStmt.setInt(2, userID);
                int rowsUpdated = updatePasswordStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Password reset successfully for userID: " + userID);
                } else {
                    System.out.println("Failed to reset password.");
                }
            } else {
                System.out.println("UserID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
