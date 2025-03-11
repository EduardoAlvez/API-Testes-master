package com.sistemaPedidos.pedidos.domain.exception;

public class EntidadeComEsseNomeJaCriadaException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public EntidadeComEsseNomeJaCriadaException(String msg) {
        super(msg);
    }
}
