package telran.logs.bugs.jpa.entities;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="bugs")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
@Builder
public class Bug {
	@Id
	@GeneratedValue
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	long id;
	@Column(nullable = false)
	String description;
	@Column(name="date_open", nullable = false)
	LocalDate dateOpen;
	@Column(name="date_close", nullable = true)
	LocalDate dateClose;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	BugStatus status;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	Seriousness seriousness;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name="openning_method")
	OpenningMethod openningMethod;
	@ManyToOne
	@JoinColumn(name="programmer_id", nullable = true)
	Programmer programmer;
	
	public Bug(String description, LocalDate dateOpen, LocalDate dateClose, BugStatus status, Seriousness seriousness,
			OpenningMethod openningMethod, Programmer programmer) {
	this(0, description, dateOpen, dateClose, status, seriousness, openningMethod, programmer);
	}
	

}
