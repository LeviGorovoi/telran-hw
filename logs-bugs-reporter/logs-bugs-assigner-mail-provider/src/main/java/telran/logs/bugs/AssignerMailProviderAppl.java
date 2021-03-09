package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RestController
@Slf4j
public class AssignerMailProviderAppl {
@Value("${assigner.email}")
String assignerMail;
	public static void main(String[] args) {
		SpringApplication.run(AssignerMailProviderAppl.class, args);


	}
	@GetMapping("/mail/assigner")
	String getEmail() {
		log.debug("assigner mail is {}", assignerMail);
		return assignerMail;
		
	}
}
