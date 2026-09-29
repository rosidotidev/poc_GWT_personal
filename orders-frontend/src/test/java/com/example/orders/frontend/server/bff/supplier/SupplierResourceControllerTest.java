package com.example.orders.frontend.server.bff.supplier;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class SupplierResourceControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private SupplierBffService supplierService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        supplierService = mock(SupplierBffService.class);
        MappingJackson2HttpMessageConverter json = new MappingJackson2HttpMessageConverter(objectMapper);
        mockMvc = standaloneSetup(new SupplierResourceController(supplierService))
                .setControllerAdvice(new SupplierExceptionHandler())
                .setMessageConverters(json)
                .build();
    }

    @Test
    void createReturns201LocationAndNormalizedRepresentation() throws Exception {
        SupplierWriteRequest request = new SupplierWriteRequest("ACME", "Acme Supply", "Jo Example",
                "jo@example.test", "555-0100");
        when(supplierService.create(request)).thenReturn(supplier(42, "ACME", "Acme Supply"));

        mockMvc.perform(post("/v1/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/v1/suppliers/42")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getReturnsSupplierRepresentation() throws Exception {
        when(supplierService.get(42)).thenReturn(supplier(42, "ACME", "Acme Supply"));

        mockMvc.perform(get("/v1/suppliers/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ACME"));
    }

    @Test
    void putUsesPathResourceAndReturnsUpdatedRepresentation() throws Exception {
        SupplierWriteRequest request = new SupplierWriteRequest("ACME-2", "Acme Supply Updated", "Jo Example",
                "jo@example.test", "555-0100");
        when(supplierService.update(42, request)).thenReturn(supplier(42, "ACME-2", "Acme Supply Updated"));

        mockMvc.perform(put("/v1/suppliers/42").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.code").value("ACME-2"));
    }

    @Test
    void mapsNotFoundConflictAndValidationToDistinctStatuses() throws Exception {
        when(supplierService.get(404)).thenThrow(new SupplierBffExceptions.NotFound(404));
        when(supplierService.create(new SupplierWriteRequest("DUP", "Duplicate", "", "", "")))
                .thenThrow(new SupplierBffExceptions.Conflict("DUP"));
        when(supplierService.create(new SupplierWriteRequest("", "", "", "", "")))
                .thenThrow(new SupplierBffExceptions.InvalidInput(Map.of("code", "is required")));

        mockMvc.perform(get("/v1/suppliers/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("supplier_not_found"));
        mockMvc.perform(post("/v1/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"DUP\",\"name\":\"Duplicate\",\"contactName\":\"\",\"email\":\"\",\"phone\":\"\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("supplier_code_conflict"));
        mockMvc.perform(post("/v1/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"\",\"name\":\"\",\"contactName\":\"\",\"email\":\"\",\"phone\":\"\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.code").value("is required"));
    }

    @Test
    void rejectsMalformedUnknownAndUnsupportedRequestsAsProblemJson() throws Exception {
        mockMvc.perform(post("/v1/suppliers").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"));
        mockMvc.perform(post("/v1/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"X\",\"name\":\"X\",\"id\":99}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("malformed_request"));
        mockMvc.perform(post("/v1/suppliers").contentType(MediaType.TEXT_PLAIN).content("supplier"))
                .andExpect(status().isUnsupportedMediaType());
        mockMvc.perform(patch("/v1/suppliers/42").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void mapsDownstreamFailureTo502() throws Exception {
        when(supplierService.get(42)).thenThrow(new SupplierBffExceptions.DownstreamUnavailable(
                new IllegalStateException("SOAP unavailable")));

        mockMvc.perform(get("/v1/suppliers/42"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("supplier_service_unavailable"));
    }

    private SupplierRepresentation supplier(long id, String code, String name) {
        return new SupplierRepresentation(id, code, name, "Jo Example", "jo@example.test", "555-0100", true);
    }
}