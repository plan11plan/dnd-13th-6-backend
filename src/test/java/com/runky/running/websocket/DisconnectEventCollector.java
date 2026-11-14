package com.runky.running.websocket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/** 테스트 중 발생한 SessionDisconnectEvent 의 close code 를 모은다. */
@TestComponent
public class DisconnectEventCollector {

	private final BlockingQueue<CloseStatus> statuses = new LinkedBlockingQueue<>();

	@EventListener
	public void onSessionDisconnect(SessionDisconnectEvent event) {
		statuses.add(event.getCloseStatus());
	}

	public void clear() {
		statuses.clear();
	}

	public int awaitCloseCode(Duration timeout) throws InterruptedException {
		CloseStatus status = statuses.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
		assertThat(status).as("SessionDisconnectEvent 를 받아야 한다").isNotNull();
		return status.getCode();
	}
}
