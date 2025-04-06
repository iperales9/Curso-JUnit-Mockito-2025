package com.ivan.test.springboot.app.springboot_test.repositories;

import com.ivan.test.springboot.app.springboot_test.models.Banco;

import java.util.List;

public interface BancoRepository {
    List<Banco> findAll();

    Banco findById(Long id);

    void update(Banco banco);
}