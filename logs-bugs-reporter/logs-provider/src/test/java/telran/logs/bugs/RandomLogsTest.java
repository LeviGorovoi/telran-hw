package telran.logs.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import telran.logs.bugs.dto.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class RandomLogsTest {
	private static final String AUTHENTICATION_ARTIFACT = "authentication";
	private static final String AUTHORIZATION_ARTIFACT = "authorization";
	private static final String CLASS_ARTIFACT = "class";
	private static final long N_LOGS = 100000;
	private static final int N_GENERATED_DTO = 10;
	@Autowired
	RandomLogs randomLogs;
	@Autowired
	OutputDestination output;

	@Test
	void logTypeArtifactTest() throws Exception {

		EnumMap<LogType, String> logTypeArtifactsMap = getMapForTest();
		logTypeArtifactsMap.forEach((k, v) -> {
			switch (k) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(AUTHENTICATION_ARTIFACT, v);
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(AUTHORIZATION_ARTIFACT, v);
				break;
			default:
				assertEquals(CLASS_ARTIFACT, v);

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

		List<LogDto> logs = Stream.generate(() -> randomLogs.createRandomLog()).limit(N_LOGS)
				.collect(Collectors.toList());
		testLogContent(logs);
		Map<LogType, Long> logTypeOccurrences = logs.stream()
				.collect(Collectors.groupingBy(l -> l.logType, Collectors.counting()));
		logTypeOccurrences.forEach((k, v) -> {
			System.out.printf("LogType: %s, count: %d\n", k, v);
		});
		assertEquals(LogType.values().length, logTypeOccurrences.entrySet().size());

	}

	private void testLogContent(List<LogDto> logs) {
		logs.forEach(log -> {
			switch (log.logType) {
			case AUTHENTICATION_EXCEPTION:
				assertEquals(AUTHENTICATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;
			case AUTHORIZATION_EXCEPTION:
				assertEquals(AUTHORIZATION_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;

			case NO_EXCEPTION:
				assertEquals(CLASS_ARTIFACT, log.artifact);
				assertTrue(log.responseTime > 0);
				assertTrue(log.result.isEmpty());
				break;

			default:
				assertEquals(CLASS_ARTIFACT, log.artifact);
				assertEquals(0, log.responseTime);
				assertTrue(log.result.isEmpty());
				break;

			}
		});
	}

	Thread[] threadArr = new Thread[N_GENERATED_DTO];

	
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
			System.out.println(messageStr);
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
	assertEquals(N_GENERATED_DTO, stringsOfDto.size());

}

}
