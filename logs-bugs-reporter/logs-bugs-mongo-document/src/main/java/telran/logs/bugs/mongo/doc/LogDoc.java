package telran.logs.bugs.mongo.doc;

import java.util.Date;
import java.util.Set;

import javax.validation.*;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import telran.logs.bugs.dto.*;

@Document(collection="logs")
public class LogDoc {
	@Id
	ObjectId id;
	public ObjectId getId() {
		return id;
	}
	private Date dateTime;
	private LogType logType;
	private String artifact;
	private int responseTime;
	private String result;

	public LogDoc(LogDto logDto) throws Exception {
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator =  factory.getValidator();
		Set<ConstraintViolation<LogDto>> violations = validator.validate(logDto);
		for(ConstraintViolation<?> violation: violations) {
			if(!violation.getMessage().isEmpty()) {
				throw new Exception("WRONG DTO");
			}			
		}
		
		dateTime = logDto.dateTime; 
		logType = logDto.logType;
		artifact = logDto.artifact;
		responseTime = logDto.responseTime;
		result = logDto.result;
	}
	public LogDto getLogDto () {
		LogDto res = new LogDto(dateTime, logType, artifact, responseTime, result);
		return res;
	}
	public LogDoc(Date dateTime, LogType logType, String artifact, int responseTime, String result) {
		super();
		
		this.dateTime = dateTime;
		this.logType = logType;
		this.artifact = artifact;
		this.responseTime = responseTime;
		this.result = result;
	}
	public LogDoc() {
	}
	public Date getDateTime() {
		return dateTime;
	}
	public LogType getLogType() {
		return logType;
	}
	public String getArtifact() {
		return artifact;
	}
	public int getResponseTime() {
		return responseTime;
	}
	public String getResult() {
		return result;
	}
	
	

}
