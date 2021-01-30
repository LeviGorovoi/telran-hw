package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import telran.logs.bugs.dto.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class RandomLogsTest {
	@Value("${authentication-artifact}")
	private String authenticationArtifact;
	@Value("${authorization-artifact:y}")
	private String authorizationArtifact;
	@Value("${class-artifact:z}")
	private String classArtifact;
	@Value("${n-logs:1}")
	private  long nLogs;
	@Value("${n-generated-dto:1}")
	private int nFeneratedDto;
	@Autowired
	RandomLogs randomLogs;
	@Autowired
	OutputDestination output;
	static Logger LOG = LoggerFactory.getLogger(RandomLogsTest.class);

	@Test
	void logTypeArtifactTest() throws Exception {

		EnumMap<LogType, String> logTypeArtifactsMap = getMapForTest();
		logTypeArtifactsMap.forEach((k, v) -> {
			switch (k) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(authenticationArtifact, v);
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(authorizationArtifact, v);
				break;
			default:
				assertEquals(classArtifact, v);

			}
		});
	}

	private EnumMap<LogType, String> getMapForTest()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method getMapMethod = randomLogs.getClass().getDeclaredMethod("getLogArtifactMap");
		getMapMethod.setAccessible(true);
		@SuppressWarnings("unchecked")
		EnumMap<LogType, String> logTypeArtifactsMap = (EnumMap<LogType, String>) getMapMethod.invoke(randomLogs);
		return logTypeArtifactsMap;
	}

	@Test
	void generation() throws Exception {

		List<LogDto> logs = Stream.generate(() -> randomLogs.createRandomLog()).limit(nLogs)
				.collect(Collectors.toList());
		testLogContent(logs);
		Map<LogType, Long> logTypeOccurrences = logs.stream()
				.collect(Collectors.groupingBy(l -> l.logType, Collectors.counting()));
		logTypeOccurrences.forEach((k, v) -> {
			LOG.info("LogType: {}, count:{}", k, v);
//			System.out.printf("LogType: %s, count: %d\n", k, v);
		});
		assertEquals(LogType.values().length, logTypeOccurrences.entrySet().size());

	}

	private void testLogContent(List<LogDto> logs) {
		logs.forEach(log -> {
			switch (log.logType) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(authenticationArtifact, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(authorizationArtifact, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;

			case NO_EXCEPTION:
				assertEquals(classArtifact, log.artifact);
				assertTrue(log.responseTime > 0);
				assertTrue(log.result.isEmpty());
				break;

			default:
				assertEquals(classArtifact, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;

			}
		});
	}

	Thread[] threadArr = new Thread[nFeneratedDto];

	
	private Set<String> createSetFromDtoStrings(){		
Set<String>	stringsOfDto = new HashSet<>();
for (int i = 0; i < 10; i++) {
	while (true) {
	byte[] messageBytes;
	try {
		messageBytes = output.receive(1000).getPayload();
		if (messageBytes.length != 0) {
			String messageStr = new String(messageBytes);
			stringsOfDto.add(messageStr);
			LOG.info(messageStr);
			break;
		}
	} catch (NullPointerException e) {
	}
	}
}
return stringsOfDto;
	}	
@Test
void sendRandomLogs() {
	Set<String>	stringsOfDto = createSetFromDtoStrings();
	assertEquals(nFeneratedDto, stringsOfDto.size());

}

}
