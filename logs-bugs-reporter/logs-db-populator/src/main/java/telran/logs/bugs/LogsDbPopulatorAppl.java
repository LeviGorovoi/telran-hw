package telran.logs.bugs;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogsRepo;

@SpringBootApplication
public class LogsDbPopulatorAppl {
	@Autowired

	LogsRepo logs;

public static void main(String[] args) {
	SpringApplication.run(LogsDbPopulatorAppl.class, args);
}	

@Bean
Consumer<LogDto> getLogDtoConsumer (){
	return logDto-> logs.save(new LogDoc(logDto));
}

}
	


