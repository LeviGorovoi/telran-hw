package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class AssignerMailProviderAppl {
@Value("${teamleader.email}")
String teamLeaderEmail;
	public static void main(String[] args) {
		SpringApplication.run(AssignerMailProviderAppl.class, args);


	}
	@GetMapping("/email/teamleader")
	String getEmail() {
		return teamLeaderEmail;
		
	}
}
