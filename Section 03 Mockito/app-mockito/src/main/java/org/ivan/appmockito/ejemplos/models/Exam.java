package org.ivan.appmockito.ejemplos.models;

import java.util.List;

public class Exam {
    private Long id;
    private String nombre;
    private List<String> preguntas;

    public Exam(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.preguntas = List.of();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<String> preguntas) {
        this.preguntas = preguntas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
