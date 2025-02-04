package com.blockfolio.crypto.domain.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class LocalDateAdapter implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        long timestampMillis = json.getAsLong();
        return Instant.ofEpochMilli(timestampMillis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDate();
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type typeOfSrc, JsonSerializationContext context) {
        long epochMillis = localDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
        return new JsonPrimitive(epochMillis);
    }
}


