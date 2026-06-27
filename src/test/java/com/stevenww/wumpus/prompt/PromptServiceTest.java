package com.stevenww.wumpus.prompt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromptServiceTest {
    @Mock
    private LlmGateway llmGateway;

    @InjectMocks
    private PromptService promptService;

    @Test
    void shouldDelegateToGateway() {
        when(llmGateway.generatePrompt("hello")).thenReturn("hello");

        String result = promptService.createPrompt("hello");

        assertEquals("hello", result);
        verify(llmGateway).generatePrompt("hello");
    }

    @Test
    void shouldConvertNullContextToEmptyString() {
        when(llmGateway.generatePrompt("")).thenReturn("");

        String result = promptService.createPrompt(null);

        assertEquals("", result);
        verify(llmGateway).generatePrompt("");
    }
}

