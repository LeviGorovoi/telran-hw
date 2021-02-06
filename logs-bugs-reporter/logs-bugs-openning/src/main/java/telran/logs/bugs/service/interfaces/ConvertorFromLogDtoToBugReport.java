package telran.logs.bugs.service.interfaces;

import java.time.LocalDate;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.jpa.entities.BugStatus;
import telran.logs.bugs.jpa.entities.OpenningMethod;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.jpa.entities.Seriousness;

public interface ConvertorFromLogDtoToBugReport {
	LocalDate getDateOpen();
	LocalDate getDateClose();
	BugStatus getStatus();
	String getDescription();
	Programmer getProgrammer();
	Seriousness getSeriousness();
	OpenningMethod getOpenningMethod();

	
	
}
