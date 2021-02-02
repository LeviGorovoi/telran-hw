package telran.logs.bugs.jpa.entities;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Entity
@Table(name="artifacts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Artifact {
	@Id
	@Column(name="artifact_id")
	String artifactId;
	@ManyToOne
	@JoinColumn(name = "programmer_id", nullable = false)
	Programmer programmer;
	
}
