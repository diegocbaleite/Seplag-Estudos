package br.com.seplag.exception;

public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String mensagem) { super(mensagem); }
}
