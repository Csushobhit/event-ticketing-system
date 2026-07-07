package com.event.ticketing.eventticketingsystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmailWithAttachment(
            String to,
            String subject,
            String body,
            byte[] attachmentData,
            String attachmentName
    ) {

        log.info(
                "Attempting to send email to {} on thread: {}",
                to,
                Thread.currentThread().getName()
        );

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true
                    );

            helper.setFrom(
                    "noreply@eventticketing.local"
            );

            helper.setTo(to);

            helper.setSubject(subject);

            helper.setText(
                    body,
                    true
            );

            if (
                    attachmentData != null
                            && attachmentData.length > 0
            ) {

                helper.addAttachment(
                        attachmentName,
                        new ByteArrayResource(
                                attachmentData
                        )
                );

                log.info(
                        "Attachment {} added to the email.",
                        attachmentName
                );
            }

            mailSender.send(message);

            log.info(
                    "Email sent successfully to {} on thread: {}",
                    to,
                    Thread.currentThread().getName()
            );

        } catch (MessagingException e) {

            log.error(
                    "Failed to send email to {}: {}",
                    to,
                    e.getMessage(),
                    e
            );
        }
    }
}