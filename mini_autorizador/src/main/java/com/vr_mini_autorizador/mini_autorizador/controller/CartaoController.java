package com.vr_mini_autorizador.mini_autorizador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.vr_mini_autorizador.mini_autorizador.entity.Cartao;
import com.vr_mini_autorizador.mini_autorizador.repository.CartaoRepository;
import com.vr_mini_autorizador.mini_autorizador.util.CartaoAtt;
import com.vr_mini_autorizador.mini_autorizador.util.CartaoDto;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/")
public class CartaoController {
    
    @Autowired
    private CartaoRepository cartaoRepository;

    public CartaoController(CartaoRepository _cartaoRepository) {
        this.cartaoRepository = _cartaoRepository;
    }

    @GetMapping("/cartoes/{numeroCartao}")
    public ResponseEntity<String> GetCartao(String numeroCartao){
        ResponseEntity<Cartao> cartao = GetCartaoByNumber(numeroCartao);
        
        return (cartao != null && cartao.getBody() != null) ? new ResponseEntity<>(CriarJSON(cartao.getBody().getValor()), HttpStatus.OK)
        : new ResponseEntity<>(CriarJSON(), HttpStatus.NOT_FOUND);
    }

    /**
     * @param cartao
     * @return
     */
    @PostMapping("/cartoes")
    public ResponseEntity<String> Add(@RequestBody CartaoAtt cartaoAtt){
        int statusCode = GetCartao(cartaoAtt.getNumeroCartao()).getStatusCodeValue();
        boolean cartaoValidado = ValidarCartao(cartaoAtt.getNumeroCartao(), cartaoAtt.getSenha());

        switch (statusCode) {
            case 200:
            return new ResponseEntity<>(CriarJSON(cartaoAtt), HttpStatus.UNPROCESSABLE_ENTITY);
            case 404:
            if(cartaoValidado){
                Cartao bdCartao = new Cartao(cartaoAtt.getNumeroCartao(), cartaoAtt.getSenha(), (double) 500);
                cartaoRepository.save(bdCartao);
                return new ResponseEntity<>(CriarJSON(cartaoAtt), HttpStatus.CREATED);
            }
            default:
            return new ResponseEntity<>(CriarJSON(cartaoAtt), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private ResponseEntity<Cartao> GetCartaoByNumber(String numeroCartao){
        return cartaoRepository.findById(numeroCartao)
        .map(record -> ResponseEntity.ok().body(record))
        .orElse(ResponseEntity.notFound().build());
    }

    //Procedimentos para validações
    //Validar string do numero do cartão
    private boolean ValidarCartao(String numeroCartao, String senha){
        boolean cartaoValidado = (numeroCartao.length() == 16 && numeroCartao.matches("[0-9]{16}")) ? true : false;
        boolean senhaValidada = (senha.length() == 4 && senha.matches("[0-9]{4}")) ? true : false;

        return (cartaoValidado && senhaValidada) ? true : false;
    }

    //Procedimentos sobrecarregados para criar os objetos JSON
    private String CriarJSON(){
        Gson gson = new Gson();

        return gson.toJson(null);
    }
    private String CriarJSON(Double saldo){
        Gson gson = new Gson();

        return gson.toJson(saldo);
    }
    private String CriarJSON(CartaoAtt cartao){
        CartaoDto cDto = new CartaoDto(cartao.getSenha(), cartao.getNumeroCartao());

        Gson gson = new Gson();

        return gson.toJson(cDto);
    }
}
