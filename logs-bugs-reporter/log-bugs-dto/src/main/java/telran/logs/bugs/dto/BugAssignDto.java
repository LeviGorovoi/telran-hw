package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class BugAssignDto extends BugDto {
	@Min(1)
	public long programmerId;

	public BugAssignDto(Seriousness seriousness, String description, LocalDate dateOpen, @Min(1) long programmerId) {
		super(seriousness, description, dateOpen);
		this.programmerId = programmerId;
	}

}
