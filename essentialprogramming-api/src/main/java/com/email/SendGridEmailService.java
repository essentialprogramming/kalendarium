package com.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.util.cloud.DeploymentConfiguration;
import com.util.exceptions.ServiceException;
import com.util.text.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

class SendGridEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    private static final String EMAIL_FROM = DeploymentConfiguration.getProperty("EMAIL_FROM", "noreply@essentialprogramming.com");
    private static final String EMAIL_FROM_NAME = DeploymentConfiguration.getProperty("EMAIL_FROM_NAME", "EssentialProgramming Team");

    private final SendGrid sendGridClient;

    public SendGridEmailService(String sendGridApiKey) {

        this.sendGridClient = new SendGrid(sendGridApiKey);
    }

    @Override
    public void sendMail(String recipient, String subject, String htmlContent) {
        Email from = new Email(EMAIL_FROM, EMAIL_FROM_NAME);
        Email to = new Email(recipient);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, StringUtils.encodeText(subject), to, content);


        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGridClient.api(request);
            if (response.getStatusCode() != 202) {
                throw new ServiceException(TemplateErrorCode.UNABLE_TO_SEND_EMAIL,
                        "Unable to send email for: " + recipient + " with subject:" + subject + ". Sendgrid response status code: " + response.getStatusCode());
            }
        } catch (IOException ex) {
            throw new ServiceException(TemplateErrorCode.UNABLE_TO_SEND_EMAIL,
                    "Unable to send email for: " + recipient + " with subject:" + subject + ".");
        }
    }
}
