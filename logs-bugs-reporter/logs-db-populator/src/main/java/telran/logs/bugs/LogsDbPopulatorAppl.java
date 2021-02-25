package telran.logs.bugs;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import reactor.core.scheduler.Schedulers;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);
	@Autowired
	LogsRepo logs;

public static void main(String[] args) {
	SpringApplication.run(LogsDbPopulatorAppl.class, args);
}	

@Bean
Consumer<LogDto> getLogDtoConsumer() {
	return t -> {
		try {
			takeAndSaveLogDto(t);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
}

@Autowired
LogsRepo logsRepository;

void takeAndSaveLogDto(LogDto logDto) throws InterruptedException {
	// taking and saving to MongoDB logDto
	LOG.debug("received log: {}", logDto);
	logsRepository.save(new LogDoc(logDto)).subscribe(log -> LOG.debug("log: {} saved to Mongo collection", log.getLogDto()));
	LOG.debug("start saving log and finishing of takeAndSaveLogDto method");

}

}
	


