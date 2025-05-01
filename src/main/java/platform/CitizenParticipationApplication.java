package platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "exception", "security"
})
public class CitizenParticipationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitizenParticipationApplication.class, args);
    }

}
