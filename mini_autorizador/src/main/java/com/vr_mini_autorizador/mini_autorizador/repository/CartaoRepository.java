package com.vr_mini_autorizador.mini_autorizador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vr_mini_autorizador.mini_autorizador.entity.Cartao;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, String>{
    
}
