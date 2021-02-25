package telran.logs.bugs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.repo.ArtifactsRepo;
import telran.logs.bugs.service.interfaces.BugReportFactory;
import telran.logs.bugs.service.interfaces.ConvertorFromLogDtoToBugReport;
@Service
public class BugReportFactoryImpl implements BugReportFactory {
	@Autowired
	ArtifactsRepo artifacts;
	@Override
	public Bug createBugReport(LogDto logDto) {
		ConvertorFromLogDtoToBugReport convertor = new ConvertorFromLogDtoToBugReportImpl(logDto, artifacts);
		return Bug.builder().dateOpen(convertor.getDateOpen()).dateClose(convertor.getDateClose()).seriousness(convertor.getSeriousness())
				.programmer(convertor.getProgrammer()).status(convertor.getStatus()).openningMethod(convertor.getOpenningMethod()).
				description(convertor.getDescription()).build();
	}

}
