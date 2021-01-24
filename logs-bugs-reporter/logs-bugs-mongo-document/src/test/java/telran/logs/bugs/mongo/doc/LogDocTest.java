package telran.logs.bugs.mongo.doc;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=LogsRepo.class)
@EnableAutoConfiguration 
@AutoConfigureDataMongo
public class LogDocTest {
@Autowired
LogsRepo logs;
//LogDto logDto;	
//LogDoc actualDoc;
String expectedExcepnionMessage = "WRONG DTO";

LogDto NormalLogDto() {
	return  new LogDto(new Date(), LogType.NO_EXCEPTION, "artifact",
			20, "result");
}

private LogDoc logDtoEnterExit(LogDto logDto) throws Exception{
	logs.deleteAll();
	try {
		logs.save( new LogDoc(logDto));
	} catch (Exception e) {
		System.out.println(e.getMessage());
		throw new Exception (e);
	}
	return logs.findAll().get(0);
}
private void catchException(LogDto logDto) {
	try {
		logDtoEnterExit(logDto);
	} catch (Exception e) {
		assertTrue(e.getMessage().contains(expectedExcepnionMessage));
	}
}

@Test
void docStoreTestNormal() throws Exception {
	LogDto logDto =  NormalLogDto();
	LogDoc actualDoc = logDtoEnterExit(logDto);
	assertEquals(logDto, actualDoc.getLogDto());	
}

@Test
void docStoreTestNoDate() throws Exception {
	LogDto logDto =  NormalLogDto();
	logDto.dateTime = null;
	catchException(logDto);	
}

@Test
void docStoreTestNoLogType() throws Exception {	
	LogDto logDto =  NormalLogDto();
	logDto.logType = null;
	catchException(logDto);	
}

@Test
void docStoreTestEmptyArtifact() throws Exception {	
	LogDto logDto =  NormalLogDto();
	logDto.artifact = "";
	catchException(logDto);	
}

@Test
void docStoreTestNoResult() throws Exception {
	LogDto logDto =  NormalLogDto();
	logDto.result = "";
	LogDoc actualDoc = logDtoEnterExit(logDto);	
	System.out.println(logDto.result);
	System.out.println(actualDoc.getLogDto().result);
	assertTrue(logDto.result.contains(actualDoc.getLogDto().result) );	
}
}
