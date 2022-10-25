package com.mo16.eventstoreplayground;

import com.eventstore.dbclient.ResolvedEvent;
import com.eventstore.dbclient.Subscription;
import com.eventstore.dbclient.SubscriptionListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SubscriptionListenerBuilder {
    private BiConsumer<Subscription, ResolvedEvent> eventHandler = (sub, event) -> {
    };
    private BiConsumer<Subscription, Throwable> errorHandler = (sub, throwable) -> {
    };
    private Consumer<Subscription> cancellationHandler = subscription -> {
    };

    public static SubscriptionListenerBuilder get() {
        return new SubscriptionListenerBuilder();
    }

    public SubscriptionListenerBuilder onEvent(BiConsumer<Subscription, ResolvedEvent> handler) {
        this.eventHandler = handler;
        return this;
    }

    public SubscriptionListenerBuilder onError(BiConsumer<Subscription, Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public SubscriptionListenerBuilder onCancelled(Consumer<Subscription> cancellationHandler) {
        this.cancellationHandler = cancellationHandler;
        return this;
    }

    public SubscriptionListener getSubscriptionListener() {
        return new SubscriptionListener() {
            @Override
            public void onEvent(Subscription subscription, ResolvedEvent event) {
                eventHandler.accept(subscription, event);
            }

            @Override
            public void onError(Subscription subscription, Throwable throwable) {
                errorHandler.accept(subscription, throwable);
            }

            @Override
            public void onCancelled(Subscription subscription) {
                cancellationHandler.accept(subscription);
            }
        };
    }
}
