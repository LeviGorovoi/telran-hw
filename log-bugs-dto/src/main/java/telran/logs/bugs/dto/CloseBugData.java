package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class CloseBugData {
	@Min(1)
	public long bugId;
	public LocalDate dateClose;
	public String description;
}
