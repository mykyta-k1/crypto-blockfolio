package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.UserService;
import com.crypto.blockfolio.domain.dto.UserAddDto;
import com.crypto.blockfolio.domain.exception.EntityNotFoundException;
import com.crypto.blockfolio.domain.exception.SignUpException;
import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.mindrot.bcrypt.BCrypt;

class UserServiceImpl extends GenericService<User, UUID> implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
        this.userRepository = userRepository;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(
                () -> new EntityNotFoundException("Користувача з таким логіном не існує."));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Користувача з таким email не існує."));
    }

    @Override
    public Set<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Set<User> getAll(Predicate<User> filter) {
        return new TreeSet<>(userRepository.findAll(filter));
    }

    @Override
    public User add(User entity) {
        throw new NotImplementedException(
            "Заборонено створювати користувача без використання DTO. Використовуйте add(UserAddDto).");
    }

    @Override
    public User add(UserAddDto userAddDto) {
        if (userRepository.findByEmail(userAddDto.getEmail()).isPresent()) {
            throw new SignUpException("Користувач із таким email вже існує.");
        }

        try {
            User user = new User(
                userAddDto.getId(),
                BCrypt.hashpw(userAddDto.getRawPassword(), BCrypt.gensalt()), // Хешування пароля
                userAddDto.getUsername(),
                userAddDto.getEmail()
            );
            userRepository.add(user);
            return user;
        } catch (Exception e) {
            throw new SignUpException("Помилка при створенні користувача: %s"
                .formatted(e.getMessage()));
        }
    }

    @Override
    public boolean remove(User user) {
        return userRepository.remove(user);
    }

    @Override
    public void generateReport(Predicate<User> filter) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"№", "Логін", "Email", "Дата створення"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        for (User user : getAll(filter)) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getEmail());
            row.createCell(3).setCellValue(user.getCreatedAt().toString());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = "users[%s].xls".formatted(LocalDateTime.now().toString())
            .replace(':', '-');

        Path outputPath = Path.of(REPORTS_DIRECTORY, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new SignUpException(
                "Помилка при збереженні звіту користувачів: %s".formatted(e.getMessage()));
        }
    }
}
