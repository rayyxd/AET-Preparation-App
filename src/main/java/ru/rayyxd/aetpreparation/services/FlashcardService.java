package ru.rayyxd.aetpreparation.services;
import org.springframework.stereotype.Service;
import ru.rayyxd.aetpreparation.noSqlEntities.FlashcardTopic;
import ru.rayyxd.aetpreparation.noSqlRepositories.FlashcardTopicRepository;

import java.util.List;

@Service
public class FlashcardService {
    private final FlashcardTopicRepository repository;

    public FlashcardService(FlashcardTopicRepository repository) {
        this.repository = repository;
    }

    public List<FlashcardTopic> getAllTopics() {
        return repository.findAll();
    }

    public FlashcardTopic getTopicById(String id) {
        return repository.findById(id).orElse(null);
    }
}
