package com.mo16.eventstoreplayground;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventDataBuilder;
import com.eventstore.dbclient.ResolvedEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.control.Try;

public class EventSerializer {
    private final static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <T extends Event> Try<EventData> serialize(T event) {
        return Try.of(() -> {
            byte[] value = mapper.writeValueAsBytes(event);
            return EventDataBuilder.json(event.id(), event.eventType(), value)
                    .build();
        });
    }

    public static <T> Try<T> deserialize(Class<T> eventClass, ResolvedEvent resolvedEvent) {
        return Try.of(() -> {
            byte[] data = resolvedEvent.getEvent().getEventData();
            return mapper.readValue(data, eventClass);
        });
    }
}
