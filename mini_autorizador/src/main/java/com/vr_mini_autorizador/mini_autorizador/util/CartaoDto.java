package com.vr_mini_autorizador.mini_autorizador.util;

public class CartaoDto {
    
    private String senha;

    private String numeroCartao;

    public CartaoDto(String senha, String numeroCartao) {
        this.senha = senha;
        this.numeroCartao = numeroCartao;
    }
}
