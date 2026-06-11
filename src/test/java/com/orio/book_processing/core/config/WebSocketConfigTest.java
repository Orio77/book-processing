package com.orio.book_processing.core.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

class WebSocketConfigTest {

    @Test
    void registerStompEndpoints_allowsConfiguredOrigins() {
        WebSocketConfig cfg = new WebSocketConfig();
        String[] origins = new String[] { "http://a", "http://b" };
        ReflectionTestUtils.setField(cfg, "allowedOrigins", origins);

        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration reg = mock(StompWebSocketEndpointRegistration.class);
        SockJsServiceRegistration sockJs = mock(SockJsServiceRegistration.class);
        when(registry.addEndpoint("/ws")).thenReturn(reg);
        when(reg.setAllowedOriginPatterns(any(String[].class))).thenReturn(reg);
        when(reg.withSockJS()).thenReturn(sockJs);

        cfg.registerStompEndpoints(registry);

        verify(registry).addEndpoint("/ws");
        ArgumentCaptor<String[]> originCaptor = ArgumentCaptor.forClass(String[].class);
        verify(reg).setAllowedOriginPatterns(originCaptor.capture());
        assertArrayEquals(origins, originCaptor.getValue());
        verify(reg).withSockJS();
    }

    @Test
    void configureMessageBroker_enablesTopicBrokerAndAppPrefix() {
        WebSocketConfig cfg = new WebSocketConfig();
        ReflectionTestUtils.setField(cfg, "allowedOrigins", new String[] { "*" });
        MessageBrokerRegistry broker = mock(MessageBrokerRegistry.class);

        cfg.configureMessageBroker(broker);

        verify(broker).enableSimpleBroker("/topic");
        verify(broker).setApplicationDestinationPrefixes("/app");
    }
}
