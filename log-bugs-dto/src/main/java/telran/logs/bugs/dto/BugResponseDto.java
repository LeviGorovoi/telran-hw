package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class BugResponseDto extends BugAssignDto {
	public long bugId;
	public LocalDate dateClose;
	public BugStatus status;
	public OpenningMethod openingMethod;
	public BugResponseDto(long bugId, Seriousness seriousness, String description, LocalDate dateOpen, @Min(1) long programmerId,
			 LocalDate dateClose, BugStatus status, OpenningMethod openingMethod) {
		super(seriousness, description, dateOpen, programmerId);
		this.bugId = bugId;
		this.dateClose = dateClose;
		this.status = status;
		this.openingMethod = openingMethod;
	}
	
}
