package raul_and.ai_assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import raul_and.ai_assistant.api.AIAService;
import raul_and.ai_assistant.ui.TerminalMenu;

@SpringBootApplication
public class AiAssistantApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(AiAssistantApplication.class, args);

		AIAService aiaService = context.getBean(AIAService.class);

		TerminalMenu terminalMenu = new TerminalMenu(aiaService);

		try {
			terminalMenu.run();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
