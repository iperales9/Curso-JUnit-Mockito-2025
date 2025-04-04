package org.ivan.appmockito.ejemplos.services;

import org.ivan.appmockito.ejemplos.models.Exam;

import java.util.Optional;

public interface ExamService {

    Optional<Exam> findExamByName(String nombre);

    Exam findExamByNameWithQuestions(String nombre);

    Exam save(Exam exam);
}

