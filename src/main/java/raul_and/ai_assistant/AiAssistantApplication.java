package raul_and.ai_assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import raul_and.ai_assistant.assistantApi.AIAService;
import raul_and.ai_assistant.telegramApi.TelegramService;
import raul_and.ai_assistant.ngrok.ngrokInitializer;
import raul_and.ai_assistant.ui.TelegramUI;

@SpringBootApplication
public class AiAssistantApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(AiAssistantApplication.class, args);

		ngrokInitializer ngrokInit = context.getBean(ngrokInitializer.class);
		ngrokInit.startNgrok();
		Runtime.getRuntime().addShutdownHook(new Thread(ngrokInit::stopNgrok));

		TelegramService telegramService = context.getBean(TelegramService.class);
		AIAService aiaService = context.getBean(AIAService.class);

		TelegramUI telegramUI = new TelegramUI(telegramService, aiaService);

		try {
			telegramUI.run();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
