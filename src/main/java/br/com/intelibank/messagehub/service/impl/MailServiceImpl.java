package br.com.intelibank.messagehub.service.impl;

import br.com.intelibank.messagehub.domain.VerificationEmailEvent;
import br.com.intelibank.messagehub.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Objects;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    @KafkaListener(topics = "message-hub", groupId = "mail-group")
    public void sendEmailsByType(String message) {
        try {
            VerificationEmailEvent event = objectMapper.readValue(message, VerificationEmailEvent.class);

            if(Objects.equals(event.getType(), "verification_email")) {
                Context context = new Context();
                context.setVariable("verificationCode", event.getKey());

                // Processa o template com Thymeleaf
                String htmlContent = templateEngine.process("verify_email", context);

                // Configura o e-mail
                MimeMessage messageToSend = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(messageToSend, true);
                helper.setTo(event.getEmail());
                helper.setSubject("Confirmação de E-mail");
                helper.setText(htmlContent, true);

                mailSender.send(messageToSend);
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar a mensagem: " + e.getMessage());
        }
    }
}
