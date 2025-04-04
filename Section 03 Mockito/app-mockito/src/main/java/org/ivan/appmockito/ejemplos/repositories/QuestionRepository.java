package org.ivan.appmockito.ejemplos.repositories;

import java.util.List;

public interface QuestionRepository {
    List<String> findQuestionByExamById(Long id);
    void saveQuestions(List<String> questions);

}
