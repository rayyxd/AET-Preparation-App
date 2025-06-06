package ru.rayyxd.aetpreparation.noSqlRepositories;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import ru.rayyxd.aetpreparation.noSqlEntities.FinalTestNoSql;

public interface FinalTestNoSqlRepository extends MongoRepository<FinalTestNoSql, ObjectId> {
	Optional<FinalTestNoSql> findByTestId(int testId);
}
