package telran.logs.bugs.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.dto.SeriousnessBugCount;
import telran.logs.bugs.jpa.entities.Bug;

public interface BugRepository extends JpaRepository<Bug, Long>{

	List<Bug> findByProgrammerId(long programmerId);

	List<Bug> findByStatus(BugStatus openned);

	List<Bug> findByStatusNotAndDateOpenBefore(BugStatus closed, LocalDate dateOpen);
	
@Query("select programmer.email as email, count(b) as count from Bug b right join b.programmer programmer group by programmer.email order by count(b) desc")
	List<EmailBugsCount> emailBugsCounts();

	@Modifying
	@Query("update Bug b set b.status = telran.logs.bugs.dto.BugStatus.CLOSED, b.dateClose = :close_date where b.id = :bug_id")
	void closeBug(@Param("close_date") LocalDate dateClose, @Param("bug_id") long bugId);


	@Query("select b.programmer.name as name from Bug b right join b.programmer programmer group by name order by count(b) desc")
	Stream<String> descendingRatingOfProgrammersByBugs();

	@Query("select b.programmer.name as name from Bug b right join b.programmer programmer group by name order by count(b) asc")
	Stream<String> ascendingRatingOfProgrammersByBugs();
	
	@Query("select b.seriousness as seriousness, count(b) as count from Bug b group by seriousness order by count(b) desc")
	Stream<SeriousnessBugCount> seriousnessBugsCounts();
}


