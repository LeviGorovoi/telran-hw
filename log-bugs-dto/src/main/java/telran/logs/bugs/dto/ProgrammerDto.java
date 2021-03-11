package telran.logs.bugs.dto;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;


@AllArgsConstructor
@EqualsAndHashCode
public class ProgrammerDto {
	@Min(1)
public long id;
	@NotEmpty
public String name;
	@Email
public String email;
}
