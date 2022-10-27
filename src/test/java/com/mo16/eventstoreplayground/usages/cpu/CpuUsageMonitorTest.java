package com.mo16.eventstoreplayground.usages.cpu;

import com.eventstore.dbclient.*;
import com.mo16.eventstoreplayground.EventSerializer;
import com.mo16.eventstoreplayground.SubscriptionCheckpoint;
import com.mo16.eventstoreplayground.SubscriptionCheckpointRepository;
import com.mo16.eventstoreplayground.SubscriptionListenerBuilder;
import com.mo16.eventstoreplayground.usages.cpu.events.V1CpuReadEvent;
import com.mo16.eventstoreplayground.usages.cpu.events.V2CpuReadEvent;
import io.vavr.control.Try;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

@SpringBootTest
public class CpuUsageMonitorTest {

    @Autowired
    private EventStoreDBClient store;

    @Value("${application.subscriptions.cpu-usage.id}")
    private String cpuUsageSubscriptionId;

    @Autowired
    private SubscriptionCheckpointRepository checkPointRepo;

    @Value("${application.subscriptions.cpu-usage.streamId}")
    private String cpuUsageStreamId;

    @Test
    @Disabled
    public void cpuUsageProducer() throws InterruptedException {
        CentralProcessor processor = new SystemInfo().getHardware().getProcessor();
        emmit100V1CpuReadEvents(processor);

        Runnable runnable = () -> {
            while (true) {
                double usage = processor.getSystemCpuLoad(1000L);
                var event = new V2CpuReadEvent(UUID.randomUUID(), usage, LocalDateTime.now());
                EventData eventData = EventSerializer.serialize(event).get();
                try {
                    store.appendToStream(cpuUsageStreamId, eventData).get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
    }

    private void emmit100V1CpuReadEvents(CentralProcessor processor) {
        IntStream.range(0, 100) // publish 100 V1CpuReadEvent every 200ms
                .mapToObj(value -> new V1CpuReadEvent(UUID.randomUUID(),
                        processor.getSystemCpuLoad(200), LocalDateTime.now().toString()))
                .map(EventSerializer::serialize)
                .map(Try::get)
                .forEach(eventData -> store.appendToStream(cpuUsageStreamId, eventData));
    }

    @Test
    @Disabled
    public void printCpuUsageReads() throws InterruptedException {

        var subscriptionListener = SubscriptionListenerBuilder.get()
                .onEvent("cpuUsageRead.1", cpuReadEventV1Handler)
                .onEvent("cpuUsageRead.2", cpuReadEventV2Handler)
                .onError((sub, throwable) -> throwable.printStackTrace())
                .onCancelled(sub -> System.out.printf("subscription %s cancelled\n", sub.getSubscriptionId()))
                .getSubscriptionListener();

        SubscriptionCheckpoint checkpoint = resolveCheckpoint(cpuUsageSubscriptionId);
        SubscribeToStreamOptions options = SubscribeToStreamOptions.get()
                .fromRevision(checkpoint.getRevision());

        store.subscribeToStream(cpuUsageStreamId, subscriptionListener, options);

        keepTestRunning();
    }

    private static void keepTestRunning() throws InterruptedException {
        // a thread that just exists to prevent the program from exiting
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        thread.join();
    }

    private SubscriptionCheckpoint resolveCheckpoint(String subscriptionId) {
        return checkPointRepo.findById(subscriptionId)
                .orElseGet(() -> {
                    SubscriptionCheckpoint cp = SubscriptionCheckpoint.get()
                            .id(subscriptionId)
                            .streamId(cpuUsageStreamId)
                            .revision(0);
                    return checkPointRepo.save(cp);
                });
    }


    private final BiConsumer<Subscription, ResolvedEvent> cpuReadEventV1Handler = (sub, event) ->
            Try.run(() -> {
                SubscriptionCheckpoint checkpoint = checkPointRepo.findById(cpuUsageSubscriptionId)
                        .orElseThrow(() -> new RuntimeException("subscription checkpoint wasn't found"));

                long revision = event.getOriginalEvent().getStreamRevision().getValueUnsigned();
                if (revision <= checkpoint.getRevision()) {
                    return;
                }

                V1CpuReadEvent cpuUsage = EventSerializer.deserialize(V1CpuReadEvent.class, event).get();
                System.out.println("cpu usage = " + cpuUsage.usage() + " revision " + revision + " " + cpuUsage);

                checkpoint.revision(revision);
                checkPointRepo.save(checkpoint);
            }).get();

    private final BiConsumer<Subscription, ResolvedEvent> cpuReadEventV2Handler = (sub, event) ->
            Try.run(() -> {
                SubscriptionCheckpoint checkpoint = checkPointRepo.findById(cpuUsageSubscriptionId)
                        .orElseThrow(() -> new RuntimeException("subscription checkpoint wasn't found"));

                long revision = event.getOriginalEvent().getStreamRevision().getValueUnsigned();
                if (revision <= checkpoint.getRevision())
                    return;

                V2CpuReadEvent cpuUsage = EventSerializer.deserialize(V2CpuReadEvent.class, event).get();
                System.out.println("cpu usage = " + cpuUsage.usage() + " revision " + revision + " " + cpuUsage);

                checkpoint.revision(revision);
                checkPointRepo.save(checkpoint);
            }).get();
}
