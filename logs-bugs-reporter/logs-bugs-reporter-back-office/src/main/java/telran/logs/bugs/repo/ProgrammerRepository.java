package telran.logs.bugs.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.jpa.entities.Programmer;

public interface ProgrammerRepository extends JpaRepository<Programmer, Long> {
	@Query(value = "Select p from Programmer p where p.id = :id")
	Programmer findByProgrammerId(long id);



}
