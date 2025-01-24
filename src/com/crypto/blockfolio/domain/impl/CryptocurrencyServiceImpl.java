package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.dto.CryptocurrencyAddDto;
import com.crypto.blockfolio.domain.exception.SignUpException;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CryptocurrencyServiceImpl extends GenericService<Cryptocurrency, String> implements
    CryptocurrencyService {

    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final CoinGeckoApiService apiService;

    public CryptocurrencyServiceImpl(CryptocurrencyRepository cryptocurrencyRepository,
        CoinGeckoApiService apiService) {
        super(cryptocurrencyRepository);
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.apiService = apiService;
    }

    @Override
    public Cryptocurrency addCryptocurrency(CryptocurrencyAddDto cryptocurrencyAddDto) {
        Cryptocurrency cryptocurrency = new Cryptocurrency(
            cryptocurrencyAddDto.getSymbol(), // Використання символу як ID
            cryptocurrencyAddDto.getName(),
            cryptocurrencyAddDto.getCurrentPrice(),
            cryptocurrencyAddDto.getMarketCap(),
            cryptocurrencyAddDto.getVolume24h(),
            cryptocurrencyAddDto.getPercentChange24h(),
            cryptocurrencyAddDto.getLastUpdated()
        );
        return cryptocurrencyRepository.add(cryptocurrency);
    }

    @Override
    public void syncCryptocurrencyPrice(String name) {
        Cryptocurrency updatedCryptocurrency = apiService.getCryptocurrencyInfo(name);
        Cryptocurrency existingCryptocurrency = cryptocurrencyRepository.findByName(name)
            .orElseThrow(() -> new RuntimeException(
                "Криптовалюта з назвою %s не знайдена".formatted(name)));

        existingCryptocurrency.setCurrentPrice(updatedCryptocurrency.getCurrentPrice());
        existingCryptocurrency.setMarketCap(updatedCryptocurrency.getMarketCap());
        existingCryptocurrency.setVolume24h(updatedCryptocurrency.getVolume24h());
        existingCryptocurrency.setPercentChange24h(updatedCryptocurrency.getPercentChange24h());
        existingCryptocurrency.setLastUpdated(updatedCryptocurrency.getLastUpdated());

        cryptocurrencyRepository.update(existingCryptocurrency);

        System.out.printf(
            "Дані для монети %s оновлено: ціна %.2f, ринкова капіталізація %.2f%n",
            name,
            updatedCryptocurrency.getCurrentPrice(),
            updatedCryptocurrency.getMarketCap()
        );
    }

    // Використання методів із базового інтерфейсу Service

    @Override
    public Cryptocurrency get(String symbol) { // Використовуємо symbol як ідентифікатор
        return cryptocurrencyRepository.findBySymbol(symbol)
            .orElseThrow(
                () -> new RuntimeException(
                    "Криптовалюта з символом %s не знайдена".formatted(symbol))
            );
    }

    @Override
    public Set<Cryptocurrency> getAll() {
        return cryptocurrencyRepository.findAll();
    }

    @Override
    public Set<Cryptocurrency> getAll(Predicate<Cryptocurrency> filter) {
        return cryptocurrencyRepository.findAll().stream()
            .filter(filter)
            .collect(Collectors.toSet());
    }

    @Override
    public Cryptocurrency add(Cryptocurrency entity) {
        return cryptocurrencyRepository.add(entity);
    }

    @Override
    public boolean remove(Cryptocurrency entity) {
        return cryptocurrencyRepository.remove(entity);
    }

    @Override
    public void generateReport(Predicate<Cryptocurrency> filter) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Cryptocurrency");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"№", "Монета", "Символ", "Ціна", "Ринкова капіталізація",
            "Обсяг торгів (24h)", "Зміна (%)", "Останнє оновлення"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        for (Cryptocurrency cryptocurrency : getAll(filter)) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1); // Номер
            row.createCell(1).setCellValue(cryptocurrency.getName()); // Назва монети
            row.createCell(2).setCellValue(cryptocurrency.getSymbol()); // Символ монети
            row.createCell(3).setCellValue(cryptocurrency.getCurrentPrice()); // Ціна
            row.createCell(4).setCellValue(cryptocurrency.getMarketCap()); // Ринкова капіталізація
            row.createCell(5)
                .setCellValue(cryptocurrency.getVolume24h()); // Обсяг торгів за 24 години
            row.createCell(6)
                .setCellValue(cryptocurrency.getPercentChange24h()); // Зміна відсотків за 24 години
            row.createCell(7).setCellValue(
                cryptocurrency.getLastUpdated().toString()); // Дата останнього оновлення
        }

        // Автоматичне встановлення ширини колонок
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        String fileName = "cryptocurrency[%s].xls".formatted(LocalDateTime.now().toString())
            .replace(':', '-');

        Path outputPath = Path.of(REPORTS_DIRECTORY, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new SignUpException("Помилка при збереженні звіту монет: %s"
                .formatted(e.getMessage()));
        }
    }
}
