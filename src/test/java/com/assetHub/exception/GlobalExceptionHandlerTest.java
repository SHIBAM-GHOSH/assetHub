package com.assethub.exception;

import com.assethub.dto.error.ApiErrorResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnNotFoundResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/assets/99");

        ApiErrorResponse response = globalExceptionHandler.handleResourceNotFoundException(
                new ResourceNotFoundException("Asset not found"),
                request
        );

        assertEquals(404, response.status());
        assertEquals("Not Found", response.error());
        assertEquals("Asset not found", response.message());
        assertEquals("/api/assets/99", response.path());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldReturnValidationResponse() throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/assets");

        TestValidationRequest testValidationRequest = new TestValidationRequest("");
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(testValidationRequest, "createAssetRequest");
        bindingResult.addError(new FieldError(
                "createAssetRequest",
                "name",
                "",
                false,
                null,
                null,
                "name is required"
        ));

        Method method = TestValidationController.class.getDeclaredMethod("create", TestValidationRequest.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ApiErrorResponse response = globalExceptionHandler.handleMethodArgumentNotValidException(
                exception,
                request
        );

        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Validation failed", response.message());
        assertEquals("/api/assets", response.path());
        assertEquals(1, response.details().size());
        assertEquals("name: name is required", response.details().getFirst());
    }

    @Test
    void shouldReturnMalformedBodyResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/assets");
        HttpInputMessage inputMessage = new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public HttpHeaders getHeaders() {
                return HttpHeaders.EMPTY;
            }
        };

        ApiErrorResponse response = globalExceptionHandler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("Malformed JSON", inputMessage),
                request
        );

        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Request body is missing or malformed", response.message());
        assertEquals("/api/assets", response.path());
        assertTrue(response.details().isEmpty());
    }

    static class TestValidationController {

        void create(@Valid TestValidationRequest request) {
        }
    }

    record TestValidationRequest(
            @NotBlank(message = "name is required")
            String name
    ) {
    }
}
