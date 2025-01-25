package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.contract.SignUpService;
import com.crypto.blockfolio.domain.contract.UserService;
import com.crypto.blockfolio.domain.dto.UserAddDto;
import com.crypto.blockfolio.domain.exception.SignUpException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.function.Supplier;

class SignUpServiceImpl implements SignUpService {

    private static final int VERIFICATION_CODE_EXPIRATION_MINUTES = 1;
    private static LocalDateTime codeCreationTime;
    private final UserService userService;
    private final AuthService authService;

    public SignUpServiceImpl(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // відправлення на пошту
    private static void sendVerificationCodeEmail(String email, String verificationCode) {
        // Властивості для конфігурації підключення до поштового сервера
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Gmail SMTP сервер
        properties.put("mail.smtp.port", "587"); // Порт для Gmail
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Отримання сесії з автентифікацією
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("c.kryvobokov.mykyta@student.uzhnu.edu.ua",
                    "lieptqwtarsqmorw");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(
                "c.kryvobokov.mykyta@student.uzhnu.edu.ua")); // Замініть на власну адресу
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Код підтвердження");
            message.setText("Ваш код підтвердження: " + verificationCode);
            Transport.send(message);

            System.out.println("Повідомлення успішно відправлено.");

        } catch (MessagingException e) {
            throw new RuntimeException(
                "Помилка при відправці електронного листа: " + e.getMessage());
        }
    }

    private static String generateAndSendVerificationCode(String email) {
        // Генерація 6-значного коду
        String verificationCode = String.valueOf((int) (Math.random() * 900000 + 100000));

        sendVerificationCodeEmail(email, verificationCode);

        codeCreationTime = LocalDateTime.now();

        return verificationCode;
    }

    // Перевірка введеного коду
    private static void verifyCode(String inputCode, String generatedCode) {
        LocalDateTime currentTime = LocalDateTime.now();
        long minutesElapsed = ChronoUnit.MINUTES.between(codeCreationTime, currentTime);

        if (minutesElapsed > VERIFICATION_CODE_EXPIRATION_MINUTES) {
            throw new SignUpException("Час верифікації вийшов. Спробуйте ще раз.");
        }

        if (!inputCode.equals(generatedCode)) {
            throw new SignUpException("Невірний код підтвердження.");
        }

        // Скидання часу створення коду
        codeCreationTime = null;
    }

    @Override
    public boolean userExists(String username, String email) {
        boolean usernameExists = userService.getAll().stream()
            .anyMatch(user -> user.getUsername().equals(username));
        boolean emailExists = userService.getAll().stream()
            .anyMatch(user -> user.getEmail().equals(email));
        return usernameExists || emailExists;
    }

    @Override
    public void signUp(UserAddDto userAddDto, Supplier<String> waitForUserInput) {
        if (userExists(userAddDto.getUsername(), userAddDto.getEmail())) {
            throw new SignUpException("Користувач із таким логіном або email вже існує.");
        }

        String verificationCode = generateAndSendVerificationCode(userAddDto.getEmail());
        String userInputCode = waitForUserInput.get();

        verifyCode(userInputCode, verificationCode);

        userService.add(userAddDto);

        boolean isAuthenticated = authService.authenticate(userAddDto.getUsername(),
            userAddDto.getRawPassword());
        if (!isAuthenticated) {
            throw new SignUpException("Помилка під час авторизації після реєстрації.");
        }
    }

}
