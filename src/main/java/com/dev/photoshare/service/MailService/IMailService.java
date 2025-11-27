package com.dev.photoshare.service.MailService;

public interface IMailService {
    void sendSimpleEmail(String to, String subject, String text);
    void sendHtmlEmail(String to, String subject, String htmlContent);
}

