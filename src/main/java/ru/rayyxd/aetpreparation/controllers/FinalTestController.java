package ru.rayyxd.aetpreparation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayyxd.aetpreparation.sqlEntities.FinalTest;
import ru.rayyxd.aetpreparation.sqlEntities.Student;
import ru.rayyxd.aetpreparation.services.FinalTestService;
import ru.rayyxd.aetpreparation.sqlRepositories.StudentRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/final-tests")
public class FinalTestController {
    private final FinalTestService finalTestService;
    private final StudentRepository studentRepository;

    @Autowired
    public FinalTestController(FinalTestService finalTestService, StudentRepository studentRepository) {
        this.finalTestService = finalTestService;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public ResponseEntity<FinalTest> saveResult(@RequestBody FinalTest test) {
        // createdAt должен быть выставлен на сервере
        if (test.getCreatedAt() == null) {
            test.setCreatedAt(java.time.LocalDateTime.now());
        }
        return ResponseEntity.ok(finalTestService.save(test));
    }
} 