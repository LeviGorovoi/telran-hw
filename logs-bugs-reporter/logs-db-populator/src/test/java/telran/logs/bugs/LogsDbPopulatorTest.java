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
import org.springframework.messaging.Message;
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
	@Autowired
	OutputDestination consumer;
	@Value("${binding-name}")
	String bindingName;
	String expectedExcepnionMessage = "WRONG DTO";

	LogDto NormalLogDto() {
		return new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 20, "result");
	}

	private LogDoc putGetDto(LogDto logDto) {
		logs.deleteAll();
		input.send(new GenericMessage<LogDto>(logDto));
		return logs.findAll().get(0);
	}

	private void catchException(LogDto logDto) {
		LogDoc actualDoc = putGetDto(logDto);
		assertTrue(actualDoc.getLogDto().logType == LogType.BAD_REQUEST_EXCEPTION);
		assertNotNull(receiveMessage());
	}
	private Message<byte[]> receiveMessage() {
		Message<byte[]> message = consumer.receive(0, bindingName);
		
		if(message!=null) {
		LOG.debug("receved in consumer {}", new String(message.getPayload()));
		}
		return message;
		
	}

	@Test
	void docStoreTestNormal() throws Exception {
		LogDto logDto = NormalLogDto();
		LogDoc actualDoc = putGetDto(logDto);
		assertEquals(logDto, actualDoc.getLogDto());
		assertNull(receiveMessage());
	}

	@Test
	void docStoreTestNoDate() throws Exception {
		LogDto logDto = NormalLogDto();
		logDto.dateTime = null;
		catchException(logDto);


	}

	@Test
	void docStoreTestNoLogType() throws Exception {
		LogDto logDto = NormalLogDto();
		logDto.logType = null;
		catchException(logDto);

	}

	@Test
	void docStoreTestEmptyArtifact() throws Exception {
		LogDto logDto = NormalLogDto();
		logDto.artifact = "";
		catchException(logDto);
	}

	@Test
	void docStoreTestNoResult() throws Exception {
		LogDto logDto = NormalLogDto();
		logDto.result = "";
		LogDoc actualDoc = putGetDto(logDto);
		assertTrue(logDto.result.contains(actualDoc.getLogDto().result));
		assertNull(receiveMessage());
	}

	@Test
	void docStoreTestNoResultFalse() throws Exception {
		LogDto logDto = NormalLogDto();
		logDto.result = "";
		LogDoc actualDoc = putGetDto(logDto);
		assertFalse(actualDoc.getLogDto().logType == LogType.BAD_REQUEST_EXCEPTION);
	}

}
