package com.orio.book_processing.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

class WebConfigTest {

    @Test
    void corsFilter_isCreated() {
        WebConfig cfg = new WebConfig();
        ReflectionTestUtils.setField(cfg, "allowedOrigins", new String[] { "http://localhost:5173" });
        CorsFilter filter = cfg.corsFilter();
        assertNotNull(filter);
    }

    @Test
    void corsFilter_registersGlobalMappingWithConfiguredOriginsCredentialsAndWildcardMethods() {
        WebConfig cfg = new WebConfig();
        ReflectionTestUtils.setField(cfg, "allowedOrigins",
                new String[] { "http://localhost:5173", "https://app.example" });
        CorsFilter filter = cfg.corsFilter();

        CorsConfigurationSource source = (CorsConfigurationSource) ReflectionTestUtils.getField(filter,
                "configSource");
        assertNotNull(source);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/any/path");
        CorsConfiguration cors = source.getCorsConfiguration(request);
        assertNotNull(cors);
        assertTrue(cors.getAllowCredentials());
        assertTrue(cors.getAllowedOriginPatterns().contains("http://localhost:5173"));
        assertTrue(cors.getAllowedOriginPatterns().contains("https://app.example"));
        assertEquals(1, cors.getAllowedMethods().size());
        assertEquals("*", cors.getAllowedMethods().iterator().next());
        assertEquals(1, cors.getAllowedHeaders().size());
        assertEquals("*", cors.getAllowedHeaders().iterator().next());
    }
}
