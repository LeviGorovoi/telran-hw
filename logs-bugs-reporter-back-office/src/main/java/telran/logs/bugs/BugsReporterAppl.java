package telran.logs.bugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

import telran.logs.bugs.interfaces.BugsReporter;

@SpringBootApplication
@EntityScan("telran.logs.bugs.jpa.entities")
public class BugsReporterAppl {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BugsReporterAppl.class, args);
		 context.getBean(BugsReporter.class).getEmailBugsCounts();
		 

	}

}
