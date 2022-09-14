package com.jos.dem.vetlog.service;

import com.jos.dem.vetlog.helper.LocaleResolver;
import com.jos.dem.vetlog.service.impl.LocaleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class LocaleServiceTest {

    private LocaleService service;

    @Mock private MessageSource messageSource;
    @Mock private LocaleResolver localeResolver;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        service = new LocaleServiceImpl(messageSource, localeResolver);
    }

    @Test
    @DisplayName("getting message by request")
    void shouldGetMessageByRequest(TestInfo testInfo){
        log.info("Running: {}", testInfo.getDisplayName());
        String code = "code";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(messageSource.getMessage(code, null, localeResolver.resolveLocale(request))).thenReturn("message");
        assertEquals("message", service.getMessage(code, request));
    }
}