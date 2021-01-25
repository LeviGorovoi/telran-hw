package telran.logs.provider;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.provider.RandomLogs;

@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@ContextConfiguration(classes = RandomLogs.class)
class LogsProviderTest {
	private static final String AUTHENTICATION_ARTIFACT = "authentication";
	private static final String AUTHORIZATION_ARTIFACT = "authorization";
	private static final String CLASS_ARTIFACT = "class";
	private static final long N_LOGS = 100000;
	@Autowired
	RandomLogs randomLogs;

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

	private EnumMap<LogType, String> getMapForTest() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method getMapMethod = randomLogs.getClass().getDeclaredMethod("getLogArtifactMap");
		getMapMethod.setAccessible(true);
		@SuppressWarnings("unchecked")
		EnumMap<LogType, String> logTypeArtifactsMap = (EnumMap<LogType, String>) getMapMethod.invoke(randomLogs);
		return logTypeArtifactsMap;
	}

	@Test
	void generation() throws Exception {

		List<LogDto> logs = Stream.generate(() -> {
			try {
				return randomLogs.createRandomLog();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).limit(N_LOGS).collect(Collectors.toList());
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

}
