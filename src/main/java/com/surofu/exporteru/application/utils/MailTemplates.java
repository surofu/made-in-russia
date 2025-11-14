package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.application.dto.translation.HstoreTranslationDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.MailException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

public final class MailTemplates {

    public static String getEmailVerification(String code, String expirationDate, Locale locale) {
        if (locale.getLanguage().equalsIgnoreCase("ru")) {
            return getEmailVerificationMessageRu(code, expirationDate);
        }

        if (locale.getLanguage().equalsIgnoreCase("en")) {
            return getEmailVerificationMessageEn(code, expirationDate);
        }

        if (locale.getLanguage().equalsIgnoreCase("zh")) {
            return getEmailVerificationMessageZh(code, expirationDate);
        }

        return getEmailVerificationMessageEn(code, expirationDate);
    }

    public static String getDeleteAccountMail(Locale locale) throws MailException {
        if (locale.getLanguage().equalsIgnoreCase("ru")) {
            return getDeleteAccountMailEn();
        }

        if (locale.getLanguage().equalsIgnoreCase("en")) {
            return getDeleteAccountMailRu();
        }

        if (locale.getLanguage().equalsIgnoreCase("zh")) {
            return getDeleteAccountMailZh();
        }

        return getDeleteAccountMailEn();
    }

    public static String getConfirmDeleteAccountMail(String code, String expirationDate, Locale locale) throws MailException {
        if (locale.getLanguage().equalsIgnoreCase("en")) {
            return getConfirmDeleteAccountMailEn(code, expirationDate);
        }

        if (locale.getLanguage().equalsIgnoreCase("ru")) {
            return getConfirmDeleteAccountMailRu(code, expirationDate);
        }

        if (locale.getLanguage().equalsIgnoreCase("zh")) {
            return getConfirmDeleteAccountMailZh(code, expirationDate);
        }

        return getConfirmDeleteAccountMailEn(code, expirationDate);
    }

    public static String getOrderMail(String productUrl, String productTitle, BigDecimal originalPrice, BigDecimal discountedPrice,
                                      String firstName, String email, String phoneNumber,
                                      Integer quantity) throws MailException {

        return """
                <!DOCTYPE html>
                            <html lang="ru">
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>\uD83D\uDCE6 Новый заказ</title>
                                <style>
                                    body {
                                        font-family: Arial, sans-serif;
                                        background-color: #f4f4f4;
                                        color: #333;
                                        padding: 20px;
                                    }
                
                                    .container {
                                        background-color: #fff;
                                        border-radius: 8px;
                                        padding: 20px;
                                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                                        max-width: 600px;
                                        margin: 0 auto;
                                    }
                
                                    .header {
                                        text-align: center;
                                        padding: 20px 0;
                                        border-bottom: 2px solid #eee;
                                        margin-bottom: 20px;
                                    }
                
                                    .order-alert {
                                        background-color: #e8f5e9;
                                        padding: 15px;
                                        border-radius: 6px;
                                        text-align: center;
                                        margin: 20px 0;
                                        border-left: 4px solid #4CAF50;
                                    }
                
                                    .info-block {
                                        background-color: #f9f9f9;
                                        padding: 15px;
                                        border-radius: 6px;
                                        margin: 15px 0;
                                    }
                
                                    .label {
                                        font-weight: bold;
                                        color: #555;
                                        width: 150px;
                                    }
                
                                    .footer {
                                        margin-top: 30px;
                                        text-align: center;
                                        color: #777;
                                        font-size: 14px;
                                        border-top: 1px solid #eee;
                                        padding-top: 20px;
                                    }
                                </style>
                            </head>
                            <body>
                            <div class="container">
                                <div class="header">
                                    <h2>\uD83D\uDCE6 Новый заказ</h2>
                                </div>
                
                                <div class="order-alert">
                                    <strong>Поступил новый заказ</strong>
                                </div>
                
                                <div class="info-block">
                                    <p><span class="label">Покупатель:</span> %s</p>
                                    <p><span class="label">Email:</span> <a href="mailto:%s">%s</a></p>
                                    <p><span class="label">Телефон:</span> %s</p>
                                </div>
                
                                <div class="info-block">
                                    <p><span class="label">Товар:</span> <a href="%s">%s</a></p>
                                    <p><span class="label">Цена:</span> %s</p>
                                    <p><span class="label">Цена со скидкой:</span> %s</p>
                                    <p><span class="label">Количество:</span> %s</p>
                                </div>
                
                                <div class="footer">
                                    <p>С уважением Exporteru.com</p>
                                </div>
                            </div>
                            </body>
                            </html>
                """.formatted(
                Objects.requireNonNullElse(StringUtils.trimToNull(firstName), "-"),
                email, email,
                Objects.requireNonNullElse(StringUtils.trimToNull(phoneNumber), "-"),
                productUrl,
                productTitle,
                originalPrice.setScale(0, RoundingMode.DOWN).toString(),
                discountedPrice.setScale(0, RoundingMode.DOWN).toString(),
                quantity
        );
    }

    public static String getSupportMail(String username, String fromMail, String phoneNumber, String subject, String content, String date) {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Обращение в поддержку от пользователя</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                            margin: 0;
                        }
                
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 25px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                            max-width: 650px;
                            margin: 0 auto;
                        }
                
                        .header {
                            color: #2c5aa0;
                            border-bottom: 2px solid #eee;
                            padding-bottom: 15px;
                            margin-bottom: 20px;
                            text-align: center;
                        }
                
                        .user-info {
                            background-color: #f9f9f9;
                            padding: 15px;
                            border-left: 4px solid #3498db;
                            border-radius: 4px;
                            margin: 20px 0;
                            font-size: 15px;
                        }
                
                        .message-block {
                            background-color: #fff8e1;
                            padding: 20px;
                            border: 1px solid #ffeaa7;
                            border-radius: 6px;
                            margin: 20px 0;
                            white-space: pre-line; /* Сохраняет переносы строк из сообщения пользователя */
                        }
                
                        .footer {
                            margin-top: 25px;
                            font-size: 14px;
                            text-align: center;
                            color: #7f8c8d;
                            border-top: 1px solid #eee;
                            padding-top: 15px;
                        }
                
                        .label {
                            font-weight: bold;
                            color: #555;
                        }
                
                        .media {
                            display: flex;
                            flex-direction: column;
                            gap: 10px;
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                    <div class="header">
                        <h1>✉️ Новое обращение в поддержку</h1>
                    </div>
                
                    <div class="user-info">
                        <p><span class="label">От пользователя: %s</span></p>
                        <p><span class="label">Email для связи:</span>
                           <a href="mailto:[%s]">[%s]</a>
                        </p>
                        <p><span class="label">Телефон для связи:</span>
                           <strong>%s</strong>
                        </p>
                        <p><span class="label">Дата и время обращения:</span>%s</p>
                    </div>
                
                    <h2>%s</h2>
                    <div class="message-block">%s</div>
                
                    <p><em>Обращение обработано и зарегистрировано в системе поддержки.</em></p>
                
                    <div class="footer">
                        <p>С уважением,<br>Автоматизированная система уведомлений</p>
                        <p>Exporteru Support</p>
                    </div>
                </div>
                </body>
                </html>
                """.formatted(username, fromMail, fromMail, phoneNumber, date, subject, content);
    }

    private static String getEmailVerificationMessageRu(String code, String expirationDate) {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Подтверждение электронной почты</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        }
                
                        h1 {
                            font-size: 72px;
                            color: #4CAF50;
                            text-align: center;
                        }
                
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                        }
                
                        img {
                            display: block;
                            margin: 0 auto 20px;
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                    <h2>Здравствуйте!</h2>
                    <p>Спасибо за регистрацию в Exporteru! Чтобы завершить процесс регистрации, пожалуйста, подтвердите свою электронную
                        почту, введя код ниже:</p>
                    <strong>Ваш код подтверждения:</strong>
                    <h1>%s</h1>
                    <p>Пожалуйста, введите этот код в соответствующее поле на нашем сайте. Если вы не регистрировались на сайте
                        Exporteru.com, просто проигнорируйте это сообщение.</p>
                    <p>Код действителен до: %s</p>
                    <p>Спасибо, что выбираете Exporteru!</p>
                    <div class="footer">
                        <p>С уважением, <br>Команда exporteru.com</p>
                    </div>
                </div>
                </body>
                </html>
                """.formatted(code, expirationDate);
    }

    private static String getEmailVerificationMessageEn(String code, String expirationDate) {
        return """
                <!DOCTYPE html>
                       <html lang="en">
                       <head>
                           <meta charset="UTF-8">
                           <meta name="viewport" content="width=device-width, initial-scale=1.0">
                           <title>Email Confirmation</title>
                           <style>
                               body {
                                   font-family: Arial, sans-serif;
                                   background-color: #f4f4f4;
                                   color: #333;
                                   padding: 20px;
                               }
                
                               .container {
                                   background-color: #fff;
                                   border-radius: 8px;
                                   padding: 20px;
                                   box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                               }
                
                               h1 {
                                   font-size: 72px;
                                   color: #4CAF50;
                                   text-align: center;
                               }
                
                               .footer {
                                   margin-top: 20px;
                                   font-size: 14px;
                                   text-align: center;
                               }
                
                               img {
                                   display: block;
                                   margin: 0 auto 20px;
                               }
                           </style>
                       </head>
                       <body>
                       <div class="container">
                           <h2>Hello!</h2>
                           <p>Thank you for registering with Exporteru! To complete your registration, please confirm your email address by entering the code below:</p>
                           <strong>Your confirmation code:</strong>
                           <h1>%s</h1>
                           <p>Please enter this code in the appropriate field on our website. If you did not register on Exporteru.com, please ignore this message.</p>
                           <p>Code is valid until: %s</p>
                           <p>Thank you for choosing Exporteru!</p>
                           <div class="footer">
                               <p>Best regards, <br>The exporteru.com team</p>
                           </div>
                       </div>
                       </body>
                       </html>
                """.formatted(code, expirationDate);
    }

    private static String getEmailVerificationMessageZh(String code, String expirationDate) {
        return """
                <!DOCTYPE html>
                <html lang="zh">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>电子邮件确认</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        }
                
                        h1 {
                            font-size: 72px;
                            color: #4CAF50;
                            text-align: center;
                        }
                
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                        }
                
                        img {
                            display: block;
                            margin: 0 auto 20px;
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                    <h2>您好！</h2>
                    <p>感谢您注册Exporteru！要完成注册，请输入以下验证码确认您的电子邮件地址：</p>
                    <strong>您的验证码：</strong>
                    <h1>%s</h1>
                    <p>请在网站上输入此验证码。如果您没有在Exporteru.com上注册，请忽略此邮件。</p>
                    <p>验证码有效期至：%s</p>
                    <p>感谢您选择Exporteru！</p>
                    <div class="footer">
                        <p>此致<br>exporteru.com团队</p>
                    </div>
                </div>
                </body>
                </html>
                """.formatted(code, expirationDate);
    }

    private static String getDeleteAccountMailEn() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Account Deletion</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                            max-width: 600px;
                            margin: 0 auto;
                        }
                
                        h2 {
                            color: #d9534f;
                        }
                
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                    <div>
                        <h2>Hello!</h2>
                        <p>Your account on exporteru.com has been deleted.</p>
                        <p>Please complete the registration procedure again. If you did not register on Exporteru.com, simply ignore this message.</p>
                        <div class="footer">
                            <p>Best regards, <br>The exporteru.com team</p>
                        </div>
                    </div>
                </div>
                </body>
                </html>
                """;
    }

    private static String getDeleteAccountMailRu() {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Удаление аккаунта</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                            max-width: 600px;
                            margin: 0 auto;
                        }
                
                        h2 {
                            color: #d9534f;
                        }
                
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                    <div>
                        <h2>Здравствуйте!</h2>
                        <p>Ваш аккаунт на сайте exporteru.com был удален.</p>
                        <p>Пожалуйста, пройдите процедуру регистрации заново. Если вы не регистрировались на сайте Exporteru.com, просто проигнорируйте это сообщение.</p>
                        <div class="footer">
                            <p>С уважением, <br>Команда exporteru.com</p>
                        </div>
                    </div>
                </div>
                </body>
                </html>
                """;
    }

    private static String getDeleteAccountMailZh() {
        return """
                <!DOCTYPE html>
                <html lang="zh">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>帐户删除</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                            max-width: 600px;
                            margin: 0 auto;
                        }
                
                        h2 {
                            color: #d9534f;
                        }
                
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                            color: #666;
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                    <div>
                        <h2>您好！</h2>
                        <p>您在 exporteru.com 上的帐户已被删除。</p>
                        <p>请重新完成注册程序。如果您没有在 Exporteru.com 上注册，请忽略此消息。</p>
                        <div class="footer">
                            <p>此致敬礼，<br>exporteru.com 团队</p>
                        </div>
                    </div>
                </div>
                </body>
                </html>
                """;
    }

    private static String getConfirmDeleteAccountMailEn(String code, String expirationDate) {
        return """
                <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Account Deletion Confirmation</title>
                            <style>
                                body {
                                    font-family: Arial, sans-serif;
                                    background-color: #f4f4f4;
                                    color: #333;
                                    padding: 20px;
                                }
                
                                .container {
                                    background-color: #fff;
                                    border-radius: 8px;
                                    padding: 20px;
                                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                                }
                
                                h1 {
                                    font-size: 72px;
                                    color: #4CAF50;
                                    text-align: center;
                                }
                
                                .footer {
                                    margin-top: 20px;
                                    font-size: 14px;
                                    text-align: center;
                                }
                
                                img {
                                    display: block;
                                    margin: 0 auto 20px;
                                }
                            </style>
                        </head>
                        <body>
                        <div class="container">
                            <h2>Hello!</h2>
                            <p>To complete the account deletion process, please confirm by entering the code below:</p>
                            <strong>Your confirmation code:</strong>
                            <h1>%s</h1>
                            <p>Please enter this code in the appropriate field on our website. If you did not sign up for an account on
                                Exporteru.com, please ignore this message.</p>
                            <p>The code is valid until: %s</p>
                            <p>Thank you for choosing Exporteru!</p>
                            <div class="footer">
                                <p>Best regards, <br>The exporteru.com team</p>
                            </div>
                        </div>
                        </body>
                        </html>
                """.formatted(code, expirationDate);
    }

    private static String getConfirmDeleteAccountMailRu(String code, String expirationDate) {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Подтверждение удаления аккаунта</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            color: #333;
                            padding: 20px;
                        }
                
                        .container {
                            background-color: #fff;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        }
                
                        h1 {
                            font-size: 72px;
                            color: #4CAF50;
                            text-align: center;
                        }
                
                        .footer {
                            margin-top: 20px;
                            font-size: 14px;
                            text-align: center;
                        }
                
                        img {
                            display: block;
                            margin: 0 auto 20px;
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                    <h2>Здравствуйте!</h2>
                    <p>Чтобы завершить процесс удаления аккаунта, пожалуйста, подтвердите удаление, введя код ниже:</p>
                    <strong>Ваш код подтверждения:</strong>
                    <h1>%s</h1>
                    <p>Пожалуйста, введите этот код в соответствующее поле на нашем сайте. Если вы не регистрировались на сайте
                        Exporteru.com, просто проигнорируйте это сообщение.</p>
                    <p>Код действителен до: %s</p>
                    <p>Спасибо, что выбираете Exporteru!</p>
                    <div class="footer">
                        <p>С уважением, <br>Команда exporteru.com</p>
                    </div>
                </div>
                </body>
                </html>
                """.formatted(code, expirationDate);
    }

    private static String getConfirmDeleteAccountMailZh(String code, String expirationDate) {
        return """
                <!DOCTYPE html>
                        <html lang="zh">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>账户删除确认</title>
                            <style>
                                body {
                                    font-family: "Microsoft YaHei", Arial, sans-serif;
                                    background-color: #f4f4f4;
                                    color: #333;
                                    padding: 20px;
                                }
                
                                .container {
                                    background-color: #fff;
                                    border-radius: 8px;
                                    padding: 20px;
                                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                                }
                
                                h1 {
                                    font-size: 72px;
                                    color: #4CAF50;
                                    text-align: center;
                                }
                
                                .footer {
                                    margin-top: 20px;
                                    font-size: 14px;
                                    text-align: center;
                                }
                
                                img {
                                    display: block;
                                    margin: 0 auto 20px;
                                }
                            </style>
                        </head>
                        <body>
                        <div class="container">
                            <h2>您好！</h2>
                            <p>为了完成账户删除流程，请输入下方的验证码进行确认：</p>
                            <strong>您的验证码：</strong>
                            <h1>%s</h1>
                            <p>请在我们的网站上的相应字段输入此代码。如果您并未在 Exporteru.com 注册账户，请忽略此邮件。</p>
                            <p>验证码有效期至：%s</p>
                            <p>感谢您选择 Exporteru！</p>
                            <div class="footer">
                                <p>此致，<br>exporteru.com 团队</p>
                            </div>
                        </div>
                        </body>
                        </html>
                """.formatted(code, expirationDate);
    }

    public static String getPhoneRequestMail(String firstName, String email, String phoneNumber, String date) {
        return """
            <!DOCTYPE html>
            <html lang="ru">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>📞 Call Request</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        color: #333;
                        padding: 20px;
                    }
    
                    .container {
                        background-color: #fff;
                        border-radius: 8px;
                        padding: 20px;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                        max-width: 600px;
                        margin: 0 auto;
                    }
    
                    .header {
                        text-align: center;
                        padding: 20px 0;
                        border-bottom: 2px solid #eee;
                        margin-bottom: 20px;
                    }
    
                    .call-alert {
                        background-color: #e3f2fd;
                        padding: 15px;
                        border-radius: 6px;
                        text-align: center;
                        margin: 20px 0;
                        border-left: 4px solid #2196F3;
                    }
    
                    .info-block {
                        background-color: #f9f9f9;
                        padding: 15px;
                        border-radius: 6px;
                        margin: 15px 0;
                    }
    
                    .label {
                        font-weight: bold;
                        color: #555;
                        width: 240px;
                        display: inline-block;
                    }
    
                    .footer {
                        margin-top: 30px;
                        text-align: center;
                        color: #777;
                        font-size: 14px;
                        border-top: 1px solid #eee;
                        padding-top: 20px;
                    }
    
                    .priority {
                        background-color: #fff3e0;
                        padding: 10px;
                        border-radius: 4px;
                        text-align: center;
                        margin: 10px 0;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
            <div class="container">
                <div class="header">
                    <h2>📞 Заявка на звонок / Call Request / 通话请求</h2>
                </div>
    
                <div class="call-alert">
                    <strong>Новая заявка на обратный звонок / New callback request / 新的回电请求</strong>
                </div>
    
                <div class="info-block">
                    <p><span class="label">Имя / Name / 姓名:</span> %s</p>
                    <p><span class="label">Email / 邮箱:</span> <a href="mailto:%s">%s</a></p>
                    <p><span class="label">Телефон / Phone / 电话:</span> <strong>%s</strong></p>
                </div>
    
                <div class="info-block">
                    <p><span class="label">Тип запроса / Request type / 请求类型:</span> Обратный звонок / Callback / 回电</p>
                    <p><span class="label">Статус / Status / 状态:</span> ⏳ Ожидает обработки / Pending / 待处理</p>
                    <p><span class="label">Время запроса / Request time / 请求时间:</span> %s</p>
                </div>
    
                <div class="footer">
                    <p>С уважением / Best regards / 此致 Exporteru.com</p>
                    <p><em>Пожалуйста, свяжитесь с клиентом в ближайшее время / Please contact the customer as soon as possible / 请尽快联系客户</em></p>
                </div>
            </div>
            </body>
            </html>
            """.formatted(
                Objects.requireNonNullElse(StringUtils.trimToNull(firstName), "-"),
                email, email,
                Objects.requireNonNullElse(StringUtils.trimToNull(phoneNumber), "-"),
                date
        );
    }

    public static String getRejectedProductMail(String productUrl, HstoreTranslationDto productTitleTranslations) {
        return """
        <!DOCTYPE html>
        <html lang="ru">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>⚠️ Товар отклонен</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    color: #333;
                    padding: 20px;
                }

                .container {
                    background-color: #fff;
                    border-radius: 8px;
                    padding: 20px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                    max-width: 600px;
                    margin: 0 auto;
                }

                .header {
                    text-align: center;
                    padding: 20px 0;
                    border-bottom: 2px solid #eee;
                    margin-bottom: 20px;
                }

                .rejection-alert {
                    background-color: #ffebee;
                    padding: 15px;
                    border-radius: 6px;
                    text-align: center;
                    margin: 20px 0;
                    border-left: 4px solid #f44336;
                }

                .info-block {
                    background-color: #f9f9f9;
                    padding: 15px;
                    border-radius: 6px;
                    margin: 15px 0;
                }

                .label {
                    font-weight: bold;
                    color: #555;
                    width: 240px;
                    display: inline-block;
                }

                .footer {
                    margin-top: 30px;
                    text-align: center;
                    color: #777;
                    font-size: 14px;
                    border-top: 1px solid #eee;
                    padding-top: 20px;
                }

                .product-link {
                    color: #2196F3;
                    text-decoration: none;
                    font-weight: normal;
                }

                .product-link:hover {
                    text-decoration: underline;
                }
            </style>
        </head>
        <body>
        <div class="container">
            <div class="header">
                <h2>⚠️ Товар отклонен</h2>
            </div>

            <div class="rejection-alert">
                <strong>Модерация отклонила ваш товар</strong>
            </div>

            <div class="info-block">
                <p><span class="label">Товар:</span> <a href="%s" class="product-link">%s</a></p>
            </div>

            <div class="info-block">
                <p><span class="label">Статус:</span> ❌ Отклонен</p>
                <p><span class="label">Причина:</span> Не соответствует правилам платформы</p>
            </div>

            <div class="footer">
                <p>С уважением Exporteru.com</p>
                <p><em>Для уточнения деталей обратитесь в поддержку</em></p>
            </div>
        </div>
        </body>
        </html>
        """.formatted(
                productUrl,
                productTitleTranslations.textRu()
        );
    }
}
