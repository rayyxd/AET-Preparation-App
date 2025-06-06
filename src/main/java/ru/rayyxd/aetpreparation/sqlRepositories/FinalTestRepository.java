package ru.rayyxd.aetpreparation.sqlRepositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.rayyxd.aetpreparation.sqlEntities.FinalTest;
import ru.rayyxd.aetpreparation.sqlEntities.Student;

public interface FinalTestRepository extends JpaRepository<FinalTest, Long> {
	
	Optional<FinalTest> findById(Long id);
	List<FinalTest> findByTestId(int testId);
	List<FinalTest> findByUser(Student user);

}
