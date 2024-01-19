package org.example;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ElectionVotingApp extends Application {

    private Map<String, String> userCredentials;
    private Map<String, Integer> candidateVotes;
    private Set<String> votedUsers;
    private Map<String, String> userSecrets;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        userCredentials = new HashMap<>();
        votedUsers = new HashSet<>();
        candidateVotes = new HashMap<>();
        userSecrets = new HashMap<>();

        // Add some sample user credentials
        userCredentials.put("user1", "password1");
        userCredentials.put("user2", "password2");
        userCredentials.put("user3", "password3");

        candidateVotes.put("Rahul", 0);
        candidateVotes.put("Bharat", 0);
        candidateVotes.put("Akhil", 0);

        primaryStage.setTitle("Election Voting App");

        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene loginScene = createLoginScene();
        primaryStage.setScene(loginScene);

        primaryStage.show();
    }

    private Scene createLoginScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button loginBtn = new Button("Login");
        grid.add(loginBtn, 1, 4);

        Label message = new Label();
        grid.add(message, 1, 6);

        loginBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();

            if (authenticateUser(username, password)) {
                if (votedUsers.contains(username)) {
                    message.setText("You have already voted.");
                } else {
                    // Check if the user has a secret key
                    if (userSecrets.containsKey(username)) {
                        showVotingPage(username);
                    } else {
                        showRegistrationPage(username);
                    }
                }
            } else {
                message.setText("Invalid username or password.");
            }
        });

        return new Scene(grid, 400, 275);
    }

    private boolean authenticateUser(String username, String password) {
        return userCredentials.containsKey(username) && userCredentials.get(username).equals(password);
    }

    private void showRegistrationPage(String username) {
        // Generate and store secret key for the user
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();
        userSecrets.put(username, secretKey);

        // Generate QR code URL for the user to scan
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("ElectionApp", username, key);
        System.out.println("Scan this QR code in Google Authenticator:\n" + otpAuthURL);

        // Display registration message and ask the user to scan the QR code
        System.out.println("Please scan the QR code and enter the generated code to complete registration.");

        // TODO: You can add a mechanism to verify the entered code and proceed to the voting page.
        // For simplicity, let's assume manual verification for now.
        showVotingPage(username);
    }

    private void showVotingPage(String username) {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label voteLabel = new Label("Vote for your candidate:");
        grid.add(voteLabel, 0, 1);

        ComboBox<String> candidateComboBox = new ComboBox<>();
        candidateComboBox.getItems().addAll("Rahul", "Bharat", "Akhil");
        grid.add(candidateComboBox, 1, 1);

        Button voteBtn = new Button("Vote");
        grid.add(voteBtn, 1, 3);

        Label message = new Label();
        grid.add(message, 1, 5);

        voteBtn.setOnAction(e -> {
            String selectedCandidate = candidateComboBox.getValue();
            if (selectedCandidate != null) {
                votedUsers.add(username);
                candidateVotes.put(selectedCandidate, candidateVotes.get(selectedCandidate) + 1);
                message.setText("Thank you for voting!");
            } else {
                message.setText("Please select a candidate to vote.");
            }
        });

        Scene votingScene = new Scene(grid, 400, 275);
        primaryStage.setScene(votingScene);
    }
}
