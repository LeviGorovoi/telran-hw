package telran.logs.bugs.dto;


import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class AssignBugData {
	@Min(1)
	public long bugId;
	@Min(1)
	public long programmerId;
	@Setter
	public String description;
	

	
}
