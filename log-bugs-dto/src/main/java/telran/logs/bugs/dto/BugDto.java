package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class BugDto {
	@NotNull @Setter
	public Seriousness seriousness;
	@NotEmpty @Setter
	public String description;
	public LocalDate dateOpen;

}
