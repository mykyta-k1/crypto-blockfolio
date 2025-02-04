package com.blockfolio.crypto.domain.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtils {

    public static LocalDateTime convertTimestampToUtc(long timestampMillis) {
        return Instant.ofEpochMilli(timestampMillis)
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime();
    }

    public static LocalDateTime convertTimeIso8601ToUtc(String isoDate) {
        return Instant.parse(isoDate)
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTimeFromIso(String isoData) {
        return convertTimeIso8601ToUtc(isoData);
    }

    public static LocalDate getDateFromTimestamp(long timestampMillis) {
        return convertTimestampToUtc(timestampMillis).toLocalDate();
    }
}
