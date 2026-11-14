package com.runky.running.websocket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;


/**
 * 개선 전 동작(close-guard 끔): 비정상 종료 사례를 모으고, 응답하지 않는 클라이언트로 누수를 재현한다.
 * 실제 Tomcat 을 띄우고, 소켓을 직접 다루는 클라이언트로 raw /ws/websocket 에 붙는다.
 * Tomcat 10.1.19+ 는 응답 없는 close 를 30초 뒤 정리하므로, 그보다 짧은 10초 안에 닫히지 않는 것을 확인한다.
 */
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = "runky.websocket.close-guard.enabled=false")
@Import(DisconnectEventCollector.class)
@DisplayName("WebSocket 비정상 종료")
class WebSocketAbnormalCloseTest {

	private static final Duration HEARTBEAT_TIMEOUT = Duration.ofSeconds(30);

	@LocalServerPort
	int port;

	@Autowired
	DisconnectEventCollector disconnectEvents;

	@Autowired
	@Qualifier("subProtocolWebSocketHandler")
	WebSocketHandler webSocketHandler;

	@BeforeEach
	void setUp() {
		disconnectEvents.clear();
	}

	@Nested
	@DisplayName("SessionDisconnectEvent 로 close code 를 수집하면")
	class CollectCloseCodes {
		@Test
		@DisplayName("정상 종료(DISCONNECT 후 close)는 1000")
		void normalClose() throws Exception {
			try (RawStompClient client = RawStompClient.connect(port)) {
				client.disconnectNormally();

				assertThat(disconnectEvents.awaitCloseCode(Duration.ofSeconds(5))).isEqualTo(1000);
			}
		}
		@Test
		@DisplayName("앱이 화면에서 나가며 닫으면 1001")
		void goingAway() throws Exception {
			try (RawStompClient client = RawStompClient.connect(port)) {
				client.sendClose(1001);

				assertThat(disconnectEvents.awaitCloseCode(Duration.ofSeconds(5))).isEqualTo(1001);
			}
		}
		@Test
		@DisplayName("네트워크가 끊겨 close 프레임 없이 TCP 만 끊기면 1006")
		void networkDropped() throws Exception {
			RawStompClient client = RawStompClient.connect(port);
			client.dropConnection();

			assertThat(disconnectEvents.awaitCloseCode(Duration.ofSeconds(5))).isEqualTo(1006);
		}
		@Test
		@DisplayName("앱이 멈춰 하트비트가 끊기면 서버가 1002 로 닫는다")
		void heartbeatTimeout() throws Exception {
			try (RawStompClient client = RawStompClient.connect(port)) {
				// 하트비트를 보내지 않는다

				assertThat(client.awaitCloseFrameFromServer()).isEqualTo(1002);
				assertThat(disconnectEvents.awaitCloseCode(HEARTBEAT_TIMEOUT)).isEqualTo(1002);
			}
		}
	}

	@Nested
	@DisplayName("하트비트를 보내지 않고 close 에도 응답하지 않는 클라이언트(Pending)는")
	class PendingClient {

		@Test
		@DisplayName("STOMP 세션은 정리되지만, Tomcat 은 TCP 연결을 닫지 않는다")
		void stompSessionClosedButTcpConnectionRemains() throws Exception {
			try (RawStompClient client = RawStompClient.connect(port)) {
				int closeCodeFromServer = client.awaitCloseFrameFromServer();

				// STOMP 세션은 정리된다
				assertThat(closeCodeFromServer).isEqualTo(1002);
				assertThat(disconnectEvents.awaitCloseCode(HEARTBEAT_TIMEOUT)).isEqualTo(1002);
				assertThat(activeStompSessions()).isZero();

				// Tomcat 은 클라이언트의 close 응답을 기다리며 TCP 연결을 열어 둔다
				assertThat(client.serverClosesConnectionWithin(Duration.ofSeconds(10))).isFalse();
			}
		}
	}

	private int activeStompSessions() {
		SubProtocolWebSocketHandler handler =
			(SubProtocolWebSocketHandler)WebSocketHandlerDecorator.unwrap(webSocketHandler);
		return handler.getStats().getWebSocketSessions();
	}
}
