package com.mo16.eventstoreplayground;


import java.util.UUID;

public interface Event {

    UUID id();

    String eventType();
}
