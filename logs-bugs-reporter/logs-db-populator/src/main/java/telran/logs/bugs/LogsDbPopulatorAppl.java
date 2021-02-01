package telran.logs.bugs;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootApplication
public class LogsDbPopulatorAppl {
	@Autowired
	LogsRepo logs;
	@Autowired
	Validator validator;	
	@Value("${app-binding-name:exceptions-out-0}")
	String bindingName;
	@Autowired
StreamBridge streamBridge;
public static void main(String[] args) {
	SpringApplication.run(LogsDbPopulatorAppl.class, args);

}	


@Bean
Consumer<LogDto> getLogDtoConsumer (){
	return logDto -> {
		try {
			takeAndSaveLogDto(logDto);
		} catch (Exception e) {
			throw new  RuntimeException(e);
		}
	};
}

void takeAndSaveLogDto( LogDto logDto) throws Exception {
	Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
	for(ConstraintViolation<?> violation: violations) {
		if(!violation.getMessage().isEmpty()) {
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, "LogsDbPopulatorAppl", 0, violation.getMessage());
			streamBridge.send(bindingName, logDto);
		}	
		
	}
	logs.save(new LogDoc(logDto));
//	streamBridge.send("j", logDto);
}
}
	


