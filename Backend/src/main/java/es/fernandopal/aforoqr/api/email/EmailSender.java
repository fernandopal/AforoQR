package es.fernandopal.aforoqr.api.email;

public interface EmailSender {
    void send(String to, String subject, String email);
}
