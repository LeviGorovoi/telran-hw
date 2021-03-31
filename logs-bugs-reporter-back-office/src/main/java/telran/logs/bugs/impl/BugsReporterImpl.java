package telran.logs.bugs.impl;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.exceptions.DuplicatedException;
import telran.logs.bugs.exceptions.NotFoundException;
import telran.logs.bugs.interfaces.BugsReporter;
import telran.logs.bugs.jpa.entities.Artifact;
import telran.logs.bugs.jpa.entities.Bug;
import telran.logs.bugs.jpa.entities.Programmer;
import telran.logs.bugs.repo.*;

@Service
@ManagedResource(description = "testImpl")
public class BugsReporterImpl implements BugsReporter {
	BugRepository bugRepository;
	ArtifactRepository artifactRepository;
	ProgrammerRepository programmerRepository;
	@Autowired
	public BugsReporterImpl(BugRepository bugRepository, ArtifactRepository artifactRepository,
			ProgrammerRepository programmerRepository) {
		super();
		this.bugRepository = bugRepository;
		this.artifactRepository = artifactRepository;
		this.programmerRepository = programmerRepository;
	}
	@Override
	@Transactional
	@ManagedOperation
	public ProgrammerDto addProgrammer(ProgrammerDto programmerDto) {
		programmerRepository.findById(programmerDto.id).ifPresent(item->{
			throw new DuplicatedException(String.format("There is already a programmer with id %d", programmerDto.id));
		});
		programmerRepository.save(new Programmer(programmerDto.id, programmerDto.name, programmerDto.email));
		return programmerDto;
	}
	@ManagedOperation
	@Override
	public ArtifactDto addArtifact(ArtifactDto artifactDto) {		
		Programmer programmer = programmerRepository.findById(artifactDto.programmerId).orElse(null);
		if(programmer==null) {
			throw new NotFoundException(String.format("No programmer with id %d", artifactDto.programmerId));
		}
		artifactRepository.findById(artifactDto.artifactId).ifPresent(dto->{			
			throw new DuplicatedException(String.format("There is already a artifact with id %s", artifactDto.artifactId));
		});
		artifactRepository.save(new Artifact(artifactDto.artifactId, programmer));
		return artifactDto;
	}
	@ManagedOperation
	@Override
	@Transactional
	public BugResponseDto openBug(BugDto bugDto) {
		//FIXME exceptions
				LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
				Bug bug = new Bug
						(bugDto.description, dateOpen, null, BugStatus.OPENNED,
								bugDto.seriousness,OpenningMethod.MANUAL, null);
				bugRepository.save(bug);
				return toBugResponseDto(bug);
			}
	@ManagedOperation
			private BugResponseDto toBugResponseDto(Bug bug) {			
				Programmer programmer = bug.getProgrammer();
				long programmerId = programmer == null ? 0 : programmer.getId();
				return new BugResponseDto
						(bug.getId(), bug.getSeriousness(), bug.getDescription(),
								bug.getDateOpen(), programmerId, bug.getDateClose(),
								bug.getStatus(), bug.getOpenningMethod());
	}
	@ManagedOperation
	@Override
	public BugResponseDto openAndAssignBug(BugAssignDto bugDto) {
				Programmer programmer = programmerRepository.findById(bugDto.programmerId).orElse(null);
				if(programmer==null) {
					throw new NotFoundException(String.format("assigning can't be done - no programmer"
							+ " with id %d", bugDto.programmerId));
				}
				LocalDate dateOpen = bugDto.dateOpen != null ? bugDto.dateOpen : LocalDate.now();
				Bug bug = 
						new Bug(bugDto.description, dateOpen, null, BugStatus.ASSIGNED,
								bugDto.seriousness, OpenningMethod.MANUAL, programmer);
				bug = bugRepository.save(bug);
				return toBugResponseDto(bug);
	}
	@ManagedOperation
	@Override
	@Transactional
	public void assignBug(AssignBugData assignData) {
		Bug bug = bugRepository.findById(assignData.bugId).orElseThrow(()->{
			throw new NotFoundException(String.format("No bug with id %d", assignData.bugId));	
		});
		if(bug.getProgrammer()!=null) {
			throw new DuplicatedException(String.format("This bug has already been assigned to the programmer with id %d", 
					bug.getProgrammer().getId()));
		}
		bug.setDescription(bug.getDescription() + ASSIGNMENT_DESCRIPTION_TITLE +
		assignData.description);
		Programmer programmer = programmerRepository.findById(assignData.programmerId)
				.orElseThrow(()->{
					throw new NotFoundException(String.format("assigning can't be done - no programmer"
							+ " with id %d", assignData.programmerId));	
				});;
		bug.setStatus(BugStatus.ASSIGNED);
		bug.setProgrammer(programmer);
		
	}
	@ManagedOperation
	@Override
	public List<BugResponseDto> getNonAssignedBugs() {
		List<Bug> bugs = bugRepository.findByStatus(BugStatus.OPENNED);
		return toListBugResponseDto(bugs);
	}
	@Override
	@ManagedOperation
	@Transactional
	public void closeBug(CloseBugData closeData) {
		Bug bug = bugRepository.findById(closeData.getBugId()).orElseThrow(()->{
			throw new NotFoundException(String.format("No bug with id %d", closeData.bugId));	
		});
		if(bug.getStatus()==BugStatus.CLOSED) {
			throw new DuplicatedException(String.format("bug with id {} has already been cloused %d", bug.getId()));
		}
		bug.setStatus(BugStatus.CLOSED);
		bug.setDateClose(closeData.getDateClose());
		
	}
	@ManagedOperation
	@Override
	public List<BugResponseDto> getUnClosedBugsMoreDuration(int days) {
		LocalDate dateOpen = LocalDate.now().minusDays(days);
		List<Bug> bugs = bugRepository.findByStatusNotAndDateOpenBefore(BugStatus.CLOSED, dateOpen);
		return toListBugResponseDto(bugs);
	}
	@ManagedOperation
	@Override
	public List<BugResponseDto> getBugsProgrammer(long programmerId) {
		List<Bug> bugs = bugRepository.findByProgrammerId(programmerId);
		return bugs.isEmpty() ? new LinkedList<>() : toListBugResponseDto(bugs);
	}
	@ManagedOperation
	private List<BugResponseDto> toListBugResponseDto(List<Bug> bugs) {
		return bugs.stream().map(this::toBugResponseDto).collect(Collectors.toList());
	}
	@ManagedOperation
	@Override
	public List<EmailBugsCount> getEmailBugsCounts() {
		List<EmailBugsCount> result = bugRepository.emailBugsCounts();
		return result;
	}
//	@ManagedMetric
	@ManagedOperation
	@Override
	@Transactional
	public List<String> getProgrammersMostBugs(int nProgrammers) {
		Stream<String> programmers = bugRepository.descendingRatingOfProgrammersByBugs(); 
		return programmers.limit(nProgrammers).collect(Collectors.toList());
	}
	@ManagedOperation
	@Override
	@Transactional
	public List<String> getProgrammersLeastBugs(int nProgrammers) {
		Stream<String> programmers = bugRepository.ascendingRatingOfProgrammersByBugs(); 
		return programmers.limit(nProgrammers).collect(Collectors.toList());
	}
	@ManagedOperation
	@Override
	@Transactional
	public List<SeriousnessBugCount> getSeriousnessBugCounts() {
		return bugRepository.seriousnessBugsCounts().collect(Collectors.toList());
	}
	@ManagedOperation
	@Override
	@Transactional
	public List<Seriousness> getSeriousnessTypesWithMostBugs(int nTypes) {
		return bugRepository.seriousnessBugsCounts().map(item->item.getSeriousness()).limit(nTypes).collect(Collectors.toList());
	}
	@ManagedOperation
	@PostConstruct
	private void fillArtifactsAndProgrammersDb() {
		String[] programmerNames = {"Aharon", "Abba", "Avraham", "Adam", "Akiva", "Alexander", "Alon", "Alter",
				"Amos", "Amram", "Ariel", "Asher", "Avi", "Avigdor", "Avner", "Azriel", "Barak"};
		String[] artifacts = {"authentication", "authorization", "class1", "class2", "class3", "class4", "class5", "class6",
				"class7", "class8", "class9", "class10", "class11", "class12", "class13", "class14", "class15", "class16", "class17",
				"class18", "class19", "class20"};
		for(int i = 0; i<programmerNames.length; i++ ) {
			if(programmerRepository.findByProgrammerId(i)==null) {
			programmerRepository.save(new Programmer(i, programmerNames[i],"llevi9973+"+programmerNames[i]+i+"@gmail.com"));
			}
		}
		for(String artifactId:artifacts) {
			artifactRepository.save(new Artifact(artifactId, programmerRepository.findByProgrammerId((long) new Random().nextInt(programmerNames.length))));
		}
	}
}
