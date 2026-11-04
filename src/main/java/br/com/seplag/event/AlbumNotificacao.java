package br.com.seplag.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.*;

@Component
public class AlbumNotificacao {
    private final SimpMessagingTemplate mensagens;
    public AlbumNotificacao(SimpMessagingTemplate mensagens) { this.mensagens = mensagens; }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notificar(AlbumCriadoEvent criado) {
        mensagens.convertAndSend("/topic/albuns", criado);
    }
}
