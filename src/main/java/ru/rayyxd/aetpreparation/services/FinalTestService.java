package ru.rayyxd.aetpreparation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rayyxd.aetpreparation.sqlEntities.FinalTest;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.sqlRepositories.FinalTestRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FinalTestService {
    private final FinalTestRepository repository;

    @Autowired
    public FinalTestService(FinalTestRepository repository) {
        this.repository = repository;
    }

    public FinalTest save(FinalTest test) {
        return repository.save(test);
    }

    public Optional<FinalTest> getById(Long id) {
        return repository.findById(id);
    }

    public List<FinalTest> getByTestId(int testId) {
        return repository.findByTestId(testId);
    }

    public List<FinalTest> getByUser(Student user) {
        return repository.findByUser(user);
    }
} 