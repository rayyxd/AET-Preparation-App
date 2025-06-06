package ru.rayyxd.aetpreparation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class UserGradesResponseDTO {
    private String testTitle;
    private int grade;
    private int maxGrade;
    
    // Будем отдавать строку в формате "dd.MM.yyyy HH:mm"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;

    public UserGradesResponseDTO() {}

    public UserGradesResponseDTO(String testTitle, int grade, int maxGrade, LocalDateTime createdAt) {
        this.testTitle = testTitle;
        this.grade = grade;
        this.maxGrade = maxGrade;
        this.createdAt = createdAt;
    }

    // геттеры/сеттеры

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(int maxGrade) {
        this.maxGrade = maxGrade;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
