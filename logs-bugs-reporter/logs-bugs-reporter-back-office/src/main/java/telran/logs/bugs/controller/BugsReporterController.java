package telran.logs.bugs.controller;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.BugsReporter;
import static telran.logs.bugs.api.BugsReporterApi.*;

import java.util.List;

import javax.validation.Valid;

@RestController
public class BugsReporterController {
	static Logger LOG = LoggerFactory.getLogger(BugsReporterController.class);
@Autowired
BugsReporter bugsReporter;

@PostMapping(BUGS_OPEN)
BugResponseDto openBug(@Valid @RequestBody BugDto bugDto) {
	BugResponseDto res = bugsReporter.openBug(bugDto);
	LOG.debug("open bug - saved bug with id = {} , description: {}, status: {}", res.bugId, res.description, res.status);
	return res;
	
}
@PostMapping(BUGS_OPEN_ASSIGN)
BugResponseDto openAssignBug(@Valid @RequestBody BugAssignDto bugDto) {
	BugResponseDto res = bugsReporter.openAndAssignBug(bugDto);
	LOG.debug("open and assign bug - saved bug with id = {} , description: {}, status: {}", res.bugId, res.description, res.status);
	return res;
	
}
@PostMapping(BUGS_PROGRAMMERS)
ProgrammerDto addProgrammer(@Valid @RequestBody ProgrammerDto programmer) {
	ProgrammerDto res = bugsReporter.addProgrammer(programmer);
	LOG.debug("addProgrammer - saved programmer with id = {}", res.id);
	return res;
}

@PostMapping(BUGS_ARTIFACT)
ArtifactDto addArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
	ArtifactDto res = bugsReporter.addArtifact(artifactDto);
	LOG.debug("addArtifact - saved artifact with id = {}", res.artifactId);
	return res;
}
@PutMapping(BUGS_ASSIGN)
void assignBug(@Valid @RequestBody AssignBugData assignData) {
	bugsReporter.assignBug(assignData);
}
@GetMapping(BUGS_PROGRAMMERS)
List<BugResponseDto> getBugsOfProgrammer(@RequestParam(name=PROGRAMMER_ID) long programmerId) {
	List<BugResponseDto> result = bugsReporter.getBugsProgrammer(programmerId);
	LOG.debug("found {} bugs", result.size());
	return result ;
}

@GetMapping(BUGS_PROGRAMMERS_COUNT)
List<EmailBugsCount> getEmailBugsCount() {
	List<EmailBugsCount> result = bugsReporter.getEmailBugsCounts();
	result.forEach(ec -> LOG.debug("email: {}; count: {}", ec.getEmail(),ec.getCount()));
	return result;
}
@PutMapping(CLOSE_BUG)
void closeBug(@RequestBody CloseBugData closeData) {
	bugsReporter.closeBug(closeData);
	LOG.debug("bug with id {} was closed", closeData.bugId);
	
}

@GetMapping(PROGRAMMER_MOST_BUGS)
List<String> getProgrammersMostBugs(@RequestParam(name=N_PROGRAMMERS) int nProgrammers) {
	List<String> result = bugsReporter.getProgrammersMostBugs(nProgrammers);
	result.forEach(item -> LOG.debug("name: {}", item));
	return result;	
}

@GetMapping(PROGRAMMER_LEAST_BUGS)
List<String> getProgrammersLeastBugs(@RequestParam(name=N_PROGRAMMERS) int nProgrammers) {
	List<String> result = bugsReporter.getProgrammersLeastBugs(nProgrammers);
	result.forEach(item -> LOG.debug("name: {}", item));
	return result;	
}

@GetMapping(BUGS_SERIOUSNESS_COUNT)
List<SeriousnessBugCount> getSeriousnessBugCounts() {
	List<SeriousnessBugCount> result = bugsReporter.getSeriousnessBugCounts();
	result.forEach(item -> LOG.debug("seriousness: {}; count: {}", item.getSeriousness(), item.getCount()));
	return result;
}

@GetMapping(BUGS_SERIOUSNESS_TYPES_MOST_BUGS)
List<Seriousness> getSeriousnessTypesWithMostBugs(@RequestParam (name=N_TYPES)int nTypes){
	List<Seriousness> result = bugsReporter.getSeriousnessTypesWithMostBugs(nTypes);
	result.forEach(item -> LOG.debug("seriousness: {}", item));
	return result;	
}



}
