package ru.rayyxd.aetpreparation.noSqlRepositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import ru.rayyxd.aetpreparation.noSqlEntities.FinalTestNoSql;

public interface FinalTestNoSqlRepository extends MongoRepository<FinalTestNoSql, UUID>{

	Optional<FinalTestNoSql> findByTestId(int id);
	
}
