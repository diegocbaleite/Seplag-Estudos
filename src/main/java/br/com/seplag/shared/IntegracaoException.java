package br.com.seplag.shared;

public class IntegracaoException extends RuntimeException {
    public IntegracaoException(String mensagem, Throwable causa) { super(mensagem, causa); }
}
