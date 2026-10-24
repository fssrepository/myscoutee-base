package com.raxim.myscoutee.common.config.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.MongoNamespace;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.model.changestream.UpdateDescription;

@Tag("standard")
class MongoChangeStreamServiceTest {
    @Test
    void presenceUsesTheRealCursorWhenDemoAliasesTheSameDatabase() {
        var service = new MongoChangeStreamService(mock(MongoTemplate.class), new ScheduledMongoSessionRunner("same", "same"));
        var present = service.watchPresence("presence", Set.of("items"), () -> true);
        ready(service);
        try {
            service.reconcile(MongoSessionMode.REAL);
            MongoSessionModeContext.set(MongoSessionMode.DEMO);
            org.awaitility.Awaitility.await().pollInSameThread().atMost(java.time.Duration.ofSeconds(1))
                    .until(present::getAsBoolean);
            service.stop();
            assertThat(present.getAsBoolean()).isFalse();
        } finally { MongoSessionModeContext.clear(); service.stop(); }
    }
    @Test
    void emptyWorkGateIsModeScopedAndReconcilesChangesWithoutSchedulerQueries() throws Exception {
        var service = service();
        var inputs = new java.util.concurrent.ConcurrentHashMap<MongoSessionMode, Boolean>();
        var queries = new LinkedBlockingQueue<MongoSessionMode>();
        var present = service.watchPresence("presence", Set.of("items", "pending-effects"), () -> {
            var mode = MongoSessionModeContext.currentMode();
            queries.add(mode);
            return inputs.getOrDefault(mode, false);
        });
        ready(service);
        try {
            service.reconcile(MongoSessionMode.REAL);
            service.reconcile(MongoSessionMode.DEMO);
            assertThat(Set.of(queries.poll(1, TimeUnit.SECONDS), queries.poll(1, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(MongoSessionMode.REAL, MongoSessionMode.DEMO);
            assertThat(present.getAsBoolean()).isFalse();
            for (int i = 0; i < 100; i++) present.getAsBoolean();
            assertThat(queries.poll(100, TimeUnit.MILLISECONDS)).isNull();
            inputs.put(MongoSessionMode.DEMO, true);
            service.dispatch(MongoSessionMode.DEMO, event("items", "expiresAtIso"));
            assertThat(queries.poll(1, TimeUnit.SECONDS)).isEqualTo(MongoSessionMode.DEMO);
            // Wait until the query result has been published, not just entered.
            org.awaitility.Awaitility.await().atMost(java.time.Duration.ofSeconds(1)).until(() -> {
                MongoSessionModeContext.set(MongoSessionMode.DEMO);
                try { return present.getAsBoolean(); } finally { MongoSessionModeContext.clear(); }
            });
            assertThat(present.getAsBoolean()).isFalse();
            inputs.put(MongoSessionMode.DEMO, false);
            service.reconcile(MongoSessionMode.DEMO); // reconnect/removal must clear a positive result
            assertThat(queries.poll(1, TimeUnit.SECONDS)).isEqualTo(MongoSessionMode.DEMO);
            MongoSessionModeContext.set(MongoSessionMode.DEMO);
            org.awaitility.Awaitility.await().pollInSameThread().atMost(java.time.Duration.ofSeconds(1))
                    .until(() -> !present.getAsBoolean());
        } finally { MongoSessionModeContext.clear(); service.stop(); }
    }
    private MongoChangeStreamService service() {
        return new MongoChangeStreamService(mock(MongoTemplate.class), new ScheduledMongoSessionRunner("real", "demo"));
    }

    @SuppressWarnings("unchecked")
    private void ready(MongoChangeStreamService service) {
        ReflectionTestUtils.setField(service, "running", true);
        ((Map<MongoSessionMode, Boolean>) ReflectionTestUtils.getField(service, "connected"))
                .putAll(Map.of(MongoSessionMode.REAL, true, MongoSessionMode.DEMO, true));
    }

    @SuppressWarnings("unchecked")
    private ChangeStreamDocument<Document> event(String collection, String field) {
        var event = (ChangeStreamDocument<Document>) mock(ChangeStreamDocument.class);
        when(event.getNamespace()).thenReturn(new MongoNamespace("fixture", collection));
        when(event.getOperationType()).thenReturn(OperationType.UPDATE);
        var update = mock(UpdateDescription.class);
        when(update.getUpdatedFields()).thenReturn(new BsonDocument(field, BsonBoolean.TRUE));
        when(update.getRemovedFields()).thenReturn(java.util.List.of());
        when(event.getUpdateDescription()).thenReturn(update);
        return event;
    }

    @Test
    void invalidatesCachesBeforeDependentWorkRegardlessOfRegistrationOrder() throws Exception {
        var service = service();
        var revision = new AtomicInteger();
        var observed = new LinkedBlockingQueue<Integer>();
        service.watch("dependent-first", Set.of("rules"), Set.of(), () -> { observed.add(revision.get()); return null; });
        service.watch("cache-last", Set.of("rules"), Set.of(), () -> null, () -> {}, () -> revision.incrementAndGet());
        ready(service);
        try {
            service.reconcile(MongoSessionMode.REAL);
            assertThat(observed.poll(1, TimeUnit.SECONDS)).isEqualTo(1);
            service.dispatch(MongoSessionMode.REAL, event("rules", "enabled"));
            assertThat(observed.poll(1, TimeUnit.SECONDS)).isEqualTo(2);
            var renamed = event("staging-rules", "enabled");
            when(renamed.getOperationType()).thenReturn(OperationType.RENAME);
            service.dispatch(MongoSessionMode.REAL, renamed);
            assertThat(observed.poll(1, TimeUnit.SECONDS)).isEqualTo(3);
        } finally { service.stop(); }
    }

    @Test
    void reconcilesEachDatabaseAndSleepsWhenNoWorkRemains() throws Exception {
        var service = service();
        var calls = new LinkedBlockingQueue<MongoSessionMode>();
        service.watch("work", Set.of("tasks"), Set.of("pending"), () -> {
            calls.add(MongoSessionModeContext.currentMode()); return null;
        });
        ready(service);
        try {
            service.reconcile(MongoSessionMode.REAL);
            service.reconcile(MongoSessionMode.DEMO);
            assertThat(Set.of(calls.poll(1, TimeUnit.SECONDS), calls.poll(1, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(MongoSessionMode.REAL, MongoSessionMode.DEMO);
            assertThat(calls.poll(150, TimeUnit.MILLISECONDS)).isNull();
            service.dispatch(MongoSessionMode.DEMO, event("unrelated", "pending"));
            service.dispatch(MongoSessionMode.DEMO, event("tasks", "unrelated"));
            assertThat(calls.poll(100, TimeUnit.MILLISECONDS)).isNull();
            service.dispatch(MongoSessionMode.DEMO, event("tasks", "pending.id"));
            assertThat(calls.poll(1, TimeUnit.SECONDS)).isEqualTo(MongoSessionMode.DEMO);
        } finally { service.stop(); }
    }

    @Test
    void changeDuringProcessingIsNotLostAndRepeatedSignalsCoalesce() throws Exception {
        var service = service();
        var entered = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var completed = new CountDownLatch(2);
        var calls = new AtomicInteger();
        service.watch("work", Set.of("tasks"), Set.of(), () -> {
            int count = calls.incrementAndGet();
            if (count == 1) {
                entered.countDown();
                try { assertThat(release.await(1, TimeUnit.SECONDS)).isTrue(); }
                catch (InterruptedException e) { throw new IllegalStateException(e); }
            }
            completed.countDown(); return null;
        });
        ready(service);
        try {
            service.reconcile(MongoSessionMode.REAL);
            assertThat(entered.await(1, TimeUnit.SECONDS)).isTrue();
            for (int i = 0; i < 20; i++) service.dispatch(MongoSessionMode.REAL, event("tasks", "pending"));
            release.countDown();
            assertThat(completed.await(1, TimeUnit.SECONDS)).isTrue();
            Thread.sleep(100);
            assertThat(calls.get()).isEqualTo(2);
        } finally { release.countDown(); service.stop(); }
    }

    @Test
    void persistedFutureDeadlineWakesWithoutAnotherDatabaseChange() throws Exception {
        var service = service();
        var calls = new AtomicInteger();
        var completed = new CountDownLatch(1);
        service.watch("deadline", Set.of("tasks"), Set.of(), () -> {
            if (calls.incrementAndGet() == 1) return Instant.now().plusMillis(80);
            completed.countDown(); return null;
        });
        ready(service);
        try {
            service.reconcile(MongoSessionMode.REAL);
            assertThat(completed.await(1, TimeUnit.SECONDS)).isTrue();
            Thread.sleep(100);
            assertThat(calls.get()).isEqualTo(2);
        } finally { service.stop(); }
    }

    @Test
    @SuppressWarnings("unchecked")
    void disconnectedDatabaseDoesNotPollAndReconnectRecoversItsWork() throws Exception {
        var service = service();
        var calls = new LinkedBlockingQueue<MongoSessionMode>();
        service.watch("work", Set.of("tasks"), Set.of(), () -> {
            calls.add(MongoSessionModeContext.currentMode()); return null;
        });
        ready(service);
        var connected = (Map<MongoSessionMode, Boolean>) ReflectionTestUtils.getField(service, "connected");
        try {
            connected.put(MongoSessionMode.DEMO, false);
            service.reconcile(MongoSessionMode.DEMO);
            assertThat(calls.poll(100, TimeUnit.MILLISECONDS)).isNull();
            connected.put(MongoSessionMode.DEMO, true);
            service.reconcile(MongoSessionMode.DEMO);
            assertThat(calls.poll(1, TimeUnit.SECONDS)).isEqualTo(MongoSessionMode.DEMO);
        } finally { service.stop(); }
    }
}
