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
import telran.logs.bugs.service.impl.MethodsForGettingBugFromLogDtoImpl;
import telran.logs.bugs.service.interfaces.MethodsForGettingBugFromLogDto;

@SpringBootApplication
@EntityScan("telran.logs.bugs.jpa.dto")
public class BugsOpenningAppl {
	static Logger LOG = LoggerFactory.getLogger(MethodsForGettingBugFromLogDtoImpl.class);
	@Autowired
	MethodsForGettingBugFromLogDto methods;
	@Autowired
	BugsRepo bugsRepo;
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(BugsOpenningAppl.class, args);
		JPQLQueryConsole console = ctx.getBean(JPQLQueryConsole.class);
		console.run();
	}
@Bean
	Consumer<LogDto> getLogDtoConsumer() {
		return this::createBug;
	}

 void createBug (LogDto logDto) {
	Bug bug = Bug.builder().dateOpen(methods.getDateOpen()).dateClose(methods.getDateClose()).seriousness(methods.getSeriousness(logDto))
			.programmer(methods.getProgrammer(logDto)).status(methods.getStatus(logDto)).openningMethod(methods.getOpenningMethod()).
			description(methods.getDescription(logDto)).build();
	bugsRepo.save(bug);
}
}