package com.runky.running.config;

import java.util.Optional;
import java.util.Set;

import org.apache.tomcat.websocket.WsSession;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;
import org.springframework.web.socket.handler.WebSocketSessionDecorator;

import jakarta.websocket.CloseReason;

/**
 * 비정상 종료(1001, 1002, 1006) 때 Tomcat 소켓까지 닫는 데코레이터.
 *
 * 서버가 먼저 close(1002)를 보내면 Tomcat WsSession.doClose(..., closeSocket=false)가 클라이언트의 close 응답을 기다리며
 * 소켓을 열어 둔다. afterConnectionClosed 에서 WsSession.onClose()를 직접 불러 "응답을 받은 것처럼" 상태를 넘기면
 * doClose 가 소켓을 닫는다.
 */
public class CloseGuardWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

	private static final Set<Integer> ABNORMAL_CLOSE_CODES = Set.of(
		CloseStatus.GOING_AWAY.getCode(),     // 1001
		CloseStatus.PROTOCOL_ERROR.getCode(), // 1002: 하트비트 타임아웃
		CloseStatus.NO_CLOSE_FRAME.getCode()  // 1006
	);

	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {
		return new WebSocketHandlerDecorator(handler) {
			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
				if (ABNORMAL_CLOSE_CODES.contains(closeStatus.getCode())) {
					close(session);
				}
				super.afterConnectionClosed(session, closeStatus);
			}
		};
	}

	private static void close(WebSocketSession session) {
		getWsSession(session).ifPresent(wsSession ->
			wsSession.onClose(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "")));
	}

	private static Optional<WsSession> getWsSession(WebSocketSession session) {
		WebSocketSession target = WebSocketSessionDecorator.unwrap(session);
		if (target instanceof StandardWebSocketSession standardSession
			&& standardSession.getNativeSession() instanceof WsSession wsSession) {
			return Optional.of(wsSession);
		}
		return Optional.empty();
	}
}
