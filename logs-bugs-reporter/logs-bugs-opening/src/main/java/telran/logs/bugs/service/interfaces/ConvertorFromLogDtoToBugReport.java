package telran.logs.bugs.service.interfaces;

import java.time.LocalDate;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.Programmer;

public interface ConvertorFromLogDtoToBugReport {
	LocalDate getDateOpen();
	LocalDate getDateClose();
	BugStatus getStatus();
	String getDescription();
	Programmer getProgrammer();
	Seriousness getSeriousness();
	OpenningMethod getOpenningMethod();


}
