package com.runky.running.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * STOMP 세션이 정상(1000)이 아닌 코드로 끝나면 close code를 남긴다.
 * 1001(GOING_AWAY), 1002(PROTOCOL_ERROR, 하트비트 타임아웃), 1006(NO_CLOSE_FRAME)이 얼마나 생기는지 본다.
 */
@Slf4j
@Component
public class WebSocketDisconnectListener {

	@EventListener
	public void onSessionDisconnect(SessionDisconnectEvent event) {
		CloseStatus status = event.getCloseStatus();
		if (status.getCode() == CloseStatus.NORMAL.getCode()) {
			return;
		}
		log.warn("WS abnormal disconnect sessionId={} code={} reason={}",
			event.getSessionId(), status.getCode(), status.getReason());
	}
}
