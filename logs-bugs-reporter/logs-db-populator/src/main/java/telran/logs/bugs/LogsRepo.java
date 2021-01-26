package telran.logs.bugs;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import telran.logs.bugs.mongo.doc.LogDoc;

public interface LogsRepo extends MongoRepository<LogDoc, ObjectId> {

}


