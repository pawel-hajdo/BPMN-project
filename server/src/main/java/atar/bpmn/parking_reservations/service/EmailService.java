package atar.bpmn.parking_reservations.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String email;
    private static final String RESERVATION_REGISTERED_EMAIL = "reservation-registered-email";
    private static final String RESERVATION_PAYMENT_EMAIL = "reservation-payment-email";
    private static final String RESERVATION_SUCCESS_EMAIL = "reservation-success-email";

    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendReservationRegisteredEmail(String to) {
        var context = new Context();
        String emailContent = createEmailTemplate(RESERVATION_REGISTERED_EMAIL, context);
        sendHtmlEmail(to, "Reservation Registration Complete", emailContent);
    }

    public void sendReservationPaymentEmail(String to) {
        var context = new Context();
        String emailContent = createEmailTemplate(RESERVATION_PAYMENT_EMAIL, context);
        sendHtmlEmail(to, "Reservation Payment Confirmation", emailContent);
    }

    public void sendReservationSuccessEmail(String to, String accessCode) {
        var context = new Context();
        context.setVariable("accessCode", accessCode);
        String emailContent = createEmailTemplate(RESERVATION_SUCCESS_EMAIL, context);
        sendHtmlEmail(to, "Reservation Completed Successfully", emailContent);
    }

    private String createEmailTemplate(String templateName, Context context) {
        return templateEngine.process(templateName, context);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            var message = javaMailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(email);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
