package method;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class MessageManager {
    private static JPanel chatPanel;
    private static JScrollPane scrollPane;

    // Initialize MessageManager and bind UI components
    public static void initialize(JPanel panel, JScrollPane scroll) {
        chatPanel = panel;
        scrollPane = scroll;
    }

    // Display message on the UI
    private static void displayMessage(String text, boolean isUser) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 5));

        JTextArea messageArea = new JTextArea(text);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setOpaque(true);

        messageArea.setBackground(isUser ? Color.LIGHT_GRAY : Color.WHITE);
        messageArea.setForeground(Color.BLACK);
        messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // add to scroll pane
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        Dimension preferredSize = new Dimension(chatPanel.getWidth() - 50, messageArea.getPreferredSize().height + 50);
        messageScrollPane.setPreferredSize(preferredSize);
        messagePanel.add(messageScrollPane);

        // add message panel to the chat panel
        chatPanel.add(messagePanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        // Auto-scroll to the bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });

    }

    // Handle user input message
    public static void handleUserInput(String prompt, OpenAI openAI, JTextField userInput) {
        if (prompt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a message before sending.", "Empty Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Display user's message
        displayMessage(prompt, true); // save to database
        saveMessageToDatabase("User", prompt); //clear the input field
        userInput.setText("");

        // OpenAI API Async call to OpenAI API
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                // Call OpenAI to get a response based on the user prompt
                return openAI.callChat(prompt);
            }

            /**
             * 在后台任务完成后在主线程执行。
             * Executes on the Event Dispatch Thread (EDT) after the background task is finished.
             */
            @Override
            protected void done() {
                try {
                    // Retrieve the result of doInBackground() 获取 doInBackground() 的结果
                    String response = get();

                    // If the response is valid, display it and save to database如果响应有效，则显示并保存到数据库
                    if (response != null && !response.isEmpty()) {
                        displayMessage(response, false);
                        saveMessageToDatabase("AI", response);
                    } else {
                        displayMessage("No response from AI.", false);
                    }
                } catch (Exception e) {
                    // Handle any exceptions that occur while retrieving the response处理获取响应过程中发生的异常
                    displayMessage("Error: Unable to get a response from the server.", false);
                    e.printStackTrace(); // Print stack trace for debugging 打印堆栈跟踪以进行调试
                }
            }
        };

        // Execute the SwingWorker task
        worker.execute();
    }

    // Loads the chat history from the database and displays it in the chat panel.
    public static void loadChatHistory() {
        // Ensures the UI update is performed on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try (Connection connection = DatabaseConnection.getConnection()) {
                System.out.println("Connected to database");

                // SQL query to retrieve all messages ordered by creation time
                String query = "SELECT username, message FROM public.chat_messages ORDER BY created_at ASC";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                // Iterate through the result set and render each message
                while (resultSet.next()) {
                    String message = resultSet.getString("message"); // Message content
                    String username = resultSet.getString("username"); // Username
                    // Determine if the message is from the user
                    boolean isUser = username != null && username.equalsIgnoreCase("User");

                    // Display the message on the chat panel
                    displayMessage(message, isUser);
                }

                // Refresh the chat panel
                chatPanel.revalidate();
                chatPanel.repaint();

                System.out.println("Finished loading chat history");
            } catch (Exception e) {
                System.err.println("Failed to load chat history: " + e.getMessage());
            }
        });
    }

    // Saves a single message to the database.
    public static void saveMessageToDatabase(String username, String message) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQL query to insert a new message with its timestamp
            String query = "INSERT INTO public.chat_messages (username, message, created_at) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username); // Set the username
            statement.setString(2, message);  // Set the message content
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Set the current timestamp

            // Execute the query to insert the message
            statement.executeUpdate();
        } catch (Exception e) {
            System.err.println("Failed to save message to database: " + e.getMessage());
        }
    }
}