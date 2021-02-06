package telran.logs.bugs;

import java.util.function.Consumer;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.repo.BugsRepo;
import telran.logs.bugs.service.interfaces.BugReportFactory;

@SpringBootApplication
@EntityScan("telran.logs.bugs.jpa.dto")
public class BugsOpenningAppl {
	static Logger LOG = LoggerFactory.getLogger(BugsOpenningAppl.class);
	@Autowired
	BugReportFactory bugReportFactory;
	@Autowired
	BugsRepo bugsRepo;

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(BugsOpenningAppl.class, args);
		JPQLQueryConsole console = ctx.getBean(JPQLQueryConsole.class);
		console.run();
	}
@Bean
	Consumer<LogDto> getLogDtoConsumer() {
		return this::createAndSaveBugReport;
	}

 void createAndSaveBugReport (LogDto logDto) {
	 LOG.debug("Bugs Opening service has recieved log: {}", logDto);
	Bug bug = bugReportFactory.createBugReport(logDto);
	bugsRepo.save(bug);
}
}