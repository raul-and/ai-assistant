package raul_and.ai_assistant.assistantApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AIAConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
