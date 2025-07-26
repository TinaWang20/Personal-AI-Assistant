package method;
import javax.swing.*;
import java.awt.*;

public class UI extends JFrame {
    private final JTextField userInput;
    private final JButton sendButton;
    private final JPanel chatPanel;
    private final JScrollPane scrollPane;
    private final OpenAI openAI;

    public UI() {
        setTitle("ChatUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        openAI = new OpenAI();

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        userInput = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(userInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // ScrollPane
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(inputPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize MessageManager
        MessageManager.initialize(chatPanel, scrollPane);

        // Add button action listener
        sendButton.addActionListener(e -> MessageManager.handleUserInput(userInput.getText().trim(), openAI, userInput));
        // Load chat history
        MessageManager.loadChatHistory();

        setVisible(true);
    }

}