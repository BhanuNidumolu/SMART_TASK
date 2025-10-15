// FINAL & NEW: Added main application class
package springai.smart_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Enables background task processing for the AI
public class SmartTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTaskApplication.class, args);
    }

}