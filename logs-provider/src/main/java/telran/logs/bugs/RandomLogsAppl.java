package telran.logs.bugs;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.random.RandomLogs;

@SpringBootApplication
@Slf4j
public class RandomLogsAppl {
@Autowired
RandomLogs randomLogs;
	public static void main(String[] args) {
		SpringApplication.run(RandomLogsAppl.class, args);

	}
	@Bean
Supplier<LogDto> random_logs_provider() {
	return this::sendRandomLog;
}
	LogDto sendRandomLog() {
		LogDto logDto = randomLogs.createRandomLog();
		log.debug("sent log: {}", logDto);
		return logDto;
	}
}
