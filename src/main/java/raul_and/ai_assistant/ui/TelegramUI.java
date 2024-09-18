package raul_and.ai_assistant.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import raul_and.ai_assistant.assistantApi.AIAService;
import raul_and.ai_assistant.telegramApi.TelegramService;

@Component
public class TelegramUI {

    private final TelegramService telegramService;
    private final AIAService aiaService;

    private String userInput = "";
    private String runId = "";
    private String runStatus = "";

    public TelegramUI(TelegramService telegramService, AIAService aiaService) {
        this.telegramService = telegramService;
        this.aiaService = aiaService;
    }

    public void run(){

        while(true){

            userInput = telegramService.getTextInput();

            switch (userInput) {

                case "/start":
                    telegramService.sendMessage(telegramService.getChatId(), "The bot has been started.");
                    break;

                case "/ask":
                    telegramService.sendMessage(telegramService.getChatId(), "Ask any question!");
                    try{ handleQuestion(); }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case "/gptConf":
                    gptConf();
                    break;

                default:
                    break;
            }

            telegramService.setTextInput("");

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    void handleQuestion() throws Exception{

        ResponseEntity<String> response;
        telegramService.setTextInput("");

        while(true){

            if (telegramService.getTextInput().equals("/cancel")) {
                telegramService.sendMessage(telegramService.getChatId(), "Canceled current command");
                break;
            }

            if(!telegramService.getTextInput().isEmpty()){

                aiaService.addMessageToThread(aiaService.getThreadId(), telegramService.getTextInput());
                response = aiaService.createRun(aiaService.getThreadId(), aiaService.getAssistantId(), "Answer the following question");

                runId = extractJsonValue(response.getBody(), "id");

                telegramService.sendMessage(telegramService.getChatId(), "// Processing...");

                while(true){

                    runStatus = aiaService.pollRunStatus(aiaService.getThreadId(), runId);
                    System.out.printf("RUN STATUS: %s\n", runStatus);
                    if (runStatus.equals("completed")) { break; }

                    Thread.sleep(200);
                }

                response = aiaService.getThreadMessages(aiaService.getThreadId());
                String aiAnswer = extractJsonValue(response.getBody(), "getThreadMessages");

                telegramService.sendMessage(telegramService.getChatId(), aiAnswer);
                break;
            }


            Thread.sleep(300);
        }
    }

    private void gptConf(){
    }

    private String extractJsonValue(String jsonResponse, String... fieldNames) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            if (fieldNames.length == 1 && "getThreadMessages".equals(fieldNames[0])) {
                JsonNode dataArray = rootNode.path("data");

                for (JsonNode messageNode : dataArray) {
                    if ("assistant".equals(messageNode.path("role").asText())) {
                        return messageNode.path("content").get(0).path("text").path("value").asText();
                    }
                }

                return null;
            }

            JsonNode currentNode = rootNode;
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
