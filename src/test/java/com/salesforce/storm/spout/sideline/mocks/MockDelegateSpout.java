/**
 * Copyright (c) 2017, Salesforce.com, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 *   disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.storm.spout.sideline.mocks;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.salesforce.storm.spout.sideline.DefaultVirtualSpoutIdentifier;
import com.salesforce.storm.spout.sideline.Message;
import com.salesforce.storm.spout.sideline.MessageId;
import com.salesforce.storm.spout.sideline.VirtualSpoutIdentifier;
import com.salesforce.storm.spout.sideline.consumer.Consumer;
import com.salesforce.storm.spout.sideline.consumer.ConsumerState;
import com.salesforce.storm.spout.sideline.DelegateSpout;

import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * A test mock.
 */
public class MockDelegateSpout implements DelegateSpout {
    private final VirtualSpoutIdentifier virtualSpoutId;
    public volatile boolean requestedStop = false;
    public volatile boolean wasOpenCalled = false;
    public volatile boolean wasCloseCalled = false;
    public volatile boolean flushStateCalled = false;
    public volatile RuntimeException exceptionToThrow = null;
    public volatile Set<MessageId> failedTupleIds = Sets.newConcurrentHashSet();
    public volatile Set<MessageId> ackedTupleIds = Sets.newConcurrentHashSet();

    public volatile Queue<Message> emitQueue = Queues.newConcurrentLinkedQueue();

    public MockDelegateSpout() {
        this.virtualSpoutId = new DefaultVirtualSpoutIdentifier(this.getClass().getSimpleName() + UUID.randomUUID().toString());
    }

    public MockDelegateSpout(final VirtualSpoutIdentifier virtualSpoutId) {
        this.virtualSpoutId = virtualSpoutId;
    }

    @Override
    public void open() {
        wasOpenCalled = true;
    }

    @Override
    public void close() {
        wasCloseCalled = true;
    }

    @Override
    public Message nextTuple() {
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
        return emitQueue.poll();
    }

    @Override
    public void ack(Object id) {
        ackedTupleIds.add((MessageId) id);
    }

    @Override
    public void fail(Object id) {
        failedTupleIds.add((MessageId) id);
    }

    @Override
    public VirtualSpoutIdentifier getVirtualSpoutId() {
        return virtualSpoutId;
    }

    @Override
    public void flushState() {
        flushStateCalled = true;
    }

    @Override
    public synchronized void requestStop() {
        requestedStop = true;
    }

    @Override
    public synchronized boolean isStopRequested() {
        return requestedStop;
    }

    @Override
    public ConsumerState getCurrentState() {
        return ConsumerState.builder().build();
    }

    @Override
    public ConsumerState getStartingState() {
        return ConsumerState.builder().build();
    }

    @Override
    public ConsumerState getEndingState() {
        return ConsumerState.builder().build();
    }

    @Override
    public double getMaxLag() {
        return 0;
    }

    @Override
    public int getNumberOfFiltersApplied() {
        return 0;
    }

    @Override
    public Consumer getConsumer() {
        // TODO: Implement this when it is needed
        return null;
    }
}
