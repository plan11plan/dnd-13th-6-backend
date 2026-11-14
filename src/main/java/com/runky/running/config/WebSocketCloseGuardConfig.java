package com.runky.running.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * CloseGuardWebSocketHandlerDecoratorFactory 를 등록한다.
 * 개선 전후 비교용으로 runky.websocket.close-guard.enabled=false 로 끌 수 있다.
 */
@Configuration
@ConditionalOnProperty(name = "runky.websocket.close-guard.enabled", havingValue = "true", matchIfMissing = true)
public class WebSocketCloseGuardConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.addDecoratorFactory(new CloseGuardWebSocketHandlerDecoratorFactory());
	}
}
