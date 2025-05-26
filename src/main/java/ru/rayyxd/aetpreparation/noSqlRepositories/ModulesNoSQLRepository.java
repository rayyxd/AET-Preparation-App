package ru.rayyxd.aetpreparation.noSqlRepositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ru.rayyxd.aetpreparation.noSqlEntities.ModuleNoSQL;

@Repository
public interface ModulesNoSQLRepository extends MongoRepository<ModuleNoSQL, UUID> {

	Optional<ModuleNoSQL> findByModuleId(int id);
	
}
