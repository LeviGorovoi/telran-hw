package telran.logs.bugs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class AssignerMailProviderTest {
	@Autowired
	WebTestClient webClient;
		@Test
		void emailExisting() {
			webClient.get().uri("/email/teamleader")
			.exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("boss@gmail.com");
		}

}
