package telran.logs.bugs.interfaces;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import telran.logs.bugs.dto.*;

public interface LogsInfo {
	Flux<LogDto> getAllLogs();
	Flux<LogDto> getAllExceptions();
	Flux<LogDto> getLogsType(LogType logType);
	Flux<LogTypeCount> getLogTypeOccurrences();
	Flux<LogType> getMostEncounteredExceptionTypes(int nExceptions);
	Flux<ArtifactCount> getArtifactOccurrences();
	Mono<List<String>> getMostEncounteredArtifacts(int nArtifacts);

}
