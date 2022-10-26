package com.mo16.eventstoreplayground.usages.cpu.events;

import com.mo16.eventstoreplayground.Event;
import com.mo16.eventstoreplayground.Utils.ToStringBuilder;

import java.util.UUID;

public record V1CpuReadEvent(UUID id, double usage, String readAt) implements Event {

    @Override
    public String eventType() {
        return "cpuUsageRead.1";
    }

    @Override
    public String toString() {
        return ToStringBuilder.get()
                .withClassName("V1CpuReadEvent")
                .withProperty("id", id().toString())
                .withProperty("eventType", eventType())
                .withProperty("usage", String.valueOf(usage))
                .withProperty("readAt", readAt)
                .string();
    }

}