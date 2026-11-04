package br.com.seplag.exception;

public class IntegracaoException extends RuntimeException {
    public IntegracaoException(String mensagem, Throwable causa) { super(mensagem, causa); }
}
