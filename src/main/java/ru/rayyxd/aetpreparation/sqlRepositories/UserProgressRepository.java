package ru.rayyxd.aetpreparation.sqlRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rayyxd.aetpreparation.sqlEntities.UserProgress;

import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByStudentIdAndModuleId(Long studentId, Long moduleId);
}
