package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.TransactionService;
import com.crypto.blockfolio.domain.dto.TransactionAddDto;
import com.crypto.blockfolio.domain.exception.EntityNotFoundException;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.entity.TransactionType;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

class TransactionServiceImpl extends GenericService<Transaction, UUID> implements
    TransactionService {

    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
        PortfolioRepository portfolioRepository) {
        super(transactionRepository);
        this.transactionRepository = transactionRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Transaction addTransaction(TransactionAddDto transactionAddDto,
        CryptocurrencyRepository cryptocurrencyRepository) {
        Cryptocurrency cryptocurrency = cryptocurrencyRepository
            .findById(transactionAddDto.getCryptocurrencySymbol())
            .orElseThrow(
                () -> new EntityNotFoundException("Криптовалюта з таким символом не знайдена."));

        Transaction transaction = new Transaction(
            transactionAddDto.getId(),
            cryptocurrency,
            transactionAddDto.getTransactionType(),
            transactionAddDto.getAmount(),
            transactionAddDto.getCosts(),
            null,
            transactionAddDto.getFees(),
            transactionAddDto.getDescription(),
            LocalDateTime.now()
        );

        return transactionRepository.add(transaction);
    }

    @Override
    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Транзакція з таким ID не знайдена."));
    }

    @Override
    public List<Transaction> getTransactionsByPortfolioId(UUID portfolioId) {
        portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new EntityNotFoundException("Портфоліо з таким ID не знайдено."));

        return transactionRepository.findAll().stream()
            .filter(transaction -> portfolioId.equals(transaction.getPortfolioId()))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteTransaction(UUID id) {
        Transaction transaction = getTransactionById(id);
        transactionRepository.remove(transaction);
    }

    @Override
    public void calculatePnL(UUID transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        Cryptocurrency cryptocurrency = transaction.getCryptocurrency();

        BigDecimal currentPrice = BigDecimal.valueOf(cryptocurrency.getCurrentPrice());
        BigDecimal pnl = transaction.getTransactionType() == TransactionType.BUY
            ? currentPrice.multiply(transaction.getAmount()).subtract(transaction.getCosts())
            : transaction.getCosts().subtract(currentPrice.multiply(transaction.getAmount()));

        transaction.setProfit(pnl);
        transactionRepository.add(transaction);
    }

    @Override
    public void generateReport(Predicate<Transaction> filter) {
        // Генеруємо Excel-звіт про транзакції
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        // Додаємо заголовки
        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"№", "ID", "Криптовалюта", "Тип транзакції", "Кількість", "Вартість",
            "Комісія", "Опис"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Додаємо дані
        for (Transaction transaction : getAll(filter)) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1); // Номер
            row.createCell(1).setCellValue(transaction.getId().toString()); // ID
            row.createCell(2)
                .setCellValue(transaction.getCryptocurrency().getSymbol()); // Криптовалюта
            row.createCell(3)
                .setCellValue(transaction.getTransactionType().toString()); // Тип транзакції
            row.createCell(4).setCellValue(transaction.getAmount().toString()); // Кількість
            row.createCell(5).setCellValue(transaction.getCosts().toString()); // Вартість
            row.createCell(6).setCellValue(transaction.getFees().toString()); // Комісія
            row.createCell(7).setCellValue(transaction.getDescription()); // Опис
        }

        // Автоматичне налаштування ширини колонок
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Збереження звіту
        String fileName = "transactions_report_" + System.currentTimeMillis() + ".xls";
        Path outputPath = Path.of("reports", fileName);
        try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
            workbook.write(outputStream);
            workbook.close();
            System.out.println("Звіт про транзакції успішно збережено: " + outputPath);
        } catch (IOException e) {
            throw new RuntimeException("Помилка при створенні звіту: " + e.getMessage(), e);
        }
    }

}
