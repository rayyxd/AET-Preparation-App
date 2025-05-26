package ru.rayyxd.aetpreparation.sqlRepositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.rayyxd.aetpreparation.sqlEntities.Module;

@Repository
public interface ModulesRepository extends JpaRepository<Module, Long>{

	List<Module> findAll();
	Optional<Module> findById(int id);
	
}
