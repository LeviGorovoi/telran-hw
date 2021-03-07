package telran.logs.bugs.service.impl;

import java.time.LocalDate;



import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.*;
import telran.logs.bugs.repo.ArtifactsRepo;
import telran.logs.bugs.service.interfaces.BugReportFactory;
import telran.logs.bugs.service.interfaces.ConvertorFromLogDtoToBugReport;

public class ConvertorFromLogDtoToBugReportImpl implements ConvertorFromLogDtoToBugReport {

	ArtifactsRepo artifacts;
	private boolean isDatabaseChecked = false;
	BugReportFactory factory = new BugReportFactoryImpl();
	Programmer programmer;
	LogDto logDto;
	
	public ConvertorFromLogDtoToBugReportImpl(LogDto logDto, ArtifactsRepo artifacts) {
		super();
		this.logDto = logDto;
		this.artifacts = artifacts;
	}

	private Programmer findProgrammerInDataBase() {
		if (!isDatabaseChecked) {
			programmer = artifacts.findByArtifactId(logDto.artifact);
			isDatabaseChecked = true;
		}
		return programmer;
	}
	
	@Override
	public BugStatus getStatus() {
			programmer = findProgrammerInDataBase();
		if (programmer == null) {
			return BugStatus.OPENNED;
		}
		return BugStatus.ASSIGNED;
	}

	@Override
	public String getDescription() {
		return logDto.logType + " " + logDto.result;
	}

	@Override
	public Programmer getProgrammer() {		 
		return findProgrammerInDataBase();
	}

	@Override
	public Seriousness getSeriousness() {
		switch (logDto.logType) {
		case AUTHENTICATION_EXCEPTION:
			return Seriousness.BLOCKING;
		case SERVER_EXCEPTION:
			return Seriousness.CRITICAL;
		case AUTHORIZATION_EXCEPTION:
			return Seriousness.CRITICAL;
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
