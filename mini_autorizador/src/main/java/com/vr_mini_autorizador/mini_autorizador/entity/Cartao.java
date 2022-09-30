package com.vr_mini_autorizador.mini_autorizador.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Cartao {
    
    @Id
    @Column(nullable = false)
    private String numeroCortao;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private Double valor;

    public Cartao(){}

    public Cartao(String numeroCartao, String senha, Double valor){
        this.numeroCortao = numeroCartao;
        this.senha = senha;
        this.valor = valor;
    }
    
}
