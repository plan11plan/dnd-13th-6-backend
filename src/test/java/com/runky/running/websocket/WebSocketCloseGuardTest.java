package com.runky.running.websocket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

/**
 * 개선 후 동작: WebSocketCloseGuardConfig 가 1002 로 끝난 세션의 Tomcat 소켓까지 닫는다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DisconnectEventCollector.class)
@DisplayName("WebSocket 비정상 종료 (개선 후: close-guard)")
class WebSocketCloseGuardTest {

	@LocalServerPort
	int port;

	@Autowired
	DisconnectEventCollector disconnectEvents;

	@BeforeEach
	void setUp() {
		disconnectEvents.clear();
	}

	@Test
	@DisplayName("Pending 클라이언트가 1002 로 끊기면 Tomcat 도 TCP 연결을 닫는다")
	void pendingClientConnectionIsClosed() throws Exception {
		try (RawStompClient client = RawStompClient.connect(port)) {
			assertThat(client.awaitCloseFrameFromServer()).isEqualTo(1002);
			assertThat(disconnectEvents.awaitCloseCode(Duration.ofSeconds(30))).isEqualTo(1002);

			assertThat(client.serverClosesConnectionWithin(Duration.ofSeconds(3))).isTrue();
		}
	}
}
