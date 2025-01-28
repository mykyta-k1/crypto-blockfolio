package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.dto.CryptocurrencyAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

class CryptocurrencyServiceImpl extends GenericService<Cryptocurrency, String> implements
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
    public Cryptocurrency getCryptocurrencyInfo(String symbol) {
        return null;
    }

    @Override
    public List<Cryptocurrency> getAllCryptocurrencies() {
        try {
            // Fetch cryptocurrencies from the API
            List<Cryptocurrency> cryptocurrencies = apiService.getAllCryptocurrencies();

            // Save or update each cryptocurrency in the repository
            cryptocurrencies.forEach(crypto -> {
                try {
                    cryptocurrencyRepository.updateCryptocurrency(crypto);
                } catch (IllegalArgumentException e) {
                    System.err.printf("Помилка оновлення криптовалюти %s: %s%n", crypto.getSymbol(),
                        e.getMessage());
                }
            });

            // Return the updated list of cryptocurrencies
            return List.copyOf(cryptocurrencyRepository.findAll());
        } catch (Exception e) {
            System.err.printf("Помилка отримання криптовалют з API: %s%n", e.getMessage());

            // If API fails, fall back to data from the repository
            return List.copyOf(cryptocurrencyRepository.findAll());
        }
    }

    @Override
    public void addCryptocurrency(CryptocurrencyAddDto cryptocurrencyDto) {
        // Створюємо об'єкт Cryptocurrency на основі DTO
        Cryptocurrency cryptocurrency = new Cryptocurrency(
            cryptocurrencyDto.getSymbol(),
            cryptocurrencyDto.getName(),
            cryptocurrencyDto.getCurrentPrice(),
            cryptocurrencyDto.getMarketCap(),
            cryptocurrencyDto.getVolume24h(),
            cryptocurrencyDto.getPercentChange24h(),
            cryptocurrencyDto.getLastUpdated()
        );

        // Використовуємо репозиторій для збереження даних
        cryptocurrencyRepository.add(cryptocurrency);
    }


    @Override
    public void generateReport(Path savePath, Predicate<Cryptocurrency> predicate) {
        String fileName = "cryptocurrencies_report.xls";
        Path outputPath = savePath.resolve(fileName);

        try (Workbook workbook = new HSSFWorkbook();
            FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {

            Sheet sheet = workbook.createSheet("Cryptocurrencies");

            // Додавання заголовків
            String[] headers = {"№", "Назва", "Символ", "Ціна (USD)", "Ринкова капіталізація (USD)",
                "Обсяг за 24г (USD)", "Зміна за 24г (%)"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Додавання даних
            int rowNum = 1;
            for (Cryptocurrency crypto : cryptocurrencyRepository.findAll(predicate)) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(crypto.getName());
                row.createCell(2).setCellValue(crypto.getSymbol());
                row.createCell(3).setCellValue(crypto.getCurrentPrice());
                row.createCell(4).setCellValue(crypto.getMarketCap());
                row.createCell(5).setCellValue(crypto.getVolume24h());
                row.createCell(6).setCellValue(crypto.getPercentChange24h());
            }

            // Автоматичне налаштування ширини колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            System.out.println("Звіт успішно збережено за шляхом: " + outputPath);

        } catch (IOException e) {
            throw new RuntimeException("Помилка при створенні звіту: " + e.getMessage());
        }
    }


}
