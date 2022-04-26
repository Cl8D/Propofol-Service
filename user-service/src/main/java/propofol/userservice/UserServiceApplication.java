package propofol.userservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// jpaAuditing 활성화
@EnableJpaAuditing
// Eureka Client 활성화
//@EnableEurekaClient
@ConfigurationPropertiesScan(basePackages = "propofol.userservice.api.common.properties")
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	// modelMapper 활성화를 위한 bean 등록
	@Bean
	public ModelMapper createModelMapper(){
		return new ModelMapper();
	}

	// password 암호화를 위한 bcrypt 어쩌고 bean 등록
	@Bean
	public BCryptPasswordEncoder createEncoder() {
		return new BCryptPasswordEncoder();
	}

	// restTemplate Bean 등록
	@Bean
	public RestTemplate createRestTemplate() {
		return new RestTemplate();
	}
}
