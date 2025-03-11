package com.sistemaPedidos.pedidos.domain.exception;

public class EntidadeEmUsoException  extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public EntidadeEmUsoException(String msg) {
        super(msg);
    }
}
