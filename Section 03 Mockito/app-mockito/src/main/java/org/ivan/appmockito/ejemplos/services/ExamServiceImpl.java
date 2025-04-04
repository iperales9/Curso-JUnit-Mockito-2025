package org.ivan.appmockito.ejemplos.services;

import org.ivan.appmockito.ejemplos.models.Exam;
import org.ivan.appmockito.ejemplos.repositories.ExamRepository;
import org.ivan.appmockito.ejemplos.repositories.QuestionRepository;

import java.util.Optional;

public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String nombre) {
        return examRepository.findAll().stream().filter(exam ->
                exam.getNombre().equals(nombre)).findFirst();
    }

    @Override
    public Exam findExamByNameWithQuestions(String nombre) {

        Optional<Exam> examOptional = findExamByName(nombre);
        if (examOptional.isPresent()) {
            Exam exam = examOptional.get();
            exam.setPreguntas(questionRepository.findQuestionByExamById(exam.getId()));
            return exam;
        }
        return null;
    }

    @Override
    public Exam save(Exam exam) {
        if (!exam.getPreguntas().isEmpty()) {
            questionRepository.saveQuestions(exam.getPreguntas());
        }
        return examRepository.save(exam);
    }
}
