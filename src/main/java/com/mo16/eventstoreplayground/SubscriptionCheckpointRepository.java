package com.mo16.eventstoreplayground;

import org.springframework.data.repository.CrudRepository;

public interface SubscriptionCheckpointRepository extends CrudRepository<SubscriptionCheckpoint, String> {
}
