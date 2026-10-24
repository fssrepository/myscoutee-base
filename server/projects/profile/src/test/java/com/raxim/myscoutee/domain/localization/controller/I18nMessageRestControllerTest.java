package com.raxim.myscoutee.domain.localization.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.raxim.myscoutee.domain.localization.document.I18nBundle;
import com.raxim.myscoutee.domain.localization.document.I18nMessage;
import com.raxim.myscoutee.domain.localization.repository.I18nBundleRepository;
import com.raxim.myscoutee.domain.localization.repository.I18nMessageRepository;
import com.raxim.myscoutee.domain.localization.service.I18nService;

@Tag("standard")
class I18nMessageRestControllerTest {
    @Test
    void anExactVersionMatchKeepsTheExistingHttpContractWithoutRepeatingMessages() throws Exception {
        var bundles = mock(I18nBundleRepository.class);
        var messages = mock(I18nMessageRepository.class);
        var bundle = new I18nBundle();
        bundle.setLang("en");
        bundle.setVersion("2026.10.21.4");
        var message = new I18nMessage();
        message.setLang("en");
        message.setKey("greeting");
        message.setValue("Hello ".repeat(100));
        when(bundles.findAll()).thenReturn(List.of(bundle));
        when(messages.findAll()).thenReturn(List.of(message));
        var mvc = MockMvcBuilders.standaloneSetup(new I18nMessageRestController(
                new I18nService(messages, bundles))).build();

        var initial = mvc.perform(get("/i18n/bundle").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.greeting").value(message.getValue()))
                .andReturn().getResponse();
        var unchanged = mvc.perform(get("/i18n/bundle").param("lang", "en").param("version", "2026.10.21.4"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.lang").value("en"))
                .andExpect(jsonPath("$.version").value("2026.10.21.4"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse();
        assertThat(unchanged.getContentAsByteArray().length).isLessThan(100);
        assertThat(initial.getContentAsByteArray().length).isGreaterThan(600);

        mvc.perform(get("/i18n/bundle").param("lang", "en").param("version", "2026.10.20.4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.greeting").value(message.getValue()));
    }
}
