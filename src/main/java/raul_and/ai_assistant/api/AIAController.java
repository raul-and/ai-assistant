package raul_and.ai_assistant.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-assistant")
public class AIAController {

    private final AIAService aiaService;

    @Autowired
    public AIAController(AIAService aiaService){
        this.aiaService = aiaService;
    }

    @PostMapping("/createAssistant")
    public ResponseEntity<String> createAssistant(@RequestParam String instruction, @RequestParam String role) {
        return aiaService.createAssistant(instruction, role);
    }

    @PostMapping("/createThread")
    public ResponseEntity<String> createThread() {
        return aiaService.createThread();
    }

    @PostMapping("/addMessage")
    public ResponseEntity<String> addMessageToThread(@RequestParam String threadId, @RequestParam String message) {
        return aiaService.addMessageToThread(threadId, message);
    }

    @PostMapping("/createRun")
    public ResponseEntity<String> createRun(
            @RequestParam String threadId,
            @RequestParam String assistantId,
            @RequestParam String instructions) {
        return aiaService.createRun(threadId, assistantId, instructions);
    }

    @GetMapping("/pollRunStatus")
    public String pollRunStatus(@RequestParam String threadId, @RequestParam String runId) throws InterruptedException {
        return aiaService.pollRunStatus(threadId, runId);
    }

    @GetMapping("/threadMessages")
    public ResponseEntity<String> getThreadMessages(@RequestParam String threadId) {
        return aiaService.getThreadMessages(threadId);
    }
}
