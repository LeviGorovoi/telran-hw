package telran.logs.bugs;

import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@EnableAutoConfiguration
@AutoConfigureDataMongo
public class LogsDbPopulatorTest {
	@Autowired
	InputDestination input;

	@Autowired
	LogsRepo logs;
	@Autowired
	LogsDbPopulatorAppl logsDbPopulatorAppl;
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
			assertTrue(actualDoc.getLogDto().logType==LogType.BAD_REQUEST_EXCEPTION);
	}

	@Test
	void docStoreTestNormal() throws Exception {
		LogDto logDto = NormalLogDto();
		LogDoc actualDoc = putGetDto(logDto);
		assertEquals(logDto, actualDoc.getLogDto());
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
	}
	
	@Test
	void docStoreTestNoResultFalse() throws Exception {
		LogDto logDto = NormalLogDto();
		logDto.result = "";
		LogDoc actualDoc = putGetDto(logDto);
		assertFalse(actualDoc.getLogDto().logType==LogType.BAD_REQUEST_EXCEPTION);
	}
	

}
