package telran.logs.bugs.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import static telran.logs.bugs.api.BugsReporterApi.*;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
@Slf4j
@ManagedResource(description = "test")
public class BugsReporterController {
@Autowired
BugsReporter bugsReporter;
@ManagedOperation
@PostMapping(BUGS_OPEN)
BugResponseDto openBug(@Valid @RequestBody BugDto bugDto) {
	BugResponseDto res = bugsReporter.openBug(bugDto);
	log.debug("open bug - saved bug with id = {} , description: {}, status: {}", res.bugId, res.description, res.status);
	return res;
	
}
@ManagedOperation
@ManagedMetric
@PostMapping(BUGS_OPEN_ASSIGN)
BugResponseDto openAssignBug(@Valid @RequestBody BugAssignDto bugDto) {
	BugResponseDto res = bugsReporter.openAndAssignBug(bugDto);
	log.debug("open and assign bug - saved bug with id = {} , description: {}, status: {}", res.bugId, res.description, res.status);
	return res;
	
}
@ManagedOperation
@PostMapping(BUGS_PROGRAMMERS)
ProgrammerDto addProgrammer(@Valid @RequestBody ProgrammerDto programmer) {
	ProgrammerDto res = bugsReporter.addProgrammer(programmer);
	log.debug("addProgrammer - saved programmer with id = {}", res.id);
	return res;
}
@ManagedOperation
@PostMapping(BUGS_ARTIFACT)
ArtifactDto addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
	ArtifactDto res = bugsReporter.addArtifact(artifactDto);
	log.debug("addArtifact - saved artifact with id = {}", res.artifactId);
	return res;
}
@ManagedOperation
@PutMapping(BUGS_ASSIGN)
void assignBug(@Valid @RequestBody AssignBugData assignData) {
	bugsReporter.assignBug(assignData);
}
@ManagedOperation
@GetMapping(BUGS_PROGRAMMERS)
List<BugResponseDto> getBugsOfProgrammer(@RequestParam(name=PROGRAMMER_ID) long programmerId) {
	List<BugResponseDto> result = bugsReporter.getBugsProgrammer(programmerId);
	log.debug("found {} bugs", result.size());
	return result ;
}
@ManagedOperation
@GetMapping(BUGS_PROGRAMMERS_COUNT)
List<EmailBugsCount> getEmailBugsCount() {
	List<EmailBugsCount> result = bugsReporter.getEmailBugsCounts();
	result.forEach(ec -> log.debug("email: {}; count: {}", ec.getEmail(),ec.getCount()));
	return result;
}
@ManagedOperation
@PutMapping(CLOSE_BUG)
void closeBug(@RequestBody CloseBugData closeData) {
	bugsReporter.closeBug(closeData);
	log.debug("bug with id {} was closed", closeData.bugId);
	
}
@ManagedOperation
@GetMapping(PROGRAMMER_MOST_BUGS)
List<String> getProgrammersMostBugs(@RequestParam(name=N_PROGRAMMERS) int nProgrammers) {
	List<String> result = bugsReporter.getProgrammersMostBugs(nProgrammers);
	result.forEach(item -> log.debug("name: {}", item));
	return result;	
}
@ManagedOperation
@GetMapping(PROGRAMMER_LEAST_BUGS)
List<String> getProgrammersLeastBugs(@RequestParam(name=N_PROGRAMMERS) int nProgrammers) {
	List<String> result = bugsReporter.getProgrammersLeastBugs(nProgrammers);
	result.forEach(item -> log.debug("name: {}", item));
	return result;	
}
@ManagedOperation
@GetMapping(BUGS_SERIOUSNESS_COUNT)
List<SeriousnessBugCount> getSeriousnessBugCounts() {
	List<SeriousnessBugCount> result = bugsReporter.getSeriousnessBugCounts();
	result.forEach(item -> log.debug("seriousness: {}; count: {}", item.getSeriousness(), item.getCount()));
	return result;
}
@ManagedOperation
@GetMapping(BUGS_SERIOUSNESS_TYPES_MOST_BUGS)
List<Seriousness> getSeriousnessTypesWithMostBugs(@RequestParam (name=N_TYPES) @Min(1) int nTypes){
	List<Seriousness> result = bugsReporter.getSeriousnessTypesWithMostBugs(nTypes);
	result.forEach(item -> log.debug("seriousness: {}", item));
	return result;	
}



}
