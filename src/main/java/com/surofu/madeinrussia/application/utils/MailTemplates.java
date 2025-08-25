package com.surofu.madeinrussia.application.utils;

import org.springframework.mail.MailException;

import java.util.Locale;

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
        return "Your account has been deleted";
    }

    private static String getDeleteAccountMailRu() {
        return "Ваш аккаунт был удален";
    }

    private static String getDeleteAccountMailZh() {
        return "您的账户已被删除。";
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
}
