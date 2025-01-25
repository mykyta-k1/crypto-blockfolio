package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.dto.CryptocurrencyAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
    public Cryptocurrency addCryptocurrency(CryptocurrencyAddDto cryptocurrencyAddDto) {
        Cryptocurrency cryptocurrency = new Cryptocurrency(
            cryptocurrencyAddDto.getSymbol(),
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

        cryptocurrencyRepository.updateCryptocurrency(existingCryptocurrency);

        System.out.printf(
            "Дані для монети %s оновлено: ціна %.2f, ринкова капіталізація %.2f%n",
            name,
            updatedCryptocurrency.getCurrentPrice(),
            updatedCryptocurrency.getMarketCap()
        );
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
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(cryptocurrency.getName());
            row.createCell(2).setCellValue(cryptocurrency.getSymbol());
            row.createCell(3).setCellValue(cryptocurrency.getCurrentPrice());
            row.createCell(4).setCellValue(cryptocurrency.getMarketCap());
            row.createCell(5).setCellValue(cryptocurrency.getVolume24h());
            row.createCell(6).setCellValue(cryptocurrency.getPercentChange24h());
            row.createCell(7).setCellValue(cryptocurrency.getLastUpdated().toString());
        }

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
            throw new RuntimeException(
                "Помилка при збереженні звіту: %s".formatted(e.getMessage()));
        }
    }
}
