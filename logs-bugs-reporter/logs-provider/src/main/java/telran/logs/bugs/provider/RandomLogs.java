package telran.logs.bugs.provider;

import java.util.Date;

import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.LogDoc;

@Component
public class RandomLogs {
	public static final String AUTHENTICATION = "authentication";
	public static final String AUTHORIZATION = "authorization";
	private static final int MAX = 100;
	private static final int MIN = 0;
	
	private Date dateTime;
	private LogType logType;
	private String artifact;
	private int responseTime;
	private String result;
	
	double exceptionProb;
	double secExceptionProb;
	double authentSecExceptionProb;
	double rendomNumber;
	
	
	public RandomLogs(double exceptionProb, double secExceptionProb, double authentSecExceptionProb,
			double rendomNumber) {
		super();
		this.exceptionProb = exceptionProb;
		this.secExceptionProb = secExceptionProb;
		this.authentSecExceptionProb = authentSecExceptionProb;
		this.rendomNumber = rendomNumber;
	}
	
	

	public void setRendomNumber(double rendomNumber) {
		this.rendomNumber = rendomNumber;
	}



	public LogDoc createRandomLog() throws Exception {
		dateTime = new Date();
		LogType[] logTypes = LogType.values();
		logType = logTypes[generateIndexOfLogTypes(logTypes.length)];
		artifact = generateArtifact(logType);
		responseTime = getResponseTime();
		result = "";
		LogDto logDto = new LogDto(dateTime, logType, artifact, responseTime, result);
		return new LogDoc(logDto);
	}
	
	private int getResponseTime() {
		if(logType.equals(LogType.NO_EXCEPTION)){
			return 1;
		}
		return 0;
	}

	private String generateArtifact(LogType logType) {
		if(logType.equals(LogType.AUTHENTICATION_EXCEPTION)) {
			return AUTHENTICATION;
		}
		if(logType.equals(LogType.AUTHORIZATION_EXCEPTION)) {
			return AUTHORIZATION;
		}
		return "class"+ (int) ((rendomNumber * (MAX - MIN)) + MIN);
	}

	private int generateIndexOfLogTypes(int logTypeLength) {
		if(rendomNumber>=1-exceptionProb*secExceptionProb&&
				rendomNumber<1-exceptionProb*secExceptionProb + exceptionProb*secExceptionProb*authentSecExceptionProb) {
			return logTypeLength-2;
		}
		if(rendomNumber>=1-exceptionProb*secExceptionProb + exceptionProb*secExceptionProb*authentSecExceptionProb) {
			return logTypeLength-1;					
		}
		double probabilityOfRemainingOutcomes = (exceptionProb-exceptionProb*secExceptionProb)/(logTypeLength-3);
		for(int i = 0; i<logTypeLength-3;i++) {
			if(rendomNumber>=(1-exceptionProb)+i*probabilityOfRemainingOutcomes
					&&rendomNumber<(1-exceptionProb)+(i+1)*probabilityOfRemainingOutcomes) {
				return 1+i;
			}
		}
		return 0;
	}
}
