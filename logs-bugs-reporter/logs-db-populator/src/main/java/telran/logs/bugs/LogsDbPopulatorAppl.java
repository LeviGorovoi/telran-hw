package telran.logs.bugs;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorAppl.class);
	@Autowired
	LogsRepo logs;
	@Autowired
	Validator validator;	
	@Value("${binding-name:exceptions-out-0}")
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
			LOG.error("logDto : {}; field: {}; message: {}",logDto,
					violation.getPropertyPath(), violation.getMessage());
			logDto = new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, LogsDbPopulatorAppl.class.getName(), 0, violation.getMessage());
			streamBridge.send(bindingName, logDto);
			LOG.debug("log: {} sent to binding name: {}", logDto, bindingName);
		}	
		 	 	
	}
	logs.save(new LogDoc(logDto));
}
}
	


