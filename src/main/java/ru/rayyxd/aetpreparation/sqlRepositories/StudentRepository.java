package ru.rayyxd.aetpreparation.sqlRepositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.rayyxd.aetpreparation.sqlEntities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
	
	Optional<Student> findByEmail(String email);
	
	Optional<Student> findByEmailAndPassword(String email, String password);
	
	Boolean existsByEmail(String email);
	
	Optional<Student> findByEmailAndVerificationCode(String email, String verificationCode);
	
	Optional<Student> findById(Long id);
	
}
