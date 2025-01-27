package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.PortfolioService;
import com.crypto.blockfolio.domain.dto.PortfolioAddDto;
import com.crypto.blockfolio.domain.exception.EntityNotFoundException;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PortfolioServiceImpl extends GenericService<Portfolio, UUID> implements
    PortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceImpl.class);
    private final PortfolioRepository portfolioRepository;
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final TransactionRepository transactionRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
        CryptocurrencyRepository cryptocurrencyRepository,
        TransactionRepository transactionRepository) {
        super(portfolioRepository);
        this.portfolioRepository = portfolioRepository;
        this.transactionRepository = transactionRepository;
        this.cryptocurrencyRepository = cryptocurrencyRepository;
    }

    @Override
    public Portfolio addPortfolio(PortfolioAddDto portfolioAddDto) {
        Portfolio portfolio = new Portfolio(
            portfolioAddDto.getId(),
            portfolioAddDto.getOwnerId(),
            portfolioAddDto.getName()
        );
        portfolioRepository.add(portfolio);
        return portfolio;
    }

    @Override
    public boolean removeTransactionFromPortfolio(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        boolean removed = portfolio.removeTransaction(transactionId, transactionRepository,
            cryptocurrencyRepository);

        if (removed) {
            // Зберігаємо оновлений портфель у репозиторії
            portfolioRepository.update(portfolio);
            return true;
        }

        return false; // Транзакція не знайдена або не видалена
    }

    public Portfolio getPortfolioById(UUID id) {
        return portfolioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Портфель із таким ID не знайдено."));
    }


    @Override
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll().stream().toList();
    }

    @Override
    public void deletePortfolio(UUID id) {
        Portfolio portfolio = getPortfolioById(id);
        portfolioRepository.remove(portfolio);
    }

    @Override
    public void addCryptocurrencyToPortfolio(UUID portfolioId, Cryptocurrency cryptocurrency) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        portfolio.addCryptocurrency(cryptocurrency);
        portfolioRepository.add(portfolio);
    }

    @Override
    public void removeCryptocurrencyFromPortfolio(UUID portfolioId, Cryptocurrency cryptocurrency) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        portfolio.removeCryptocurrency(cryptocurrency);
        portfolioRepository.add(portfolio);
    }
    
    @Override
    public void generateReport(Predicate<Portfolio> filter) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Portfolios");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"№", "Назва", "Загальна вартість", "ID Власника"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        for (Portfolio portfolio : getAll(filter)) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum);
            row.createCell(1).setCellValue(portfolio.getName());
            row.createCell(2).setCellValue(portfolio.getTotalValue().toString());
            row.createCell(3).setCellValue(portfolio.getOwnerId().toString());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = "portfolios[%s].xls".formatted(LocalDateTime.now().toString())
            .replace(':', '-');

        Path outputPath = Path.of(REPORTS_DIRECTORY, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(
                "Помилка при збереженні звіту портфелів: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public void calculateTotalValue(Portfolio portfolio) {
        portfolio.calculateTotalValue(cryptocurrencyRepository);
    }

}
