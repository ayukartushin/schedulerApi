package su.kartushin.busAPI;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Log4j2
@EnableJpaRepositories(basePackages = "su.kartushin.busAPI.repositories")
@SpringBootApplication(scanBasePackages = "su.kartushin.busAPI")
public class BusAPIApplication {

    public static void main(String[] args) {
        // Запуск Spring Boot приложения
        log.info("Приложение запущенно.");
        SpringApplication.run(BusAPIApplication.class, args);
    }
}