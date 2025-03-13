package ru.yandex.practicum.filmorate.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int durationInMinutes = p.getIntValue();

        if (durationInMinutes < 0) {
            log.warn("Получено некорректное значение duration: {}", durationInMinutes);
            return null;
        }
        return Duration.ofMinutes(durationInMinutes);
    }
}