package com.raxim.myscoutee.domain.localization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import com.raxim.myscoutee.domain.localization.repository.I18nMessageRepository;
import com.raxim.myscoutee.domain.localization.document.I18nMessage;
import com.raxim.myscoutee.common.config.mongo.MongoSessionModeContext;
import com.raxim.myscoutee.common.config.mongo.MongoSessionMode;

@org.junit.jupiter.api.Tag("standard")
class DBMessageSourceTest {
    @Test void rawUiPlaceholdersDoNotBreakServerMessagesAndCachesAreDatabaseScoped() {
        var repository = mock(I18nMessageRepository.class);
        when(repository.findByLang("en")).thenAnswer(call -> List.of(
                message("server.key", MongoSessionModeContext.currentMode().name()),
                message("ui.key", "Hello {name}")));
        var source = new DBMessageSource(repository);
        try {
            MongoSessionModeContext.set(MongoSessionMode.REAL);
            assertThat(source.getMessage("server.key", null, Locale.ENGLISH)).isEqualTo("REAL");
            MongoSessionModeContext.set(MongoSessionMode.DEMO);
            assertThat(source.getMessage("server.key", null, Locale.ENGLISH)).isEqualTo("DEMO");
            assertThat(source.getMessage("ui.key", null, Locale.ENGLISH)).isEqualTo("Hello {name}");
            verify(repository, times(2)).findByLang("en");
        } finally { MongoSessionModeContext.clear(); }
    }
    @Test void invalidationCannotBeUndoneByAnOlderLoad() throws Exception {
        var repository = mock(I18nMessageRepository.class);
        var entered = new java.util.concurrent.CountDownLatch(1);
        var release = new java.util.concurrent.CountDownLatch(1);
        when(repository.findByLang("en")).thenAnswer(call -> {
            entered.countDown();
            assertThat(release.await(2, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
            return List.of(message("key", "old"));
        }).thenReturn(List.of(message("key", "new")));
        var source = new DBMessageSource(repository);
        var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        try {
            var old = executor.submit(() -> source.getMessage("key", null, Locale.ENGLISH));
            assertThat(entered.await(2, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
            source.refreshCache();
            release.countDown();
            assertThat(old.get(2, java.util.concurrent.TimeUnit.SECONDS)).isEqualTo("old");
            assertThat(source.getMessage("key", null, Locale.ENGLISH)).isEqualTo("new");
            when(repository.findByLang("en")).thenReturn(List.of());
            source.refreshCache();
            assertThat(source.getMessage("key", null, "fallback", Locale.ENGLISH)).isEqualTo("fallback");
        } finally { release.countDown(); executor.shutdownNow(); }
    }

    private I18nMessage message(String key, String value) {
        var message = new I18nMessage(); message.setKey(key); message.setLang("en"); message.setValue(value); return message;
    }
}
