package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.services.LogsAnalyzerService;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class LogsAnalyzerTest {
	static Logger LOG = LoggerFactory.getLogger(LogsAnalyzerTest.class);
	@Autowired
	InputDestination producer;
	@Autowired
	OutputDestination consumer;

	@Value("${binding.names}")
	String[] bindingNames;
	LogDto logDto = new LogDto();
	LogDto logDtoBadRequestException = new LogDto();

	@BeforeEach
	void setup() {
		for (String topic : bindingNames) {
			consumer.clear(topic);
		}
		logDto = new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact", 0, "result");

	}

	private LogDto logDtoBadRequestException(LogDto logDto) {
		return new LogDto(new Date(), LogType.BAD_REQUEST_EXCEPTION, LogsAnalyzerService.class.getName(), 0,
				logDto.toString());
	}

	private void validationTests(LogDto logDto) {
		producer.send(new GenericMessage<LogDto>(logDto));
		Message<byte[]> message = consumer.receive(0, bindingNames[1]);
		assertEquals(logDtoBadRequestException(logDto), new String(message.getPayload()));

	}

	private Message<byte[]> reseiveSendLog(String topic) {
		producer.send(new GenericMessage<LogDto>(logDto));
		Message<byte[]> message = consumer.receive(0, topic);
		LOG.debug("receved in consumer {}", new String(message.getPayload()));
		return message;
	}

	@Test
	void valueAnnotationAsArrayTest() {
		assertArrayEquals(new String[] { "exceptions-out-100", "exceptions-out-1", "exceptions-out-0",
				"exceptions-out-0", "exceptions-out-0", "exceptions-out-0", "exceptions-out-0" }, bindingNames);
	}

	@Test
	void analyzerTestNonException() {
		assertNotNull(reseiveSendLog(bindingNames[0]));
	}

	@Test
	void analyzerTestBadRequestException() {
		logDto.logType = LogType.BAD_REQUEST_EXCEPTION;
		assertNotNull(reseiveSendLog(bindingNames[1]));
	}

	@Test
	void analyzerTestAuthenticationException() {
		logDto.logType = LogType.AUTHENTICATION_EXCEPTION;
		assertNotNull(reseiveSendLog(bindingNames[6]));
	}

	// validation tests
	@Test
	void noDateTest() {
		logDto.dateTime = null;
		logDto.logType = null;
		validationTests(logDto);

	}
}
