package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import telran.logs.bugs.interfaces.LogsInfo;

@SpringBootApplication
public class LogsInfoAppl {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(LogsInfoAppl.class, args);
		LogsInfo li = context.getBean(LogsInfo.class);
		li.getMostEncounteredArtifacts(2).block();

	}

}
