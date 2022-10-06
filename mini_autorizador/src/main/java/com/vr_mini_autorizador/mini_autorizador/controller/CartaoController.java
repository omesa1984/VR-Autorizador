package com.vr_mini_autorizador.mini_autorizador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.vr_mini_autorizador.mini_autorizador.entity.CartaoEntity;
import com.vr_mini_autorizador.mini_autorizador.repository.CartaoRepository;
import com.vr_mini_autorizador.mini_autorizador.util.CartaoAttDto;
import com.vr_mini_autorizador.mini_autorizador.util.CartaoViewDto;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/")
public class CartaoController {
    
    @Autowired
    private CartaoRepository cartaoRepository;

    @PostMapping("/cartoes")
    public ResponseEntity<String> Add(@RequestBody CartaoAttDto cartaoAtt){
        int statusCode = GetCartao(cartaoAtt.getNumeroCartao()).getStatusCodeValue();
        boolean cartaoValidado = ValidarCartao(cartaoAtt.getNumeroCartao(), cartaoAtt.getSenha());

        switch (statusCode) {
            case 200:
            return new ResponseEntity<>(CriarJSON(cartaoAtt), HttpStatus.UNPROCESSABLE_ENTITY);
            case 404:
            if(cartaoValidado){
                CartaoEntity bdCartao = new CartaoEntity(cartaoAtt.getNumeroCartao(), cartaoAtt.getSenha(), (double) 500);
                cartaoRepository.save(bdCartao);
                return new ResponseEntity<>(CriarJSON(cartaoAtt), HttpStatus.CREATED);
            }
            default:
            return new ResponseEntity<>(CriarJSON(cartaoAtt), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/cartoes/{numeroCartao}")
    public ResponseEntity<String> GetCartao(String numeroCartao){
        ResponseEntity<CartaoEntity> cartao = GetCartaoByNumber(numeroCartao);

        return (cartao != null && cartao.getBody() != null) ? new ResponseEntity<>(CriarJSON(cartao.getBody().getValor()), HttpStatus.OK)
        : new ResponseEntity<>(CriarJSON(), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/transacoes")
    public ResponseEntity<String> Transacoes(@RequestBody CartaoEntity transacao){
        boolean cartaoValidado = ValidarCartao(transacao.getNumeroCartao(), transacao.getSenha());
        
        if(cartaoValidado){
            ResponseEntity<CartaoEntity> cartao = cartaoRepository.findById(transacao.getNumeroCartao())
            .map(record -> ResponseEntity.ok().body(record))
            .orElse(ResponseEntity.notFound().build());

            ResponseEntity<String> aux = ErrorTransacao(transacao, cartao.getBody(), cartao.getStatusCodeValue());

            if(aux.getBody() != null){
                return aux;
            }
            Double atualizarSaldo = cartao.getBody().getValor() - transacao.getValor();
            cartao.getBody().setValor(atualizarSaldo);

            cartaoRepository.save(cartao.getBody());

            return new ResponseEntity<>("OK", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("SENHA_INVALIDA", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private ResponseEntity<String> ErrorTransacao(CartaoEntity transacao, CartaoEntity cartao, int statusCode){
        
        return (cartao == null) ? new ResponseEntity<>("CARTAO_INEXISTENTE", HttpStatus.UNPROCESSABLE_ENTITY)
               : (!transacao.getSenha().equals(cartao.getSenha())) ? new ResponseEntity<>("SENHA_INVALIDA", HttpStatus.UNPROCESSABLE_ENTITY)
               : (cartao.getValor() == 0 || cartao.getValor() - transacao.getValor() < 0) ? new ResponseEntity<>("SALDO_INSUFICIENTE", HttpStatus.UNPROCESSABLE_ENTITY)
               : new ResponseEntity<>(null, HttpStatus.OK);
    }

    private ResponseEntity<CartaoEntity> GetCartaoByNumber(String numeroCartao){
        return cartaoRepository.findById(numeroCartao)
        .map(record -> ResponseEntity.ok().body(record))
        .orElse(ResponseEntity.notFound().build());
    }

    //Procedimentos para validações
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
    private String CriarJSON(CartaoAttDto cartao){
        CartaoViewDto cDto = new CartaoViewDto(cartao.getSenha(), cartao.getNumeroCartao());

        Gson gson = new Gson();

        return gson.toJson(cDto);
    }
}
