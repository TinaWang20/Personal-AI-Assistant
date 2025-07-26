package method;

import io.github.stefanbratanov.jvm.openai.*;

public class OpenAI {

    public String callChat(String question) throws Exception {

        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API Key not found!");
            return "API Key not found!";
        }

        try {
            // Initialize OpenAI client
            io.github.stefanbratanov.jvm.openai.OpenAI openAI = io.github.stefanbratanov.jvm.openai.OpenAI.newBuilder(apiKey).build();
            System.out.println("Sending question to OpenAI: " + question);

            ChatClient chatClient = openAI.chatClient();
            CreateChatCompletionRequest request = CreateChatCompletionRequest.newBuilder()
                    .model("gpt-4o") // model("gpt-4o")("gpt-3.5-turbo")
                    .message(ChatMessage.userMessage(question))
                    .build();

            ChatCompletion chatCompletion = chatClient.createChatCompletion(request);
            String response = chatCompletion.choices().get(0).message().content();
            System.out.println("Received response from OpenAI: " + response);

            return response;
        } catch (Exception e) {
            System.err.println("Error during OpenAI API call: " + e.getMessage());
            e.printStackTrace();
            return "Error connecting to OpenAI API. Please try again later.";
        }

    }

}
