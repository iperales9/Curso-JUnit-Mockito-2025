package org.ivan.appmockito.ejemplos;

import org.ivan.appmockito.ejemplos.models.Exam;

import java.util.List;

public class Data {
    // Simulamos una lista de exámenes
    public static final List<Exam> DATA_EXAM = List.of(
            new Exam(1L, "Matematicas"),
            new Exam(2L, "Historia"),
            new Exam(3L, "Geografia"));

    public static final List<Exam> DATA_EXAM_ID_NULL = List.of(
            new Exam(null, "Matematicas"),
            new Exam(null, "Historia"),
            new Exam(null, "Geografia")
    );
    public static final List<Exam> DATA_EXAM_ID_NEGATIVE = List.of(
            new Exam(-1L, "Matematicas"),
            new Exam(-2L, "Historia"),
            new Exam(-3L, "Geografia")
    );

    public static final List<String> QUESTIONS = List.of(
            "arithmetic",
            "algebra",
            "geometry",
            "trigonometry",
            "calculus",
            "statistics"
    );

    public static final Exam EXAM = new Exam(null, "Fisica");

}
