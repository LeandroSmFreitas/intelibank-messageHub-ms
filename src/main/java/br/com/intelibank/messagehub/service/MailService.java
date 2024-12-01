package br.com.intelibank.messagehub.service;

public interface MailService {

    void send(String to, String subject, String text);

    void sendEmailsByType(String message);
}
