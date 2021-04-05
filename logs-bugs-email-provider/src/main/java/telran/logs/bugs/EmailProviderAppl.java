package telran.logs.bugs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.repo.ArtifactRepository;

@SpringBootApplication
@RestController
@Slf4j
public class EmailProviderAppl {
	@Autowired
	ArtifactRepository artifactRepository;
	public static void main(String[] args) {
		SpringApplication.run(EmailProviderAppl.class, args);


	}
	@GetMapping("/mail/{artifact}")
	String getEmail(@PathVariable(name="artifact") String artifact) {
		Artifact artifactEntity = artifactRepository.findById(artifact).orElse(null);
		String artifactId;
		String email;
		if(artifactEntity == null) {
			artifactId = "artifactId not found ";
			email = "";
		} else {
			artifactId = artifactEntity.getArtifactId();
			email = artifactEntity.getProgrammer().getEmail();
		}
		log.debug("artifact is {}, programmer mail is {}", artifactId, email);
		return email;
		
	}
}
