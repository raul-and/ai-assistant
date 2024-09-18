package raul_and.ai_assistant.telegramApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@RestController
@RequestMapping("/telegram")
public class TelegramController {

    @Autowired
    private TelegramService telegramService;

    @GetMapping("/setWebhook")
    public ResponseEntity<String> setTelegramWebhook() {
        telegramService.setWebhook();
        return ResponseEntity.ok("Webhook set successfully");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveUpdate(@RequestBody String update) {
        return telegramService.receiveUpdate(update);
    }
}
