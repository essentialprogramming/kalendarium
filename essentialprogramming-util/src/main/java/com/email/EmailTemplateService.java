package com.email;

import com.util.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class EmailTemplateService {

    private final EmailService emailService;
    private final TemplateService templateService;

    @Autowired
    public EmailTemplateService(EmailService emailService, TemplateService templateService) {
        this.emailService = emailService;
        this.templateService = templateService;
    }

    public void send(Map<String, Object> content, String recipient, String subject, Template template, Locale locale) {
        if (content == null || recipient == null || subject == null || template == null) {
            throw new ServiceException(TemplateErrorCode.UNABLE_TO_GENERATE_EMAIL_TEMPLATE,
                    "Mandatory non-null parameters: content, recipient, subject, template");
        }

        emailService.sendMail(recipient, subject, templateService.generateHTML(content, template, locale));
    }
}
