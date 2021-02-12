package telran.logs.bugs.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogsRepo extends ReactiveMongoRepository<LogDoc, ObjectId> {


}


