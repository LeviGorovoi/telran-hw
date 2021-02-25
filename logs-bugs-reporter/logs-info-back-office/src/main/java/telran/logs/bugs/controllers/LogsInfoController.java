package telran.logs.bugs.controllers;

import java.util.List;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.dto.*;
import telran.logs.bugs.interfaces.LogsInfo;

@RestController
public class LogsInfoController {
	static Logger LOG = LoggerFactory.getLogger(LogsInfoController.class);
	@Autowired
	LogsInfo logsInfo;
	@GetMapping(value="/logs",produces="application/stream+json")
Flux<LogDto> getAllLogs() {
		Flux<LogDto> result = logsInfo.getAllLogs();
		LOG.debug("Logs sent to a client");
	return result;
}
	@GetMapping(value="/logs/exceptions")
	Flux<LogDto> getLogsWithException() {
			Flux<LogDto> result = logsInfo.getAllExceptions();
			LOG.debug("Logs whith exceptions sent to a client");
		return result;
	}
	@GetMapping(value="/logs/type",produces="application/stream+json")
	Flux<LogDto> getLogsByTypes(@RequestParam(name="type") LogType logType) {
			Flux<LogDto> result = logsInfo.getLogsType(logType);
			LOG.debug("Logs of type {} sent to a client", logType);
		return result;
	}
	
	@GetMapping(value="/logs/distribution/type")
	Flux<LogTypeCount> getLogTypeOccurrences() {
		return logsInfo.getLogTypeOccurrences();
	}
	
	@GetMapping(value="/logs/most/encountered/exception")
	Flux<LogType> getMostEncounteredExceptionTypes(@RequestParam(name="n-exceptions") int nExceptions) {
		return logsInfo.getMostEncounteredExceptionTypes(nExceptions);
	}
	
	@GetMapping(value="/logs/distribution/artifact")
	Flux <ArtifactCount> getArtifactOccurrences (){
		return logsInfo.getArtifactOccurrences();
	}
	@GetMapping(value="/logs/most/encountered/artifacts")
	Mono <List <String>> getMostEncounteredArtifacts (@RequestParam(name="n-artifacts") int nArtifacts){
		return logsInfo.getMostEncounteredArtifacts(nArtifacts);
	}
}
