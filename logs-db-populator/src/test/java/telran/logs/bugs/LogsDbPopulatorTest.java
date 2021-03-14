package telran.logs.bugs;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;


import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.repo.LogsRepo;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)

public class LogsDbPopulatorTest {
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorTest.class);
	@Autowired
	InputDestination input;
	LogDto [] logArr = new LogDto[10];
	

	@Autowired
	LogsRepo logsRepo;
	@Autowired
	LogsDbPopulatorAppl logsDbPopulatorAppl;

	@BeforeEach
	void setUp() {
		logsRepo.deleteAll();
	}

	@Test
	void takeLogDtoAndSave() {
		LogDto logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "");
		for (int i = 0; i<1; i++) {
			logDto.result= ""+i;
			sendLog(logDto);
			LogDto actual = logsRepo.findAll().blockLast().getLogDto();
			assertEquals(logDto.result, actual.result);
		}
		

	}

	
	private void sendLog(LogDto logDto) {
		input.send(new GenericMessage<LogDto>(logDto));
	}

	

}