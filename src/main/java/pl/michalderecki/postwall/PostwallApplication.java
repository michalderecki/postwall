package pl.michalderecki.postwall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class PostwallApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostwallApplication.class, args);
	}
}
