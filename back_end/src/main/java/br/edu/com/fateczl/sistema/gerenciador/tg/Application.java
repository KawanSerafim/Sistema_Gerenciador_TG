package br.edu.com.fateczl.sistema.gerenciador.tg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {
	static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}