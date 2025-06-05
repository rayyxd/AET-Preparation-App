package ru.rayyxd.aetpreparation.controllers;
import org.springframework.web.bind.annotation.*;
import ru.rayyxd.aetpreparation.noSqlEntities.FlashcardTopic;
import ru.rayyxd.aetpreparation.services.FlashcardService;

import java.util.List;

@RestController
@RequestMapping("/flashcards")
public class FlashcardController {
    private final FlashcardService service;

    public FlashcardController(FlashcardService service) {
        this.service = service;
    }

    @GetMapping("/topics")
    public List<FlashcardTopic> getAllTopics() {
        return service.getAllTopics();
    }

    @GetMapping("topics/{id}")
    public FlashcardTopic getTopicById(@PathVariable String id) {
        return service.getTopicById(id);
    }
}
