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
class ExamServiceImplTest {

    @Mock
    ExamRepositoryImpl examRepository;

    @Mock
    QuestionRepositoryImpl questionRepository;

    @InjectMocks
    ExamServiceImpl examService;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
        // Alternativamente, puedes usar la siguiente línea si no estás usando @InjectMocks
        //  examRepository = mock(ExamRepositoryImpl.class);
        //  questionRepository = mock(QuestionRepository.class);
        //  examService = new ExamServiceImpl(examRepository, questionRepository);
    }

    @Test
    void findExamByName() {

        // Configuramos el comportamiento del mock para devolver la lista simulada
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        // Llamamos al metodo findExamByName con un nombre de examen
        String nombre = "Matematicas";
        Optional<Exam> examOpt = examService.findExamByName(nombre);
        // Verificamos que el resultado no sea un Optional vacío
        assertTrue(examOpt.isPresent());
        // Verificamos que el examen devuelto tenga el nombre esperado
        Exam exam = examOpt.orElseThrow();
        assertEquals(nombre, exam.getNombre());
        assertEquals(1L, exam.getId());
    }


    @Test
    void findExamByNameEmptyList() {
        // Simulamos una lista vacía
        List<Exam> datos = List.of();
        // Configuramos el comportamiento del mock para devolver la lista vacía
        when(examRepository.findAll()).thenReturn(datos);
        Optional<Exam> exam = examService.findExamByName("Matematicas");
        // Verificamos que el resultado sea un Optional vacío
        assertFalse(exam.isPresent());
    }

    @Test
    void testQuestionsExams() {

        // Simulamos el comportamiento del repositorio para devolver una lista de preguntas
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        when(questionRepository.findQuestionByExamById(1L)).thenReturn(Data.QUESTIONS);
        // Llamamos al metodo findExamByNameWithQuestions
        Exam exam = examService.findExamByNameWithQuestions("Matematicas");
        // Verificamos que el examen devuelto no sea nulo y contenga las preguntas esperadas
        assertNotNull(exam);
        assertEquals(6, exam.getPreguntas().size());
        assertTrue(exam.getPreguntas().contains("arithmetic"));

    }

    @Test
    void testQuestionsExamsVerify() {

        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        when(questionRepository.findQuestionByExamById(1L)).thenReturn(Data.QUESTIONS);

        Exam exam = examService.findExamByNameWithQuestions("Matematicas");

        assertNotNull(exam);
        assertEquals(6, exam.getPreguntas().size());
        assertTrue(exam.getPreguntas().contains("arithmetic"));

        // Verificamos que se llamaron los métodos findAll y findQuestionByExamById
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionByExamById(1L);
    }

    @Test
    void testNoExistExamsVerify() {

        when(examRepository.findAll()).thenReturn(Collections.emptyList());
        when(questionRepository.findQuestionByExamById(1L)).thenReturn(Data.QUESTIONS);

        Exam exam = examService.findExamByNameWithQuestions("Matematicas");

        assertNull(exam);

        // Verificamos que se llamaron los métodos findAll y findQuestionByExamById
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionByExamById(1L);
    }

    @Test
    void testSaveExam() {

        Exam exam = Data.EXAM;
        exam.setPreguntas(Data.QUESTIONS);

        when(examRepository.save(any(Exam.class))).then(new Answer<Exam>() {

            Long secuencia = 7L;

            @Override
            public Exam answer(InvocationOnMock invocationOnMock) throws Throwable {

                // Obtenemos el examen pasado como argumento
                Exam exam = invocationOnMock.getArgument(0);

                // Simulamos la asignación de un ID al examen guardado
                exam.setId(secuencia++);

                return exam;
            }

        });

        Exam exam2 = examService.save(exam);
        assertNotNull(exam2.getId());
        assertEquals(7L, exam2.getId());
        assertEquals("Fisica", exam2.getNombre());

        verify(examRepository).save(any(Exam.class));
        verify(questionRepository).saveQuestions(anyList());

    }

    @Test
    void testManageException() {
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM_ID_NULL);
        when(questionRepository.findQuestionByExamById(1L)).thenThrow(new IllegalArgumentException());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            examService.findExamByNameWithQuestions("Matematicas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionByExamById(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        when(questionRepository.findQuestionByExamById(anyLong())).thenReturn(Data.QUESTIONS);
        examService.findExamByNameWithQuestions("Matematicas");
        // Verificamos que se llamaron los métodos findAll y findQuestionByExamById
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionByExamById(argThat(id -> id != null && id > 0));
    }

    @Test
    void testArgumentMatchers2() {
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        when(questionRepository.findQuestionByExamById(anyLong())).thenReturn(Data.QUESTIONS);
        examService.findExamByNameWithQuestions("Matematicas");
        // Verificamos que se llamaron los métodos findAll y findQuestionByExamById
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionByExamById(argThat(new MiArgsMatcher()));

    }

    public static class MiArgsMatcher implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error " +
                    "que imprime mockito en caso de que falle " +
                    "el test" + argument + " debe ser un entero positivo";
        }

    }

    @Test
    void testArgumentCaptor() {
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        // when(questionRepository.findQuestionByExamById(anyLong())).thenReturn(Data.QUESTIONS);
        examService.findExamByNameWithQuestions("Matematicas");

        // ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(questionRepository).findQuestionByExamById(captor.capture());

        assertEquals(1L, captor.getValue());
    }


    @Test
    void testDoThrow() {
        Exam exam = Data.EXAM;
        exam.setPreguntas(Data.QUESTIONS);
        doThrow(IllegalArgumentException.class).when(questionRepository).saveQuestions(anyList());
        // Verificamos que se lanza la excepción al intentar guardar preguntas
        assertThrows(IllegalArgumentException.class, () -> {
            examService.save(exam);
        });
    }

    @Test
    void testDoAnswer() {
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        when(questionRepository.findQuestionByExamById(anyLong())).thenAnswer(new Answer<List<String>>() {
            @Override
            public List<String> answer(InvocationOnMock invocation) throws Throwable {
                Long id = invocation.getArgument(0);
                return List.of("Pregunta 1 para el examen con ID: " + id);
            }
        });
    }

    @Test
    void testDoCallRealMethod() {
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        //when(questionRepository.findQuestionByExamById(anyLong())).thenReturn(Data.QUESTIONS);
        //when(questionRepository.findQuestionByExamById(anyLong())).thenCallRealMethod();

        doCallRealMethod().when(questionRepository).findQuestionByExamById(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Matematicas");
        assertEquals(1L, exam.getId());
        assertEquals("Matematicas", exam.getNombre());
    }

    /*
    Es un metodo de prueba llamado testSpy que utiliza Mockito para crear "spies"
    de los repositorios ExamRepositoryImpl y QuestionRepositoryImpl. Un "spy" en
    Mockito es un objeto que permite espiar (es decir, observar) las llamadas a
    os métodos reales del objeto, pero también permite simular (mock) algunos de
    sus métodos.
     */

    @Test
    void testSpy() {
        ExamRepository examRepository1 = spy(ExamRepositoryImpl.class);
        QuestionRepository questionRepository1 = spy(QuestionRepositoryImpl.class);
        ExamServiceImpl examService1 = new ExamServiceImpl(examRepository1, questionRepository1);

        when(questionRepository1.findQuestionByExamById(anyLong())).thenReturn(Data.QUESTIONS);

        Exam exam = examService1.findExamByNameWithQuestions("Matematicas");
        assertEquals(1L, exam.getId());
        assertEquals("Matematicas", exam.getNombre());
    }

    @Test
    void testOrderTheInvocation(){
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        when(questionRepository.findQuestionByExamById(anyLong())).thenReturn(Data.QUESTIONS);

        examService.findExamByNameWithQuestions("Matematicas");

        InOrder inOrder = inOrder(examRepository, questionRepository);
        inOrder.verify(examRepository).findAll();
        inOrder.verify(questionRepository).findQuestionByExamById(1L);
    }

    @Test
    void testNumberTheInvocation(){
        when(examRepository.findAll()).thenReturn(Data.DATA_EXAM);
        examService.findExamByNameWithQuestions("Matematicas");

        verify(questionRepository, times(1)).findQuestionByExamById(1L);
        verify(questionRepository, atLeast(1)).findQuestionByExamById(1L);
        verify(questionRepository, atLeastOnce()).findQuestionByExamById(1L);
        verify(questionRepository, atMost(20)).findQuestionByExamById(2L);
        // verify(questionRepository, atMostOnce()).findQuestionByExamById(3L); falla

    }

    @Test
    void testNumberInvocations3() {
        when(examRepository.findAll()).thenReturn(Collections.emptyList());
        examService.findExamByNameWithQuestions("Matematicas");

        verify(questionRepository, never()).findQuestionByExamById(1L);
        verifyNoInteractions(questionRepository);

        verify(examRepository).findAll();
        verify(examRepository, times(1)).findAll();
        verify(examRepository, atLeast(1)).findAll();
        verify(examRepository, atMost(10)).findAll();
        verify(examRepository, atMostOnce()).findAll();
    }
}