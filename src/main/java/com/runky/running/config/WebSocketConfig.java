package com.runky.running.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private TaskScheduler messageBrokerTaskScheduler;

    @Autowired
    public void setMessageBrokerTaskScheduler(
            @Lazy @Qualifier("messageBrokerTaskScheduler") TaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
        long[] heartbeats = {5000, 5000};
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(heartbeats)
                .setTaskScheduler(messageBrokerTaskScheduler);

		registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지를 보낼 때 접두사
		registry.setUserDestinationPrefix("/user");

	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.addEndpoint("/ws")
			.withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		// 인터셉터 등록
		registration.interceptors();
	}
}
