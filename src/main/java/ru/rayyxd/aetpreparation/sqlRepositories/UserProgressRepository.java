package ru.rayyxd.aetpreparation.sqlRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import ru.rayyxd.aetpreparation.sqlEntities.UserProgress;

import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByStudentIdAndModuleId(Long studentId, Long moduleId);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserProgress up SET up.progress = :progress WHERE up.student.id = :studentId AND up.module.id = :moduleId")
    void updateProgress(@Param("studentId") Long studentId,
                        @Param("moduleId") Long moduleId,
                        @Param("progress") Double progress);
}
