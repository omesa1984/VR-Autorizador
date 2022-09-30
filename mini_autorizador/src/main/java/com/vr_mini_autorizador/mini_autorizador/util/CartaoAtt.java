package com.vr_mini_autorizador.mini_autorizador.util;

import lombok.Data;

@Data
public class CartaoAtt {
    
    private String numeroCartao;

    private String senha;

    public CartaoAtt(String numeroCartao, String senha){
        this.numeroCartao = numeroCartao;
        this.senha = senha;
    }
}
