package telran.logs.bugs;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)

public class LogsDbPopulatorTest {
	static Logger LOG = LoggerFactory.getLogger(LogsDbPopulatorTest.class);
	@Autowired
	InputDestination input;

	@Autowired
	LogsRepo logs;
	@Autowired
	LogsDbPopulatorAppl logsDbPopulatorAppl;

	LogDto NormalLogDto() {
		return new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
	}

	private LogDoc putGetDto(LogDto logDto) {
		logs.deleteAll();
		input.send(new GenericMessage<LogDto>(logDto));
		return logs.findAll().get(0);
	}


	@Test
	void docStoreTest() throws Exception {
		LogDto logDto = NormalLogDto();
		LogDoc actualDoc = putGetDto(logDto);
		assertEquals(logDto, actualDoc.getLogDto());		
	}

	

}
