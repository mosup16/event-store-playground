package com.mo16.eventstoreplayground;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBConnectionString;
import com.eventstore.dbclient.ParseError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventStorePlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventStorePlaygroundApplication.class, args);
    }

    @Bean
    EventStoreDBClient eventStoreClient(@Value("${application.event-store.server.connection}")
                                        String serverConn) throws ParseError {
        var settings = EventStoreDBConnectionString.parse(serverConn);
        return EventStoreDBClient.create(settings);
    }

}
