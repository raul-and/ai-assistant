package raul_and.ai_assistant.telegramApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {

    @Value("${bot.token}")
    private String botToken;

    @Value("${ngrok.domain}")
    private String ngrokDomain;

    private String textInput = "";
    private String chatId = "";
    private String sender = "";
    private final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    public String getTextInput() { return textInput; }
    public void setTextInput(String textInput) { this.textInput = textInput; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getNgrokDomain() { return ngrokDomain; }

    public ResponseEntity<String> setWebhook(){
        String url = TELEGRAM_API_URL + botToken + "/setWebhook?url=" + ngrokDomain + "/telegram/webhook";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        System.out.println("Response from Telegram API: " + response.getBody());

        return response;
    }

    public ResponseEntity<String> receiveUpdate(@RequestBody String update) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(update);

            setChatId(jsonNode.path("message").path("chat").path("id").asText());
            setTextInput(jsonNode.path("message").path("text").asText());
            setSender(jsonNode.path("message").path("from").path("first_name").asText());

            System.out.println("\nMessage received from " + getSender() + "::" + getChatId() + " : " + getTextInput());

        } catch (IOException e){
            e.printStackTrace();
        }

        return ResponseEntity.ok("Message was processed");
    }

    public ResponseEntity<String> sendMessage(String chatId, String text){
        String url = TELEGRAM_API_URL + botToken + "/sendMessage";

        Map<String, String> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        System.out.println("Telegram API Response: " + response.getBody());

        return response;
    }
}
