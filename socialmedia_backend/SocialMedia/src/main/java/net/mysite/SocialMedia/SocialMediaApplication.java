package net.mysite.SocialMedia;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class SocialMediaApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {

		run(SocialMediaApplication.class, args);
	}

}
