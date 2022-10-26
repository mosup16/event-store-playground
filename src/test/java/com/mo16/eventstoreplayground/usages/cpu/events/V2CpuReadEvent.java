package com.mo16.eventstoreplayground.usages.cpu.events;

import com.mo16.eventstoreplayground.Event;
import com.mo16.eventstoreplayground.Utils.ToStringBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

public record V2CpuReadEvent(UUID id, double usage, LocalDateTime readAt) implements Event {

    @Override
    public String eventType() {
        return "cpuUsageRead.2";
    }

    @Override
    public String toString() {
        return ToStringBuilder.get()
                .withClassName("V2CpuReadEvent")
                .withProperty("id", id().toString())
                .withProperty("eventType", eventType())
                .withProperty("usage", String.valueOf(usage))
                .withProperty("readAt", String.valueOf(readAt))
                .string();
    }

}