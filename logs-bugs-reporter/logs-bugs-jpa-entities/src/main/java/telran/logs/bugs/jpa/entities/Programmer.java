package telran.logs.bugs.jpa.entities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="programmers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Programmer {
	
	@Id
	long id;
		@Column(name="name", nullable = false)
		String name;



}
