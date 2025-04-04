package org.ivan.appmockito.ejemplos.repositories;

import org.ivan.appmockito.ejemplos.Data;
import org.ivan.appmockito.ejemplos.models.Exam;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestionRepositoryImpl implements QuestionRepository {
    @Override
    public List<String> findQuestionByExamById(Long id) {
        System.out.println("QuestionRepositoryImpl.findQuestionByExamById");
        try{
            TimeUnit.SECONDS.sleep(2);
        }catch (Exception e){

        }
        return Data.QUESTIONS;
    }

    @Override
    public void saveQuestions(List<String> questions) {
        System.out.println("QuestionRepositoryImpl.saveQuestions");
    }
}
