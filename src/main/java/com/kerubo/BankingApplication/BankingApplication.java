package com.kerubo.BankingApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
info = @Info(
		title = "Kerubo Banking application",
		description = "Backend REST APIS for the bank",
		version = "v1.0",
		contact = @Contact(
				name = "Monicah Kerubo",
				email = "moniquenyakundi@gmail.com",
				url = "https://github.com/Monique-199/BankingApplication"
		),
		license = @License(
				name = "Kerubo",
				url = "https://github.com/Monique-199/BankingApplication"

		)


),
		externalDocs =@ExternalDocumentation(
				description = "Kerubo bank app documentation",
				url = "https://github.com/Monique-199/BankingApplication"
		)


)
public class BankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

}
