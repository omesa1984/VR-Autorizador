package com.vr_mini_autorizador.mini_autorizador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vr_mini_autorizador.mini_autorizador.entity.CartaoEntity;

@Repository
public interface CartaoRepository extends JpaRepository<CartaoEntity, String>{
    
}
