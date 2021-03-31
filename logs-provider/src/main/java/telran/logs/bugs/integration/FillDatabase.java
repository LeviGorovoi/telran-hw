package telran.logs.bugs.integration;

import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.repo.ArtifactRepository;
import telran.logs.bugs.repo.ProgrammerRepository;

@Component
public class FillDatabase {
	ArtifactRepository artifactRepository;
	ProgrammerRepository programmerRepository;
	@Autowired
	public FillDatabase(ArtifactRepository artifactRepository,
			ProgrammerRepository programmerRepository) {
		super();
		this.artifactRepository = artifactRepository;
		this.programmerRepository = programmerRepository;
	}
	@Value("${app-integration-test-enable:true}")
boolean integrationTest;
	String[] programmerNames = {"Aharon", "Abba", "Avraham", "Adam", "Akiva", "Alexander", "Alon", "Alter",
			"Amos", "Amram", "Ariel", "Asher", "Avi", "Avigdor", "Avner", "Azriel", "Barak"};
	String[] artifacts = {"authentication", "authorization", "class1", "class2", "class3", "class4", "class5", "class6",
			"class7", "class8", "class9", "class10", "class11", "class12", "class13", "class14", "class15", "class16", "class17",
			"class18", "class19", "class20"};
	
	@PostConstruct
	private void fillArtifactsAndProgrammersDb() {
	if(!integrationTest) {
		return;
	}
		for(int i = 0; i<programmerNames.length; i++ ) {
			if(programmerRepository.findByProgrammerId(i)==null) {
			programmerRepository.save(new Programmer(i, programmerNames[i],"llevi9973+"+programmerNames[i]+i+"@gmail.com"));
			}
		}
		for(String artifactId:artifacts) {
			artifactRepository.save(new Artifact(artifactId, programmerRepository.findByProgrammerId((long) new Random().nextInt(programmerNames.length))));
		}
	}
}
