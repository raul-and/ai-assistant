package raul_and.ai_assistant.ngrok;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ngrokInitializer {

    @Value("${ngrok.init}")
    private String ngrokInit;

    @Value("${ngrok.port}")
    private String ngrokPort;

    private Process ngrokProcess;

    public void startNgrok() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ngrok", "http", "--domain=" + ngrokInit, ngrokPort);
            ngrokProcess = processBuilder.start();
            System.out.println("\nNgrok started successfully with domain: " + ngrokInit + " on Port: " + ngrokPort);
        } catch (Exception e) {
            System.err.println("\nFailed to start ngrok: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopNgrok() {
        if (ngrokProcess != null) {
            ngrokProcess.destroy();
            System.out.println("\nNgrok process terminated.");
        }
    }
}
