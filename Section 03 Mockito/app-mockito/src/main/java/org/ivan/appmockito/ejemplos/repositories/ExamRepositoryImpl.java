package org.ivan.appmockito.ejemplos.repositories;

import org.ivan.appmockito.ejemplos.Data;
import org.ivan.appmockito.ejemplos.models.Exam;

import java.util.List;

public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public Exam save(Exam exam) {
        System.out.println("ExamRepositoryImpl.save");
        return Data.EXAM;
    }

    @Override
    public List<Exam> findAll() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ExamRepositoryImpl.findAll");
        return Data.DATA_EXAM;
    }
}
