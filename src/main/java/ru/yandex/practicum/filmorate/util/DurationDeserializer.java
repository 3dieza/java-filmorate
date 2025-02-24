package ru.yandex.practicum.filmorate.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int durationInMinutes = p.getIntValue();

        if (durationInMinutes < 0) {
            throw new IllegalArgumentException("!!!Duration must be positive or zero.!!!");
        }
        return Duration.ofMinutes(durationInMinutes);
    }
}