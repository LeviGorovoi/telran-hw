package telran.logs.bugs.provider;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.validation.constraints.NotEmpty;

import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Component
public class RandomLogs {
	private static final double EXCEPTION_PROB = 10;
	private static final double SEC_EXCEPTION_PROB = 30;
	private static final double AUTHENT_SEC_EXCEPTION_PROB = 70;


	public LogDto createRandomLog() throws Exception {
		LogType logType = getLogType();
		return new LogDto(new Date(), logType, getArtifact(logType),  getResponseTime(logType), "");
	}
	
	private @NotEmpty String getArtifact(LogType logType) {
		EnumMap<LogType, String> logArtifact = getLogArtifactMap();
		return logArtifact.get(logType);
		
		
	}

	private EnumMap<LogType, String> getLogArtifactMap() {
		EnumMap<LogType, String> res = new EnumMap<>(LogType.class);
		Arrays.asList(LogType.values()).forEach(lt -> {
			fillLogTypeArtifactMap(res, lt);
		});
		return res;
	}

	private void fillLogTypeArtifactMap(EnumMap<LogType, String> res, LogType lt) {
		switch(lt) {
		case AUTHENTICATION_EXCEPTION:
			res.put(LogType.AUTHENTICATION_EXCEPTION, "authentication");
			break;
		case AUTHORIZATION_EXCEPTION:
			res.put(LogType.AUTHORIZATION_EXCEPTION, "authorization");
			break;
		case BAD_REQUEST_EXCEPTION:
			res.put(LogType.BAD_REQUEST_EXCEPTION, "class");
			break;
		case DUPLICATED_KEY_EXCEPTION:
			res.put(LogType.DUPLICATED_KEY_EXCEPTION, "class");
			break;
		case NOT_FOUND_EXCEPTION:
			res.put(LogType. NOT_FOUND_EXCEPTION, "class");
			break;
		case NO_EXCEPTION:
			res.put(LogType. NO_EXCEPTION, "class");
			break;
		case SERVER_EXCEPTION:
			res.put(LogType. SERVER_EXCEPTION, "class");
			break;
		default:
			break;
		
		}
		
	}

	private LogType getLogType() {
	return getChance()<=EXCEPTION_PROB?getExceptionLog():LogType.NO_EXCEPTION;
	}

	private LogType getExceptionLog() {
		return getChance()<=SEC_EXCEPTION_PROB?getSecurityExceptionLog() : getNonSecurityExceptionLog();
	}

	private LogType getNonSecurityExceptionLog() {
		LogType nonSecExceptions[] = {
				LogType.BAD_REQUEST_EXCEPTION, LogType.DUPLICATED_KEY_EXCEPTION,
				LogType.NOT_FOUND_EXCEPTION, LogType.SERVER_EXCEPTION
		};
		int ind = ThreadLocalRandom.current().nextInt(0, nonSecExceptions.length);
		return nonSecExceptions[ind];
	}

	private LogType getSecurityExceptionLog() {
		return getChance()<=AUTHENT_SEC_EXCEPTION_PROB?LogType.AUTHORIZATION_EXCEPTION:LogType.AUTHENTICATION_EXCEPTION;
	}

	private int getChance() {
		return ThreadLocalRandom.current().nextInt(1, 101);
	}

	private int getResponseTime(LogType logType) {
		return logType==LogType.NO_EXCEPTION?ThreadLocalRandom.current().nextInt(20, 200):0;
	}




}
