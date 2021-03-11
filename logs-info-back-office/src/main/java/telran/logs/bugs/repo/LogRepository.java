package telran.logs.bugs.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.mongo.doc.*;

public interface LogRepository extends ReactiveMongoRepository<LogDoc, ObjectId>, LogStatistics {

	Flux<LogDto> findByLogType(LogType logType);

	Flux<LogDto> findByLogTypeNot(LogType noException);

	@Aggregation({"{$group:{_id:'$artifact',count:{$sum:1}}}", "{$project:{_id:0, artifact:$_id, count:1}}"})
	Flux<ArtifactCount> getArtifactOccurrences();
	
	@Aggregation({"{$group:{_id:'$artifact',count:{$sum:1}}}", "{$project:{_id:0, artifact:$_id, count:1}}",
		"{ '$sort' : { count : -1 } }", "{$limit:?0}"})
	Flux<ArtifactCount> getMostEncounteredArtifacts(int nArtifacts);

}
