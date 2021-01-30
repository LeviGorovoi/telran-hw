package telran.logs.bugs;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;

@Component
public class RandomLogs {
	@Value("${sec-exception-prob}")
	int secExceptionProb;
	@Value("${exception-prob}")
	int exceptionProb;
	@Value("${authentication-prob}")
	int authenticationProb;
	@Value("${authentication-artifact}")
	String authenticationArtifact;
	@Value("${authorization-artifact}")
	String authorizationArtifact;
	@Value("${class-artifact}")
	String classArtifact;
	
public LogDto createRandomLog() {
	LogType logType = getLogType();
	return new LogDto(new Date(), logType, getArtifact(logType), getResponseTime(logType), "");
}

private int getResponseTime(LogType logType) {
	
	return logType == LogType.NO_EXCEPTION ?
			ThreadLocalRandom.current().nextInt(20, 200):0;
}

private  String getArtifact(LogType logType) {
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
		res.put(LogType.AUTHENTICATION_EXCEPTION, authenticationArtifact);
		break;
	case AUTHORIZATION_EXCEPTION:
		res.put(LogType.AUTHORIZATION_EXCEPTION, authorizationArtifact);
		break;
	case BAD_REQUEST_EXCEPTION:
		res.put(LogType.BAD_REQUEST_EXCEPTION, classArtifact);
		break;
	case DUPLICATED_KEY_EXCEPTION:
		res.put(LogType.DUPLICATED_KEY_EXCEPTION, classArtifact);
		break;
	case  NOT_FOUND_EXCEPTION:
		res.put(LogType. NOT_FOUND_EXCEPTION, classArtifact);
		break;
	case NO_EXCEPTION:
		res.put(LogType. NO_EXCEPTION, classArtifact);
		break;
	case SERVER_EXCEPTION:
		res.put(LogType. SERVER_EXCEPTION, classArtifact);
		break;
	
	
	}
}


private LogType getLogType() {
	int chance = getChance();
	
	return chance <= exceptionProb ? getExceptionLog() : LogType.NO_EXCEPTION;
}

private LogType getExceptionLog() {
	
	return getChance() <= secExceptionProb ? getSecurityExceptionLog() : getNonSecurityExceptionLog();
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
	
	return getChance() <= authenticationProb ? LogType.AUTHENTICATION_EXCEPTION : LogType.AUTHORIZATION_EXCEPTION ;
}

private int getChance() {
	
	return ThreadLocalRandom.current().nextInt(1, 101);
}
}
