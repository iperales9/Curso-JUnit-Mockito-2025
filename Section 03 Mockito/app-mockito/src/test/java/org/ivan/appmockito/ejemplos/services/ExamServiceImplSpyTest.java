package org.ivan.appmockito.ejemplos.services;

import org.ivan.appmockito.ejemplos.Data;
import org.ivan.appmockito.ejemplos.models.Exam;
import org.ivan.appmockito.ejemplos.repositories.ExamRepository;
import org.ivan.appmockito.ejemplos.repositories.ExamRepositoryImpl;
import org.ivan.appmockito.ejemplos.repositories.QuestionRepository;
import org.ivan.appmockito.ejemplos.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para ExamServiceImpl.
 * Esta clase utiliza Mockito para simular el comportamiento de los repositorios y probar la lógica del servicio.
 */
@ExtendWith(MockitoExtension.class)
class ExamServiceImplSpyTest {

    @Spy
    ExamRepositoryImpl examRepository;

    @Spy
    QuestionRepositoryImpl questionRepository;

    @InjectMocks
    ExamServiceImpl examService;

    /*
    Es un metodo de prueba llamado testSpy que utiliza Mockito para crear "spies"
    de los repositorios ExamRepositoryImpl y QuestionRepositoryImpl. Un "spy" en
    Mockito es un objeto que permite espiar (es decir, observar) las llamadas a
    os métodos reales del objeto, pero también permite simular (mock) algunos de
    sus métodos.
     */

    @Test
    void testSpy() {

        List<String> questions = List.of();

        doReturn(questions).when(questionRepository).findQuestionByExamById(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Matematicas");
        assertEquals(1L, exam.getId());
        assertEquals("Matematicas", exam.getNombre());
        assertEquals(0, exam.getPreguntas().size());

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionByExamById(anyLong());

    }

}