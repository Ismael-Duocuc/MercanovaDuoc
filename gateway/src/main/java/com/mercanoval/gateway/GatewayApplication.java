package com.mercanoval.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;

import java.util.Map;

@SpringBootApplication
public class GatewayApplication {

	private static final Logger logger = LoggerFactory.getLogger(GatewayApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public CommandLineRunner debugRoutes(ApplicationContext ctx) {
		return args -> {
			Map<String, RouterFunction> beans = ctx.getBeansOfType(RouterFunction.class);
			logger.info("=== DIAGNOSTICO GATEWAY: {} bean(s) RouterFunction encontrados ===", beans.size());
			for (Map.Entry<String, RouterFunction> entry : beans.entrySet()) {
				logger.info("=== RouterFunction bean '{}': {} ===", entry.getKey(), entry.getValue());
			}
			if (beans.isEmpty()) {
				logger.info("=== DIAGNOSTICO: NO se registro ningun RouterFunction. Las rutas del YAML no se cargaron. ===");
			}
		};
	}

}

