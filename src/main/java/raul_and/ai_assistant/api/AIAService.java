package raul_and.ai_assistant.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIAService {

    @Value("${open.api.key}")
    private String openaiApiKey;

    @Value("${open.api.url}")
    private String openaiApiUrl;

    private RestTemplate restTemplate;

    public AIAService(RestTemplate restTemplate){ this.restTemplate = restTemplate; }

    //Create an AI Assistant
    public ResponseEntity<String> createAssistant(String userQuery, String role){
        String url = openaiApiUrl + "/v1/assistants";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("instructions", userQuery);
        requestBody.put("name", role);
        requestBody.put("tools", new Object[]{new HashMap<String, Object>() {{
            put("type", "code_interpreter");
        }}});
        requestBody.put("model", "gpt-4o-mini-2024-07-18");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    //Create a Thread
    public ResponseEntity<String> createThread() {
        String url = openaiApiUrl + "/v1/threads";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    //Add a Message to the Thread
    public ResponseEntity<String> addMessageToThread(String threadId, String messageContent){
        String url = openaiApiUrl + "/v1/threads/" + threadId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("role", "user");
        requestBody.put("content", messageContent);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    //Create a Run
    public ResponseEntity<String> createRun(String threadId, String assistantId, String instructions){
        String url = openaiApiUrl + "/v1/threads/" + threadId + "/runs";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("OpenAI-Beta", "assistants=v2");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("assistant_id", assistantId);
        requestBody.put("instructions", instructions);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    // Poll the Run Status
    public String pollRunStatus(String threadId, String runId) throws InterruptedException {
        String url = openaiApiUrl + "/v1/threads/" + threadId + "/runs/" + runId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("Content-Type", "application/json");
        headers.set("OpenAI-Beta", "assistants=v2");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        String status = "in_progress";
        while ("in_progress".equals(status)) {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                status = (String) responseBody.get("status");
            }

            if ("in_progress".equals(status)) {
                Thread.sleep(2000); // wait for 2 seconds before polling again
            }
        }

        return status;
    }

    // Retrieve Assistant Messages
    public ResponseEntity<String> getThreadMessages(String threadId) {
        String url = openaiApiUrl + "/v1/threads/" + threadId + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("Content-Type", "application/json");
        headers.set("OpenAI-Beta", "assistants=v2");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
    }

    private String extractJsonParam(String jsonResponse, String fieldName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            return rootNode.path(fieldName).asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
