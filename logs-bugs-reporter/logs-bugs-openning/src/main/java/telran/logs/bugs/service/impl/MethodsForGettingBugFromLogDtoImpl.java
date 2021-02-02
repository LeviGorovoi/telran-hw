package telran.logs.bugs.service.impl;



import java.time.LocalDate;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpenningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;
import telran.logs.bugs.repo.ArtifactsRepo;
import telran.logs.bugs.service.interfaces.MethodsForGettingBugFromLogDto;

@Service
@EntityScan("telran.logs.bugs.jpa.entities")
public class MethodsForGettingBugFromLogDtoImpl implements MethodsForGettingBugFromLogDto {
	@Autowired
ArtifactsRepo artifacts;
	@Override
	public BugStatus getStatus(LogDto logDto) {
		if(getProgrammer(logDto)==null) {
			return BugStatus.OPENNED;
		}
		return BugStatus.ASSIGNED;
	}

	@Override
	public String getDescription(LogDto logDto) {
		return logDto.logType + " " +logDto.result;
	}

	@Override
	public Programmer getProgrammer(LogDto logDto) {
		return artifacts.findByArtifactId(logDto.artifact);
	}

	@Override
	public Seriousness getSeriousness(LogDto logDto) {
		switch(logDto.logType) {
		case AUTHENTICATION_EXCEPTION : return Seriousness.BLOCKING;
		case SERVER_EXCEPTION : return Seriousness.CRITICAL;
		case AUTHORIZATION_EXCEPTION : return Seriousness.CRITICAL;
		default:
			return Seriousness.MINOR;
		}
	}

	@Override
	public LocalDate getDateOpen() {
		return LocalDate.now();
	}

	@Override
	public LocalDate getDateClose() {
		return null;
	}

	@Override
	public OpenningMethod getOpenningMethod() {
		return OpenningMethod.AUTOMATIC;
	}
	


	
}
