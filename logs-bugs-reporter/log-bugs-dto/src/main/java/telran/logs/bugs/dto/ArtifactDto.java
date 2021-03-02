package telran.logs.bugs.dto;

import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ArtifactDto {
	@NotEmpty
	public String artifactId;
	@Min(1)
	public long programmerId;
}
