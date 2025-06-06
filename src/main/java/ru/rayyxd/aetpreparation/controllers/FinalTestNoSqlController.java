package ru.rayyxd.aetpreparation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rayyxd.aetpreparation.noSqlEntities.FinalTestNoSql;
import ru.rayyxd.aetpreparation.services.FinalTestNoSqlService;

import java.util.List;

@RestController
@RequestMapping("/tests")
public class FinalTestNoSqlController {
    private final FinalTestNoSqlService testService;

    @Autowired
    public FinalTestNoSqlController(FinalTestNoSqlService testService) {
        this.testService = testService;
    }

    @GetMapping
    public List<FinalTestNoSql> getAllTests() {
        return testService.getAllTests();
    }

    @GetMapping("/{testId}")
    public ResponseEntity<FinalTestNoSql> getTestByTestId(@PathVariable int testId) {
        return testService.getTestByTestId(testId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 