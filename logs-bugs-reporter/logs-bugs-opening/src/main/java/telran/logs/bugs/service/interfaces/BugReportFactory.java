package telran.logs.bugs.service.interfaces;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.repo.ArtifactsRepo;

public interface BugReportFactory {
	Bug createBugReport (LogDto logDto);
}
