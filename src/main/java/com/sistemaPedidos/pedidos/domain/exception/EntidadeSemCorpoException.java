package com.sistemaPedidos.pedidos.domain.exception;

public class EntidadeSemCorpoException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public EntidadeSemCorpoException(String msg) {
        super(msg);
    }
}
