package com.blockfolio.crypto.domain.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String dateTimeString = json.getAsString();

        // Якщо є "Z" у кінці → це UTC
        if (dateTimeString.endsWith("Z")) {
            return Instant.parse(dateTimeString)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
        }

        // Інакше парсимо як `LocalDateTime`
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(FORMATTER.format(localDateTime));
    }
}
