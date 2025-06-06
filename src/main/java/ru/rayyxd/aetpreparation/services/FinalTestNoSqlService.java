package ru.rayyxd.aetpreparation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.rayyxd.aetpreparation.noSqlEntities.FinalTestNoSql;
import ru.rayyxd.aetpreparation.noSqlRepositories.FinalTestNoSqlRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FinalTestNoSqlService {
    private final FinalTestNoSqlRepository repository;

    @Autowired
    public FinalTestNoSqlService(FinalTestNoSqlRepository repository) {
        this.repository = repository;
    }

    public List<FinalTestNoSql> getAllTests() {
        return repository.findAll();
    }

    public Optional<FinalTestNoSql> getTestByTestId(int testId) {
        return repository.findByTestId(testId);
    }
} 