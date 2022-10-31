package com.mo16.eventstoreplayground;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventStoreDBClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

@SpringBootTest
public class EventStoreTest {

    @Autowired
    private EventStoreDBClient store;

    @Test
    @Disabled
    public void testSend10000EventToStream() throws ExecutionException, InterruptedException {
        record TestEvent(UUID id, int index){}

        var streamName = "testSend10000EventToStream";
        List<EventData> events = IntStream.range(0, 10000)
                .mapToObj(i -> {
                            var event = new TestEvent(UUID.randomUUID(), i);
                            return EventData.builderAsJson("test-event", event)
                                    .eventId(event.id())
                                    .build();
                        }
                ).toList();

        long start = System.currentTimeMillis();

        store.appendToStream(streamName, events.toArray(new EventData[0])).get();

        System.out.println("time taken to publish 10000 event %sms"
                .formatted(System.currentTimeMillis() - start));
    }

    @Test
    @Disabled
    public void testCreateAndSendEventsTo1000Stream() {
        record TestEvent(UUID id, int index) {
        }

        long start = System.currentTimeMillis();
        IntStream.range(0, 1000)
                .forEach(i -> {
                            var event = new TestEvent(UUID.randomUUID(), i);
                            var streamName = "testCreateAndSendEventsTo1000Stream-" + event.id();
                            EventData eventData = EventData.builderAsJson("test-event", event)
                                    .eventId(event.id())
                                    .build();
                            try {
                                store.appendToStream(streamName, eventData);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

        System.out.println("time taken to publish 1000 event %sms"
                .formatted(System.currentTimeMillis() - start));
    }
}
