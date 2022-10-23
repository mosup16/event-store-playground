package com.mo16.eventstoreplayground;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventStoreDBClient;
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
}
