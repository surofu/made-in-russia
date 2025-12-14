package com.surofu.exporteru.infrastructure.config;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    @Bean
    @Primary
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        // Таймауты
        props.put("mail.smtp.connectiontimeout", "60000");
        props.put("mail.smtp.timeout", "60000");
        props.put("mail.smtp.writetimeout", "60000");

        // Дополнительные настройки для доставляемости
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.ssl.ciphersuites",
                "TLS_AES_128_GCM_SHA256,TLS_AES_256_GCM_SHA384,TLS_CHACHA20_POLY1305_SHA256");

        // Отладка (включать только при необходимости)
        props.put("mail.debug", "false");

        return mailSender;
    }



    @Bean
    public Mailer mailer() {
        return MailerBuilder
                .withSMTPServer(host, port, username, password)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withSessionTimeout(60000)

                // Базовые SMTP настройки
                .withProperty("mail.smtp.connectiontimeout", 60000)
                .withProperty("mail.smtp.timeout", 60000)
                .withProperty("mail.smtp.writetimeout", 60000)
                .withProperty("mail.smtp.ssl.trust", host)
                .withProperty("mail.transport.protocol", "smtp")
                .withProperty("mail.smtp.auth", "true")
                .withProperty("mail.smtp.starttls.enable", "true")
                .withProperty("mail.smtp.starttls.required", "true")
                .withProperty("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3")
                .withProperty("mail.smtp.ssl.checkserveridentity", "true")

                // Критически важно для Yandex и iCloud
                .withProperty("mail.smtp.localhost", getDomainFromEmail(username))
                .withProperty("mail.smtp.ehlo", "true")
                .withProperty("mail.smtp.allow8bitmime", "true")

                // Для правильной обработки кириллицы
                .withProperty("mail.mime.charset", "UTF-8")
                .withProperty("mail.mime.encodefilename", "true")
                .withProperty("mail.mime.decodefilename", "true")

                // Защита от блокировок
                .withProperty("mail.smtp.sendpartial", "true")
                .withProperty("mail.smtp.quitwait", "false")

                .clearEmailValidator()
                .async()
                .withThreadPoolSize(10)
                .withConnectionPoolCoreSize(10)
                .buildMailer();
    }

    private String getDomainFromEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(email.indexOf("@") + 1);
        }
        return "localhost";
    }
}
