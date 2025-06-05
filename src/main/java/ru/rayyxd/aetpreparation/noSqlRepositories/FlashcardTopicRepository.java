package ru.rayyxd.aetpreparation.noSqlRepositories;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.rayyxd.aetpreparation.noSqlEntities.FlashcardTopic;

public interface FlashcardTopicRepository extends MongoRepository<FlashcardTopic, String> {
}