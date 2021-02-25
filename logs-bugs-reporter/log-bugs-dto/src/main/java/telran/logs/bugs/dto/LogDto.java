package telran.logs.bugs.dto;

import java.util.Date;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class LogDto {
	
	@NotNull
	public Date dateTime;
	@NotNull
	public LogType logType;
	@NotEmpty
	public String artifact;
	public int responseTime;
	public String result;

	
	

}
