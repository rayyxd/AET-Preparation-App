package ru.rayyxd.aetpreparation.sqlRepositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.rayyxd.aetpreparation.sqlEntities.FinalTest;

public interface FinalTestRepository extends JpaRepository<FinalTest, Long> {
	
	Optional<FinalTest> findById(int id);

}
