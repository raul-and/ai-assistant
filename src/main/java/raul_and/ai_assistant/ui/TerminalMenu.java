package raul_and.ai_assistant.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import raul_and.ai_assistant.api.AIAService;

import java.util.Scanner;

@Component
public class TerminalMenu implements CommandLineRunner {

    private final AIAService aiaService;

    @Value("${assistant.id}")
    private String assistantId;

    private String threadId = "";
    private String userQuery = "";
    private String runId = "";

    private int numMessages = 0;

    public TerminalMenu(AIAService aiaService) { this.aiaService = aiaService; }

    private String getAssistantId() { return assistantId; }

    private void setAssistantId(String assistantId) { this.assistantId = assistantId; }

    private String getThreadId() { return threadId; }

    private void setThreadId(String threadId) { this.threadId = threadId; }

    private void setUserQuery(String userQuery) { this.userQuery = userQuery; }

    private String getUserQuery(){ return userQuery; }

    private String getRunId() { return runId; }

    private void setRunId(String runId) { this.runId = runId; }

    @Override
    public void run(String... args) throws Exception {
        ResponseEntity<String> response;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nHello!! I'm your helpful AI assistant. How can I help you?");
            System.out.println("----------------------------------------------------------\n");
            System.out.println("1. Create Assistant");
            System.out.println("2. Create Thread");
            System.out.println("3. Add Message to Thread");
            System.out.println("4. Create Run");
            System.out.println("5. Poll Run Status");
            System.out.println("6. Get Thread Messages");
            System.out.println("7. Exit\n");

            System.out.printf("Current assistant id: %s\n", getAssistantId());
            System.out.printf("Current thread id: %s\n", getThreadId());
            System.out.printf("Current num of messages on the thread: %d\n", numMessages);
            System.out.printf("Current run id: %s\n", getRunId());

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Enter the assistant instructions: ");
                    setUserQuery(scanner.nextLine());

                    System.out.println("Enter the assistant role: ");
                    String role = scanner.nextLine();

                    response = aiaService.createAssistant(userQuery, role);

                    setAssistantId(extractJsonValue(response.getBody(), "id"));
                    System.out.printf("Assistant created with id: %s\n", assistantId);
                    break;

                case "2":
                    response = aiaService.createThread();

                    setThreadId(extractJsonValue(response.getBody(), "id"));
                    System.out.printf("Thread created with id: %s\n", threadId);
                    break;

                case "3":
                    if (threadId.isEmpty()){
                        System.out.println("ERROR: You need to create a thread first.");
                        break;
                    }

                    System.out.println("Enter the prompt you would like to add: ");
                    setUserQuery(scanner.nextLine());

                    aiaService.addMessageToThread(getThreadId(), getUserQuery());
                    numMessages++;
                    System.out.printf("The following message has been added to the thread: " + "%s\n", getUserQuery());
                    break;

                case "4":
                    if (threadId.isEmpty()){
                        System.out.println("ERROR: You need to create a thread first.");
                        break;
                    }

                    System.out.println("Enter the run instructions: ");
                    setUserQuery(scanner.nextLine());

                    response = aiaService.createRun(getThreadId(), getAssistantId(), getUserQuery());
                    setRunId(extractJsonValue(response.getBody(), "id"));

                    System.out.printf("Run created with id: %s\n", getRunId());
                    break;

                case "5":
                    if (threadId.isEmpty()){
                        System.out.println("ERROR: You need to create a thread first.");
                        break;
                    }

                    if (runId.isEmpty()){
                        System.out.println("ERROR: You need to create a run first.");
                        break;
                    }

                    String status = aiaService.pollRunStatus(getThreadId(), getRunId());

                    System.out.println("Run status: " + status);
                    break;

                case "6":
                    if (threadId.isEmpty()){
                        System.out.println("ERROR: You need to create a thread first.");
                        break;
                    }

                    response = aiaService.getThreadMessages(threadId);

                    setUserQuery(extractJsonValue(response.getBody(), "value"));

                    System.out.printf("Messages: %s\n", getUserQuery());
                    break;

                case "7":
                    System.out.println("Exiting...");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private String extractJsonValue(String jsonResponse, String... fieldNames) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode currentNode = objectMapper.readTree(jsonResponse);
            for (String fieldName : fieldNames) {
                currentNode = currentNode.path(fieldName);
            }
            return currentNode.asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
