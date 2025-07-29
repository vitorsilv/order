package com.itau.desafio.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Test
    void shouldReturnValidationErrorWithDetails() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "campo", "mensagem de erro");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/api/teste");

        GlobalExceptionHandler handler = new GlobalExceptionHandler();


        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex, request);

        assertEquals(400, response.getStatusCodeValue());
        GlobalExceptionHandler.ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Erro de validação nos campos", body.message());
        assertEquals("/api/teste", body.path());
        assertTrue(body.details().containsKey("campo"));
        assertEquals("mensagem de erro", body.details().get("campo"));
        assertNotNull(body.timestamp());
    }
}