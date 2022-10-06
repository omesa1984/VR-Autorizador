package com.vr_mini_autorizador.mini_autorizador.util;

public class CartaoViewDto {
    
    private String senha;    

    private String numeroCartao;

    public CartaoViewDto(String senha, String numeroCartao) {
        this.senha = senha;
        this.numeroCartao = numeroCartao;
    }

}
