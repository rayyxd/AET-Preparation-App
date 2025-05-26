package ru.rayyxd.aetpreparation.sqlEntities;

import jakarta.persistence.*;

@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moduleId")
    private Module module;

    @Column(nullable = false)
    private Double progress;

    public UserProgress() {}

    public UserProgress(Student student, Module module, Double progress) {
        this.student = student;
        this.module = module;
        this.progress = progress;
    }

    // геттеры/сеттеры
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }
}
