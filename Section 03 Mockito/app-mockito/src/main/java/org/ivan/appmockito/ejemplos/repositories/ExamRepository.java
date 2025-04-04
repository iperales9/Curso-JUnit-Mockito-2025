package org.ivan.appmockito.ejemplos.repositories;

import org.ivan.appmockito.ejemplos.models.Exam;

import java.util.List;

public interface ExamRepository {
    Exam save(Exam exam);
    List<Exam> findAll();
}
