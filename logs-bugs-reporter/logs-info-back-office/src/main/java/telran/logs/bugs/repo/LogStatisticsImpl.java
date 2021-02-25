package telran.logs.bugs.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.LogTypeCount;
import telran.logs.bugs.mongo.doc.LogDoc;

public class LogStatisticsImpl implements LogStatistics {
	private static final String COUNT = "count";
	@Autowired
ReactiveMongoTemplate mongoTemplate;
	@Override
	public Flux<LogTypeCount> getLogTypeCounts() {
		GroupOperation groupOperation = Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT);
		ProjectionOperation projOperation = Aggregation.project(COUNT).and("_id")
				.as(LogTypeCount.LOG_TYPE);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		TypedAggregation<LogDoc> pipeline =
				Aggregation.newAggregation(LogDoc.class, groupOperation, sortOperation, projOperation);
		return mongoTemplate.aggregate(pipeline, LogTypeCount.class);
	}
	@Override
	public Flux<LogTypeCount> getMostEncounteredExceptionTypes(int nExceptions) {
		TypedAggregation<LogDoc> pipeline =
				Aggregation.newAggregation(LogDoc.class, 
						Aggregation.match(Criteria.where(LogDoc.LOG_TYPE).ne("NO_EXCEPTION")),
						Aggregation.group(LogDoc.LOG_TYPE).count().as(COUNT),
						Aggregation.sort(Direction.DESC, COUNT),
						Aggregation.limit(nExceptions),
						Aggregation.project().and("_id").as(LogDoc.LOG_TYPE));
		return mongoTemplate.aggregate(pipeline, LogTypeCount.class);
	}

}
