package com.salesforce.storm.spout.sideline.persistence;

import com.salesforce.storm.spout.sideline.kafka.consumerState.ConsumerState;
import com.salesforce.storm.spout.sideline.metrics.LogRecorder;
import com.salesforce.storm.spout.sideline.metrics.MetricsRecorder;
import com.salesforce.storm.spout.sideline.trigger.SidelineIdentifier;

import java.io.Serializable;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/**
 * In memory persistence layer implementation. useful for tests.
 * NOT for production use as all state will be lost between JVM restarts.
 */
public class InMemoryPersistenceManager implements PersistenceManager, Serializable {
    // "Persists" consumer state in memory.
    private Map<String,ConsumerState> storedConsumerState;

    // "Persists" side line request states in memory.
    private Map<SidelineIdentifier, ConsumerState> storedSidelineRequests;

    @Override
    public void open(Map topologyConfig) {
        // Allow non-destructive re-initin
        if (storedConsumerState == null) {
            storedConsumerState = new HashMap<>();
        }
        if (storedSidelineRequests == null) {
            storedSidelineRequests = new HashMap<>();
        }
    }

    @Override
    public void close() {
        // Cleanup
        storedConsumerState.clear();
        storedSidelineRequests.clear();
    }

    /**
     * Pass in the consumer state that you'd like persisted.
     * @param consumerState - ConsumerState to be persisted.
     */
    @Override
    public void persistConsumerState(String consumerId, ConsumerState consumerState) {
        storedConsumerState.put(consumerId, consumerState);
    }

    /**
     * Retrieves the consumer state from the persistence layer.
     * @return ConsumerState
     */
    @Override
    public ConsumerState retrieveConsumerState(String consumerId) {
        return storedConsumerState.get(consumerId);
    }

    /**
     * @param id - unique identifier for the sideline request.
     * @param state - the associated state to be stored w/ the request.
     */
    @Override
    public void persistSidelineRequestState(SidelineIdentifier id, ConsumerState state) {
        storedSidelineRequests.put(id, state);
    }

    /**
     * Retrieves a sideline request state for the given SidelineIdentifier.
     * @param id - SidelineIdentifier you want to retrieve the state for.
     * @return The ConsumerState that was persisted via persistSidelineRequestState().
     */
    @Override
    public ConsumerState retrieveSidelineRequestState(SidelineIdentifier id) {
        return storedSidelineRequests.get(id);
    }
}
